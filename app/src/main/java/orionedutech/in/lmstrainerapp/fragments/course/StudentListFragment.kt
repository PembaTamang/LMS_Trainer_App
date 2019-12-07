package orionedutech.`in`.lmstrainerapp.fragments.course


import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.attendance_alert.view.*
import kotlinx.android.synthetic.main.fragment_student_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.activities.MainCourseActivity
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
    lateinit var animation: LottieAnimationView

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
    var allAbsent = false
    var trainingID = ""
    var chapterType = ""
    var courseName = ""
    lateinit var json: JSONObject

    companion object {
        var disable = false
    }

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
        animation = view.anim

        val bundle = arguments!!

        selectedBatchID = bundle.getString("batch_id", "")
        selectedCourseID = bundle.getString("course_id", "")
        selectedModuleID = bundle.getString("module_id", "")
        selectedChapterID = bundle.getString("chapter_id", "")
        selectedUnitID = bundle.getString("unit_id", "")
        selectedSubUnitID = bundle.getString("subunit_id", "")
        trainerID = bundle.getString("user_id", "")
        courseName = bundle.getString("course_name","")
        uniqueID = UUID.randomUUID().toString()

        val uniquePrefs = activity!!.getSharedPreferences("uid", Context.MODE_PRIVATE)
        uniquePrefs.edit().putString(uniqueID, uniqueID).apply()

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
            json.put("students_present", a)
            json.put("students_absent", b)

            mLog.i(TAG, "json $json")

            animation.visibility = VISIBLE
            animation.playAnimation()
            markAttendance.text = ""
            NetworkOps.post(
                Urls.studentAttendanceSubmitUrl,
                json.toString(),
                context,
                object : response {
                    override fun onInternetfailure() {
                        activity!!.runOnUiThread {
                            noInternetSnackBar(activity!!)
                        }
                    }

                    @SuppressLint("ClickableViewAccessibility")
                    override fun onrespose(string: String?) {
                        mLog.i(TAG, "attendance response: $string")
                        if (string.isNullOrEmpty()) {
                            onfailure()
                        } else {
                            val json = JSONObject(string)
                            if (json.getString("success") == "1") {
                                val absent = json.getString("total_absent")
                                val trainingID = json.getString("training_id")
                                chapterType = json.getString("chapter_link_type")
                                mLog.i(TAG,"chapter link type $chapterType")
                                allAbsent = absent.toInt() == arrayList.size
                                activity!!.runOnUiThread {
                                    animation.visibility = GONE
                                    animation.cancelAnimation()
                                    markAttendance.visibility = GONE
                                    recyclerView.isEnabled = false
                                    allPresent.isEnabled = false
                                    disable = true
                                    recyclerView.alpha = 0.5f
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        showSuccessAlert(uniqueID, trainingID)
                                    }, 500)
                                }
                            } else {
                                onfailure()
                            }
                        }
                    }

                    override fun onfailure() {
                        activity!!.runOnUiThread {

                            animation.visibility = GONE
                            animation.cancelAnimation()
                            showToast(context, "submission failed")
                            markAttendance.text = "Mark Attendance"

                        }
                    }

                }) { _, _, _ ->

            }


        }

        return view
    }

    private fun showSuccessAlert(uniqueID: String, trainingID: String) {
        val view = LayoutInflater.from(context).inflate(R.layout.attendance_alert, null, false)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(view)
        builder.setCancelable(false)
        val dialogue = builder.create()
        val materialButton = view.startClass
        val animation = view.imageView10
        if (allAbsent) {
            materialButton.text = "dismiss class : 0 present"
        }
        materialButton.setOnClickListener {
            dialogue.dismiss()
            if (allAbsent) {
                activity!!.supportFragmentManager.popBackStack()
                return@setOnClickListener
            }

            //start class here
            val intent = Intent(context, MainCourseActivity::class.java)
            intent.putExtra("trainerID", trainerID)
            intent.putExtra("courseID", selectedCourseID)
            intent.putExtra("moduleID", selectedModuleID)
            intent.putExtra("chapterID", selectedChapterID)
            intent.putExtra("batchID", selectedBatchID)
            intent.putExtra("unitID", selectedUnitID)
            intent.putExtra("subunitID", selectedSubUnitID)
            intent.putExtra("trainingID", trainingID)
            intent.putExtra("uniqueID", uniqueID)
            intent.putExtra("chapter_type",chapterType)
            intent.putExtra("course_name",courseName)
            startActivity(intent)
            activity!!.supportFragmentManager.popBackStack()
            activity!!.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        dialogue.show()
        Handler(Looper.getMainLooper()).postDelayed({
            animation.visibility = VISIBLE
            animation.playAnimation()
            animation.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    materialButton.isEnabled = true
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })
        }, 500)


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
