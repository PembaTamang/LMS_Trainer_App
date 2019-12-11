package orionedutech.`in`.lmstrainerapp.fragments.activityquestionTypes


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_activity_mcq.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityAnswerDao
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityQuestionsDao
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityAnswers
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAnswer
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.util.concurrent.CountDownLatch

/**
 * A simple [Fragment] subclass.
 */
class ActivityMCQFragment : Fragment() {
    private var activityAnswer: ActivityAnswer? = null
    var db: MDatabase? = null
    var questionsDao: ActivityQuestionsDao? = null
    var answersDao: ActivityAnswerDao? = null
    lateinit var qstn : TextView
    lateinit var qNo : TextView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityAnswer = context as ActivityAnswer
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity_mcq, container, false)
        val boxContainer: RadioGroup = view.container11
        qstn = view.question
        qNo = view.sl
        val qid = arguments!!.getString("qid")!!
        val Qnumber = arguments!!.getString("sl")!!
        val context = context!!
        db = MDatabase(context)
        questionsDao = db!!.getQuestionsDao()
        answersDao = db!!.getAnswersDao()
        val qString = getQuestionString(qid)
        qstn.text = qString
        qNo.text = Qnumber
        mLog.i(TAG,"q id $qid")
        val answers = getAnswers(qid)
        mLog.i(TAG, "answers count ${answers.size}")
        answers.forEach { ans ->
            val radioButton = RadioButton(context)
            radioButton.text = ans.answer_value
            mLog.i(TAG, "loaded ans id ${ans.question_ans_id}")
            radioButton.tag = ans.question_ans_id
            radioButton.setButtonDrawable(R.drawable.custom_button)
            boxContainer.addView(radioButton)
        }

        boxContainer.setOnCheckedChangeListener { group, i ->
            val m = view.findViewById(i) as RadioButton
            activityAnswer!!.answer(qid, m.tag.toString())
        }
        return view
    }

    private fun getAnswers(qid: String): List<ActivityAnswers> {
        val countDownLatch = CountDownLatch(1)
        var answers: List<ActivityAnswers> = ArrayList()
        CoroutineScope(IO).launch {
            answers = answersDao!!.getAllAnswers(qid)
            countDownLatch.countDown()
        }
        countDownLatch.await()
        return answers
    }

    private fun getQuestionString(qid: String): String {
        val countdown = CountDownLatch(1)
        var question = ""
        CoroutineScope(IO).launch {
            question = questionsDao!!.getQuestionString(qid)
            countdown.countDown()
        }
        countdown.await()
        return question
    }


}
