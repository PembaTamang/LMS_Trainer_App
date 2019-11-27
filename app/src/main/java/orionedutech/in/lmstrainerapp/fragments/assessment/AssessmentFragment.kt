package orionedutech.`in`.lmstrainerapp.fragments.assessment


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_trainer_assessment.view.*
import kotlinx.android.synthetic.main.fragment_trainer_assessment.view.batch_spinner
import kotlinx.android.synthetic.main.fragment_trainer_assessment_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.activities.AssessmentActivity
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.AssessmentAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.AssessmentMainData
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCAssessment
import orionedutech.`in`.lmstrainerapp.network.response
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCAssessmentList
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */
class AssessmentFragment : BaseFragment(), RecyclerItemClick {
    private val arrayList = ArrayList<DCAssessmentList>()
    private lateinit var batchSpinner: Spinner
    private lateinit var batchAdapter: BatchSpinAdapter
    lateinit var selectedBatchID: String
    lateinit var centerID : String
    var batchListSpinner = ArrayList<Batch>()
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var adapter: AssessmentAdapter
    var userID = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_trainer_assessment, container, false)

        view.upload.setOnClickListener {
            //upload code here
            val ft = activity?.supportFragmentManager!!.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            val fragment =
                AssessmentUploadFragment()
            ft.add(R.id.mainContainer, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            AssessmentAdapter(
                arrayList,
                this
            )
        recyclerView.adapter = adapter


        batchSpinner = view.batch_spinner
        batchAdapter = BatchSpinAdapter(
            context!!,
            android.R.layout.simple_list_item_1,
            batchListSpinner
            , 0
        )
        batchSpinner.adapter = batchAdapter
        batchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val batch = batchListSpinner[p2]
                selectedBatchID = batch.batch_id.toString()
            }

        }
        CoroutineScope(Dispatchers.IO).launch {
            context?.let {
                val dao = MDatabase(it).getBatchDao()
                if (dao.batchDataExists()) {
                    batchListSpinner.addAll(dao.getAllBatches())
                    withContext(Dispatchers.Main) {
                        batchAdapter.notifyDataSetChanged()
                    }
                }
            }

        }

        view.button.setOnClickListener {
            launch {
                context?.let {
                    val dao = MDatabase(it).getUserDao()
                    userID = dao.getUserID()
                    val batchID = selectedBatchID
                    centerID = dao.getCenterID()
                    mLog.i(TAG, " userID $userID batchID $batchID centerID $centerID")
                    val json = JSONObject()
                    json.put("user_id", userID)
                    json.put("user_center_id", centerID)
                    json.put("batch_id", batchID)
                    getAssessmentData(json.toString())
                    recyclerView.showShimmerAdapter()
                }
            }
        }

        val ascendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingNames = AtomicBoolean(true)


        view.name.setOnClickListener {
            if (ascendingNames.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                view.name.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingName, null)
                ascendingNames.set(false)
                //sort by descending names
                val templist =
                    arrayList.sortedWith(compareByDescending(DCAssessmentList::assesment_name))
                arrayList.clear()
                arrayList.addAll(templist)
                adapter.notifyDataSetChanged()

            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                view.name.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingName, null)
                ascendingNames.set(true)
                //sort by ascending names
                val templist = arrayList.sortedWith(compareBy(DCAssessmentList::assesment_name))
                arrayList.clear()
                arrayList.addAll(templist)
                adapter.notifyDataSetChanged()
            }
        }

        val ascendingType =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingType =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingTypes = AtomicBoolean(true)
        view.type.setOnClickListener {
            if (ascendingTypes.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingType, "level", 0, 10000).setDuration(500)
                animator.start()
                view.type.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingType, null)
                ascendingTypes.set(false)
                //sort by descending types


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingType, "level", 0, 10000).setDuration(500)
                animator.start()
                view.type.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingType, null)
                ascendingTypes.set(true)
                //sort by ascending types


            }
        }

        val ascendingCenter =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingCenter =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingCenters = AtomicBoolean(true)
        view.centers.setOnClickListener {
            if (ascendingCenters.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                view.centers.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ascendingCenter,
                    null
                )
                ascendingCenters.set(false)
                //sort by descending centers
            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                view.centers.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descendingCenter,
                    null
                )
                ascendingCenters.set(true)
                //sort by ascending centers
            }
        }


        return view
    }

    fun getAssessmentData(json: String) {
        NetworkOps.post(Urls.assessmentUrl, json, context, object : response {
            var nodata = false
            override fun onrespose(string: String?) {
                val assessment = Gson().fromJson(string, DCAssessment::class.java)
                if (assessment == null) {
                    runfailureCode()
                    return
                }
                if (assessment.success == "1") {
                    val assessmentList = assessment.assesments
                    if (assessmentList.isEmpty()) {
                        nodata = true
                    }
                    arrayList.clear()
                    arrayList.addAll(assessmentList)
                    activity!!.runOnUiThread {
                        if (nodata) {
                            showToast(context, "There is no data to display")
                        }
                        adapter.notifyDataSetChanged()
                        recyclerView.hideShimmerAdapter()
                    }
                } else {
                    runfailureCode()
                }
            }

            override fun onfailure() {
                runfailureCode()
            }

            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                }
                noInternetSnackBar(activity)
            }

        }) { _, _, _ ->

        }
    }

    private fun runfailureCode() {
        showToast(context, "failed")
        activity!!.runOnUiThread {
            recyclerView.hideShimmerAdapter()
        }
    }

    override fun click(itempos: Int) {
        // get assessment here

        mLog.i(TAG, " id ${arrayList[itempos].assesment_id}")

        MaterialAlertDialogBuilder(context).setTitle("Alert")
            .setMessage("Do you want to give the assessment for ${arrayList[itempos].assesment_name} ? ")
            .setPositiveButton("proceed"){dialogInterface, i ->
                dialogInterface.dismiss()
                val intent = Intent(context,AssessmentActivity::class.java)
                intent.putExtra("assessmentID",arrayList[itempos].assesment_id)
                intent.putExtra("uid",userID)
                intent.putExtra("batch_id",selectedBatchID)
                intent.putExtra("center_id",centerID)
                startActivity(intent)
            }
            .setNegativeButton("cancel"){dialogInterface, i ->
                dialogInterface.dismiss()
            }.create().show()
    }

}
