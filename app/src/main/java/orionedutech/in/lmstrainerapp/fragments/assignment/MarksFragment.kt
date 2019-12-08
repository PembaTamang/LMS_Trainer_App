package orionedutech.`in`.lmstrainerapp.fragments.assignment


import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_marks.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.AssignmentAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.adapters.spinners.StudentSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.*
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response


/**
 * A simple [Fragment] subclass.
 */

class MarksFragment : Fragment() {

    lateinit var studentSpinner: Spinner
    lateinit var batchSpinner: Spinner
    lateinit var assignmentSpinner: Spinner

    lateinit var totalMarksET: EditText
    lateinit var studentMarksET: EditText
    lateinit var submit: MaterialButton
    lateinit var progress: LottieAnimationView

    var batchListSpinner = ArrayList<Batch>()
    lateinit var batchAdapter: BatchSpinAdapter
    lateinit var selectedBatchID: String

    var studentSpinnerList = ArrayList<DCStudents>()
    lateinit var studentAdapter: StudentSpinAdapter
    lateinit var selectedStudentID: String

    var assignmentSpinnerList = ArrayList<DCAssignmentListByBatchandStudent>()
    lateinit var assignmentAdapter: AssignmentAdapter
    lateinit var selectedAssignmentID: String

    var studentMarks = ""
    var totalMarks = ""
    var trainerID = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        val view = inflater.inflate(R.layout.fragment_marks, container, false)

