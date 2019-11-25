package orionedutech.`in`.lmstrainerapp.fragments.questionTypes


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_assessment.view.*
import kotlinx.android.synthetic.main.fragment_mcq.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAns
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
        view1  = inflater.inflate(R.layout.fragment_mcq, container, false)

        val boxContainer = view1.container11
        val count = 5
        val context = context!!

        val qid = arguments!!.getString("qid")




        for (i in 0 until count) {
            val radioButton = RadioButton(context)
            radioButton.text = String.format(Locale.getDefault(), "Option %d", i)
            radioButton.id = i
            radioButton.tag = "Answer id : $i"
            radioButton.setOnClickListener(this)
            radioButton.setTextAppearance(context, R.style.OpenSansRegular)
            radioButton.setButtonDrawable(R.drawable.custom_button)
            boxContainer.addView(radioButton)
            radioButtons.add(radioButton)
        }


        return view1
    }
    override fun onClick(p0: View?) {
        val m = view as RadioButton
        if (checks.contains(m)) {
            checks.remove(m)
        } else {
            if (checks.size < choiceLimit) {
                checks.add(m)
            } else {
                checks.removeAt(choiceLimit - checks.size)
                checks.add(m)
            }
        }
        refreshStates()
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
            activityAns!!.answer(if (choices < choiceLimit) "" else userchoices[choiceLimit - 1] + "," + userchoices[choiceLimit - 2])
        }
    }
}
