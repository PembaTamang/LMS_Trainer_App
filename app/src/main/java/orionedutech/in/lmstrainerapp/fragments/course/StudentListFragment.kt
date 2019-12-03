package orionedutech.`in`.lmstrainerapp.fragments.course


import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_student_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.NewAttendanceAdapter
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.model.AttendanceModel
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCStudentAttendanceData
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class StudentListFragment : Fragment() {
    lateinit var markAttendance: MaterialButton
    var arrayList: ArrayList<AttendanceModel> = ArrayList()
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var attendanceAdapter: NewAttendanceAdapter
    lateinit var animation : LottieAnimationView

    lateinit var allPresent: TextView
    var allPrsnt = true

    var selectedBatchID = ""
    var selectedCourseID = ""
    var selectedModuleID = ""
    var selectedChapterID = ""
    var selectedUnitID = ""
    var selectedSubUnitID = ""
    var trainerID = ""
    var uniqueID = ""


    lateinit var json: JSONObject
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_list, container, false)
        allPresent = view.present
        recyclerView = view.shimmerRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        attendanceAdapter = NewAttendanceAdapter(arrayList)
        recyclerView.adapter = attendanceAdapter
        markAttendance = view.markAttendance
        animation = view.batchAnimation

        val bundle = arguments!!

        selectedBatchID = bundle.getString("batch_id", "")
        selectedCourseID = bundle.getString("course_id", "")
        selectedModuleID = bundle.getString("module_id", "")
        selectedChapterID = bundle.getString("chapter_id", "")
        selectedUnitID = bundle.getString("unit_id", "")
        selectedSubUnitID = bundle.getString("subunit_id", "")
        trainerID = bundle.getString("user_id", "")
        uniqueID = UUID.randomUUID().toString()

        json = JSONObject()
        json.put("trainer_id", trainerID)
        json.put("batch_id", selectedBatchID)
        json.put("course_id", selectedCourseID)
        json.put("chapter_id", selectedChapterID)
        json.put("module_id", selectedModuleID)
        json.put("unit_id", selectedUnitID)
        json.put("subunit_id", selectedSubUnitID)
        json.put("uniqid", uniqueID)


        val rotatingCheck =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_green) }


        val rotatingCheck1 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_red) }
        allPresent.setOnClickListener {
            if (allPrsnt) {
                for (m in arrayList) {
                    m.status = "absent"
                }
                allPrsnt = false
                val animator =
                    ObjectAnimator.ofInt(rotatingCheck1, "level", 0, 10000).setDuration(500)
                animator.start()
                allPresent.text = "All Absent"
                allPresent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck1, null)

            } else {

                for (m in arrayList) {
                    m.status = "present"
                }

                allPrsnt = true
                val animator =
                    ObjectAnimator.ofInt(rotatingCheck, "level", 0, 10000).setDuration(500)
                animator.start()
                allPresent.text = "All Present"
                allPresent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck, null)

            }
            //adapter.clearSparseArray()
            attendanceAdapter.notifyDataSetChanged()
        }



        CoroutineScope(IO).launch {
            Handler(Looper.getMainLooper()).postDelayed({
                getStudentList(selectedBatchID)
            }, 700)
        }

        markAttendance.setOnClickListener {

            val allIDs = ArrayList<String>()
            val absentIDs = ArrayList<String>()

            arrayList.forEach {
                if (it.status == "absent") {
                    absentIDs.add(it.id!!)
                }
                allIDs.add(it.id!!)
            }
            val a = TextUtils.join(",", allIDs)
            val b = TextUtils.join(",", absentIDs)
            json.put("students_present",a)
            json.put("students_absent",b)

            mLog.i(TAG,"json $json")

          /*  NetworkOps.post(Urls.studentAttendanceSubmitUrl,json.toString(),context,object : response{
                override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity!!)
                }
               }

                override fun onrespose(string: String?) {
                mLog.i(TAG,"response $string")

                }

                override fun onfailure() {
                  activity!!.runOnUiThread {
                      showToast(context,"submission failed")
                  }
                }

            }){ _, _, _ ->

            }*/
        }




        return view
    }

    private fun getStudentList(selectedBatchID: String?) {
        val json = JSONObject()
        json.put("batch_id", selectedBatchID)
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.studentListUrl, json.toString(), context, object : response {
            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "server response $string")
                val studentData = Gson().fromJson(string, DCStudentAttendanceData::class.java)
                if (studentData == null) {
                    onfailure()
                    return
                }
                if (studentData.success == "1") {
                    val studentList = studentData.students
                    if (studentList.isNotEmpty()) {
                        arrayList.clear()
                        studentList.forEach {
                            val student = AttendanceModel(it.student_name, it.student_id, "present")
                            arrayList.add(student)
                        }
                        activity!!.runOnUiThread {
                            mLog.i(TAG, "student count ${arrayList.size}")
                            recyclerView.hideShimmerAdapter()
                            markAttendance.isEnabled = true
                            attendanceAdapter.notifyDataSetChanged()

                        }
                    } else {
                        activity!!.runOnUiThread {
                            showToast(context, "student list is empty")
                            activity!!.supportFragmentManager.popBackStack()
                        }
                    }
                } else {
                    onfailure()
                }
            }

            override fun onfailure() {
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    showToast(context, "failed to get data")
                }
            }

        }) { _, _, _ ->
        }
    }


}