        studentSpinner = view.studentSpinner
        batchSpinner = view.batch
        assignmentSpinner = view.assignment_spinner
        totalMarksET = view.total_marks
        studentMarksET = view.student_marks
        submit = view.submit
        progress = view.anim

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
                CoroutineScope(IO).launch {
                    getStudentData(selectedBatchID)
                }
            }

        }
        studentAdapter =
            StudentSpinAdapter(context!!, android.R.layout.simple_list_item_1, studentSpinnerList)
        studentSpinner.adapter = studentAdapter

        studentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val student = studentSpinnerList[p2]
                selectedStudentID = student.user_id
                assignmentSpinnerList.clear()
                assignmentAdapter.notifyDataSetChanged()
                CoroutineScope(IO).launch {
                    getAssignmentData(selectedBatchID, selectedStudentID)
                }
            }
        }

        assignmentAdapter =
            AssignmentAdapter(context!!, android.R.layout.simple_list_item_1, assignmentSpinnerList)
        assignmentSpinner.adapter = assignmentAdapter

        assignmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val assignment = assignmentSpinnerList[p2]
                selectedAssignmentID = assignment.assignment_id
            }

        }


        studentMarksET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                studentMarks = p0.toString()
            }

        })

        totalMarksET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                totalMarks = p0.toString()
            }

        })

        submit.setOnClickListener {

            //{"trainer_id":"11","student_id":"1","assignment_id":"1","total_marks":"1","marks_of_student":"1"}
            if (totalMarks.isBlank() || studentMarks.isBlank()) {
                mToast.showToast(context, "please fill both the marks")
                return@setOnClickListener
            }
            if(selectedStudentID.isBlank()||selectedAssignmentID.isBlank()||selectedBatchID.isBlank()){
                mToast.showToast(context, "please make all the proper choices")
                return@setOnClickListener

            }

            showAnimation()
            if(activity==null){
                return@setOnClickListener
            }
            activity!!.runOnUiThread {

                totalMarksET.isEnabled = false
                studentMarksET.isEnabled = false

            }
            val json = JSONObject()
            json.put("trainer_id", trainerID)
            json.put("student_id", selectedStudentID)
            json.put("assignment_id", selectedAssignmentID)
            json.put("total_marks", totalMarks)
            json.put("marks_of_student", studentMarks)

            NetworkOps.post(
                Urls.assignmentMarksUpload,
                json.toString(),
                context,
                object : response {
                    override fun onInternetfailure() {
                        if(activity==null){
                            return
                        }
                        activity!!.runOnUiThread {
                            mToast.noInternetSnackBar(activity!!)
                        }
                    }

                    override fun onrespose(string: String?) {
                        mLog.i(TAG, "response : $string ")
                        val success = JSONObject(string!!).getString("success")
                        if (activity == null){
                            return
                        }
                        activity!!.runOnUiThread {
                            when (success) {
                                "Marks added successfully" ->{ mToast.showToast(
                                    context,
                                    "marks added successfully"
                                )
                                    activity!!.supportFragmentManager.popBackStack()
                                }

                                "1" -> {
                                    mToast.showToast(context, "marks already added.")
                                    totalMarksET.isEnabled = true
                                    studentMarksET.isEnabled = true
                                    hideAnimation()
                                }

                                else -> mToast.showToast(context, "error")

                            }


                        }

                    }

                    override fun onfailure() {
                        if(activity==null){
                            return
                        }
                        activity!!.runOnUiThread {
                            totalMarksET.isEnabled = true
                            studentMarksET.isEnabled = true
                            hideAnimation()
                            mToast.showToast(context, "submission failed")
                        }
                    }

                }) { _, _, _ ->


            }
        }

        CoroutineScope(IO).launch {
            context?.let {
                val dao = MDatabase(it).getBatchDao()
                val userDao = MDatabase(it).getUserDao()
                trainerID = userDao.getUserID()

                if (dao.batchDataExists()) {
                    batchListSpinner.addAll(dao.getAllBatches())
                    withContext(Dispatchers.Main) {
                        batchAdapter.notifyDataSetChanged()
                    }
                }
            }

        }




        return view
    }

    private fun getAssignmentData(batchID: String, studentID: String) {
        showAnimation()
        val json = JSONObject()
        json.put("batch", batchID)
        json.put("student_id", studentID)
        selectedAssignmentID = ""
        NetworkOps.post(
            Urls.assignmentListByBatchAndStudent,
            json.toString(),
            context,
            object : response {
                override fun onInternetfailure() {
                    if(activity==null){
                        return
                    }
                    activity!!.runOnUiThread {
                        hideAnimation()
                        mToast.noInternetSnackBar(activity)
                    }
                }

                override fun onrespose(string: String?) {
                    mLog.i(TAG, "response: $string")
                    val assignmentData =
                        Gson().fromJson(string, DCAssignmentByBatchAndStudent::class.java)
                    if (assignmentData == null) {
                        if(activity==null){
                            return
                        }
                        activity!!.runOnUiThread {
                            mToast.showToast(context, "no data")
                        }
                        return
                    }
                    if (assignmentData.success == "1") {
                        val assignmentList = assignmentData.assignments

                        if (assignmentList.isNotEmpty()) {
                            assignmentSpinnerList.clear()
                            assignmentSpinnerList.addAll(assignmentList)
                            if(activity==null){
                                return
                            }
                            activity!!.runOnUiThread {
                                assignmentAdapter.notifyDataSetChanged()
                                hideAnimation()
                            }
                        } else {
                            mLog.i(TAG,"empty")
                            onfailure()
                        }

                    } else {
                        onfailure()
                    }

                }

                override fun onfailure() {
                    if(activity==null){
                        return
                    }
                    activity!!.runOnUiThread {
                        hideAnimation()
                        mToast.showToast(context, "failed to get data")
                    }
                }


            }) { _, _, _ ->

        }
    }

    private fun getStudentData(batchID: String) {
        showAnimation()
        selectedStudentID = ""
        val json = JSONObject()
        json.put("batch_id", batchID)
        NetworkOps.post(Urls.assignmentStudentList, json.toString(), context, object : response {
            override fun onrespose(string: String?) {
                val studentData = Gson().fromJson(string, DCStudentData::class.java)

                if (studentData == null) {
                    if(activity==null){
                        return
                    }
                    activity!!.runOnUiThread {
                        mToast.showToast(context, "error")
                    }
                    return
                }

                if (studentData.success == "1") {
                    val students = studentData.students_list
                    studentSpinnerList.clear()
                    studentSpinnerList.addAll(students)
                    if(activity==null){
                        return
                    }
                    activity!!.runOnUiThread {
                        studentAdapter.notifyDataSetChanged()
                    }
                } else {
                    onfailure()
                }

            }

            override fun onfailure() {
                if(activity==null){
                    return
                }
                activity!!.runOnUiThread {
                    mToast.showToast(context, "error")
                }
            }

            override fun onInternetfailure() {
                if(activity==null){
                    return
                }
                activity!!.runOnUiThread {
                    mToast.noInternetSnackBar(activity!!)
                }
            }


        }) { _, _, _ ->

        }
    }

    private fun showAnimation() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            submit.text = " "
            progress.visibility = VISIBLE
            progress.playAnimation()
        }
    }

    fun hideAnimation() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            submit.text = "Give Marks"
            progress.visibility = INVISIBLE
            progress.cancelAnimation()
        }
    }
}
