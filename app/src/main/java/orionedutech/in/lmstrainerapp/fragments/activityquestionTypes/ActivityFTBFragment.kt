package orionedutech.`in`.lmstrainerapp.fragments.activityquestionTypes


import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_activity_mcq.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityAnswerDao
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityQuestionsDao
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAnswer
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG


/**
 * A simple [Fragment] subclass.
 */
class ActivityFTBFragment : Fragment() {
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
        val view =  inflater.inflate(R.layout.fragment_activity_ftb, container, false)
        qstn = view.question
        qNo = view.sl
        return view
    }

    private fun underLineText(tv: TextView) {
        tv.paintFlags = tv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
