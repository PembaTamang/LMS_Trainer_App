package orionedutech.`in`.lmstrainerapp.fragments.assignment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_marks.view.*
import orionedutech.`in`.lmstrainerapp.R


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

        return view
    }

}
