package orionedutech.`in`.lmstrainerapp.fragments.assessmentquestionTypes


import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_mcq.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.AssessmentAnswer
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * A simple [Fragment] subclass.
 */
class AssessmentMCQFragment : Fragment() {


    private lateinit var view1: View
    private var activityAns: AssessmentAnswer? = null
    var aid = ""
    lateinit var qNo : TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityAns = context as AssessmentAnswer
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_mcq, container, false)

        val boxContainer: RadioGroup = view1.container11
        val context = context!!
        val qid = arguments!!.getString("qid")
        val qString = arguments!!.getString("qString")
        val review =     arguments!!.getBoolean("review")
        aid = ""
        if(review){
            aid = arguments!!.getString("aid")!!
            mLog.i(TAG,"review answer ID $aid")
        }
        qNo = view1.sl
        qNo.text = arguments!!.getString("sl")
        view1.question.text = qString

       CoroutineScope(IO).launch {
           context.let {
               val ansDao = MDatabase(it).getAssessmentAnswersDao()
               val answers = ansDao.getAllAssesmentAnswers(qid!!)
               answers.forEach {ans->
                   val radioButton = RadioButton(context)
                   radioButton.text = ans.answer_value
                   radioButton.tag = ans.question_ans_id
                   radioButton.setButtonDrawable(R.drawable.custom_button)
                   withContext(Main){
                       boxContainer.addView(radioButton)
                   }
                   if(ans.question_ans_id == aid){
                       radioButton.isChecked = true
                   }
               }

           }
         }

        boxContainer.setOnCheckedChangeListener { group, i ->
            val m = view1.findViewById(i) as RadioButton
            activityAns!!.answer(m.tag.toString())
        }
        return view1
    }

}
