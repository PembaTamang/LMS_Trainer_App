package orionedutech.`in`.lmstrainerapp.fragments.assignment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.spinners.BatchSpinAdapter
import orionedutech.`in`.lmstrainerapp.database.entities.Batch
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScoreListData
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCStudentData
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCStudents
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response


/**
 * A simple [Fragment] subclass.
 */

class MarksFragment : Fragment() {

    lateinit var courseSpinner: Spinner
    lateinit var batchSpinner: Spinner
    lateinit var assignmentSpinner: Spinner
    lateinit var totalMarks : EditText
    lateinit var studentMarks : EditText
    lateinit var submit : MaterialButton
    lateinit var progress : LottieAnimationView





    var batchListSpinner = ArrayList<Batch>()
    lateinit var batchAdapter: BatchSpinAdapter
    lateinit var selectedBatchID: String

    var studentSpinnerList = ArrayList<DCStudents>()
    lateinit var studentAdapter: BatchSpinAdapter
    lateinit var selectedStudent: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        val view = inflater.inflate(R.layout.fragment_marks, container, false)

        courseSpinner = view.course
        batchSpinner = view.batch
        assignmentSpinner = view.assignment_spinner
        totalMarks = view.total_marks
        studentMarks = view.student_marks
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
                CoroutineScope(Dispatchers.IO).launch {
                    getStudentData(selectedBatchID)
                }
            }

        }



        return view
    }

    private fun getStudentData(batchID: String) {
        val json = JSONObject()
        json.put("batch_id",batchID)
        NetworkOps.post(Urls.assignmentMarksUpload,json.toString(),context,object: response{
            override fun onrespose(string: String?) {
           val studentData = Gson().fromJson(string,DCStudentData::class.java)

                if(studentData==null){
                    activity!!.runOnUiThread {
                        mToast.showToast(context,"error")
                    }
                    return
                }

                if(studentData.success=="1"){
                 val students = studentData.students_list
                    studentSpinnerList.clear()
                    studentSpinnerList.addAll(students)
                    
             }else{
                 studentFailureCode()
             }

            }

            override fun onfailure() {
                studentFailureCode()
            }

            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    mToast.noInternetSnackBar(activity!!)
                }
            }

        }) { _, _, _ ->
        }
    }

    private fun studentFailureCode() {

    }

}
