package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.fragment_feedback.view.*
import orionedutech.`in`.lmstrainerapp.R
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FeedbackFragment : Fragment() {

    var courseList = ArrayList<String>()
    lateinit var courseAdapter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feedback, container, false)
        courseAdapter = context?.let { ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, courseList) }!!
        view.course_spinner.adapter = courseAdapter


        for (i in 0..19) {
            courseList.add("Course : $i")
        }
        courseAdapter.notifyDataSetChanged()


        view.feedback.setOnClickListener {
            val course = view.course_spinner.selectedItem.toString()


        }
        return view
    }


}
