package orionedutech.`in`.lmstrainerapp.fragments.questionTypes


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_assessment.view.*
import kotlinx.android.synthetic.main.fragment_mcq.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAns
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MCQFragment : Fragment(), View.OnClickListener {


    private lateinit var view1: View
    private val checks = ArrayList<RadioButton>()
    private val radioButtons = ArrayList<RadioButton>()
    private val choiceLimit = 1
    private var activityAns: ActivityAns? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityAns = context as ActivityAns
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view1 = inflater.inflate(R.layout.fragment_mcq, container, false)

        val boxContainer: RadioGroup = view1.container11
        val context = context!!
/*

        val qid = arguments!!.getString("qid")
        val qString = arguments!!.getString("qString")
*/

        view1.question.text = "Sample question"

        // mLog.i(TAG,"qid : $qid")
/*         CoroutineScope(IO).launch {
             context?.let {
                 val ansDao = MDatabase(it).getAssessmentAnswersDao()
                 val answers = ansDao.getAllAssesmentAnswers(qid!!)
                 answers.forEach {ans->
                     val radioButton = RadioButton(context)
                     radioButton.text = ans.answer_value
                     mLog.i(TAG,"loaded ans id ${ans.question_ans_id}")
                     radioButton.tag = ans.question_ans_id
                     radioButton.setOnClickListener(this@MCQFragment)
                     radioButton.setButtonDrawable(R.drawable.custom_button)
                     withContext(Main){
                         boxContainer.addView(radioButton)
                     }
                     radioButtons.add(radioButton)
                 }
             }
         }*/

        for (i in 0 until 4) {
            val radioButton = RadioButton(context)
            radioButton.text = "option $i"
            radioButton.tag = "tag $i"
            radioButton.setOnClickListener(this@MCQFragment)
            radioButton.setButtonDrawable(R.drawable.custom_button)
            boxContainer.addView(radioButton)
            radioButtons.add(radioButton)
        }
        boxContainer.setOnCheckedChangeListener { group, i ->
            val m = view1.findViewById(i) as RadioButton
            activityAns!!.answer(m.tag.toString())
        }
        return view1
    }

    override fun onClick(p0: View?) {

    }

    private fun refreshStates() {
        var choices = 0
        val userchoices = arrayOfNulls<String>(choiceLimit)
        for (m in radioButtons) {
            if (checks.contains(m)) {
                m.isChecked = checks.contains(m)
                userchoices[choices] = m.tag.toString()
                choices += 1
            } else {
                m.isChecked = checks.contains(m)
            }
            activityAns!!.answer(if (choices < choiceLimit) "" else userchoices[choiceLimit - 1]!!)
        }
    }
}
