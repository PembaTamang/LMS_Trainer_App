package orionedutech.`in`.lmstrainerapp.fragments.activityquestionTypes


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.fragment_activity_mcq.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAnswer

/**
 * A simple [Fragment] subclass.
 */
class ActivityMCQFragment : Fragment() {
        private var activityAnswer : ActivityAnswer ? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityAnswer = context as ActivityAnswer
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_activity_mcq, container, false)
        val boxContainer: RadioGroup = view.container11
        val aid =arguments!!.getString("aid")
        val qid = arguments!!.getString("qid")
        val qString = arguments!!.getString("qString")
        val context = context!!

        //todo set values

        boxContainer.setOnCheckedChangeListener { group, i ->
            val m = view.findViewById(i) as RadioButton
            activityAnswer!!.answer(m.tag.toString())
        }
        return view
    }


}
