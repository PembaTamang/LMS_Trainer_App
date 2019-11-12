package orionedutech.`in`.lmstrainerapp.fragments


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.fragment_feedback.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.CourseSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.dao.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCCourse
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatches
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCCourseList
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * A simple [Fragment] subclass.
 */
class FeedbackFragment : BaseFragment() {

    var courseListSpinner = ArrayList<DCCourse>()
    lateinit var courseAdapter: CourseSpinAdapter

    var batchListSpinner = ArrayList<Batch>()
    lateinit var batchAdapter: BatchSpinAdapter

    lateinit var selectedBatchID: String

    private lateinit var button: MaterialButton
    private var busy: Boolean = false
    lateinit var ft: FragmentTransaction
    var json: JSONObject = JSONObject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)

        courseAdapter = CourseSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            courseListSpinner
        )

        view.course_spinner.adapter = courseAdapter

        batchAdapter = BatchSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            batchListSpinner
        ,0)
        view.batch_spinner.adapter = batchAdapter

        view.batch_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val batch = batchListSpinner[p2]
                selectedBatchID = batch.batch_id.toString()
                CoroutineScope(IO).launch {
                    getCourseData(selectedBatchID)
                }
            }

        }


        CoroutineScope(IO).launch {
            context?.let {
                val dao = MDatabase(it).getBatchDao()
                if (dao.batchDataExists()) {
                    batchListSpinner.addAll(dao.getAllBatches())
                    withContext(Main) {
                        batchAdapter.notifyDataSetChanged()
                    }
                } else {
                    showAnimation()
                    hideAnimations(getBatchData())
                }
            }

        }


        button = view.feedback
        button.setOnClickListener {
            if (button.text == "retry") {
                CoroutineScope(IO).launch {
                    getBatchData()
                }
            } else {
                if (!busy) {
                    ft = activity?.supportFragmentManager!!.beginTransaction()
                    ft.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    ft.add(R.id.mainContainer, FeedbackListFragment())
                    ft.addToBackStack(null)
                    ft.commit()
                } else {
                    showToast("Please wait for downloads to complete")
                }
            }
        }
        return view
    }

    private fun hideAnimations(b: Boolean) {
        busy = false
        mLog.i(TAG, "hiding : $b ")
        activity?.runOnUiThread {
            if (b) {
                button.text = "give feedback"
            } else {
                button.text = "retry"
            }
            batchAnimation.visibility = View.GONE
            batchAnimation.cancelAnimation()
        }
    }

    private fun hideAnimations() {
        busy = false
        mLog.i(TAG, "hiding ")
        activity?.runOnUiThread {
            button.text = "give feedback"
            batchAnimation.visibility = View.GONE
            batchAnimation.cancelAnimation()
        }

    }

    private fun showAnimation() {
        busy = true
        mLog.i(TAG, "showing")
        activity?.runOnUiThread {
            button.text = ""
            batchAnimation.visibility = View.VISIBLE
            batchAnimation.playAnimation()
        }

    }


    suspend fun getCourseData(batchId: String?) {
        showAnimation()
        val json = JSONObject()
        json.put("batch_id", batchId)
        launch {
            context?.let {
                NetworkOps.post(
                    Urls.courseUrl,
                    json.toString(),
                    context,
                    object : response {
                        override fun onInternetfailure() {
                            mToast.noInternetSnackBar(activity!!)
                        }

                        override fun onrespose(string: String?) {
                            val courses = Gson().fromJson(string, DCCourseList::class.java)
                            if (courses.success == "1") {

                                val courseList = courses.courses
                                if (courseList.isNotEmpty()) {
                                    courseListSpinner.clear()
                                    courseListSpinner.addAll(courseList)
                                    CoroutineScope(Main).launch {
                                        courseAdapter.notifyDataSetChanged()
                                        hideAnimations()
                                    }
                                } else {
                                    val dao1 = MDatabase(it).getBatchDao()
                                    CoroutineScope(IO).launch {
                                        showToast(
                                            "There is no course for ${dao1.getBatchName(
                                                batchId.toString()
                                            )}"
                                        )
                                        hideAnimations()
                                    }


                                }
                            } else {
                                failedCourse()
                            }
                        }

                        override fun onfailure() {
                            failedCourse()
                        }

                    }) { _, _, _ -> }
            }
        }
    }

    private fun failedCourse() {
        busy = false
        CoroutineScope(Main).launch {
            showToast("failed to get data")
            button.text = "retry"
            batchAnimation.visibility = View.GONE
            batchAnimation.cancelAnimation()
        }
    }


    suspend fun getBatchData(): Boolean {
        var a = false
        val countDownLatch = CountDownLatch(1)
        //  showAnimation()
        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val trainerID = dao.getUserID()
                mLog.i(TAG, "trainer id $trainerID")
                json.put("trainer_id", trainerID)
                NetworkOps.post(
                    Urls.batchUrl,
                    json.toString(),
                    context,
                    object : response {
                        override fun onInternetfailure() {
                        mToast.noInternetSnackBar(activity!!)
                        }

                        override fun onrespose(string: String?) {

                            val batches = Gson().fromJson(string, DCBatches::class.java)
                            if (batches.success == "1") {
                                val batchlist = batches.batches
                                if (batchlist.isNotEmpty()) {
                                    mLog.i(TAG, "list length ${batchlist.size}")
                                    launch {
                                        context?.let { it ->
                                            val dao1 = MDatabase(it).getBatchDao()
                                            dao1.insertBatches(batchlist.toMutableList())
                                        }

                                    }

                                    batchListSpinner.clear()
                                    batchListSpinner.addAll(batchlist)
                                    activity?.runOnUiThread {
                                        batchAdapter.notifyDataSetChanged()
                                        //  hideAnimations()
                                    }

                                } else {
                                    showToast("Error no batches found")
                                    //  hideAnimations()
                                }

                                a = true
                                countDownLatch.countDown()
                            } else {
                                countDownLatch.countDown()

                                // failedBatch()
                            }
                        }

                        override fun onfailure() {
                            countDownLatch.countDown()
                            // failedBatch()


                        }


                    }) { _, _, _ ->

                }

            }
        }

        countDownLatch.await()
        return a
    }

    private fun failedBatch() {
        busy = false
        CoroutineScope(Main).launch {
            showToast("failed to get data")
            button.text = "retry"
            batchAnimation.visibility = View.GONE
            batchAnimation.cancelAnimation()
        }
    }

    private fun showToast(message: String) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null, false)
        val text = view.findViewById<TextView>(R.id.textView)
        text.text = message
        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 50)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
}
