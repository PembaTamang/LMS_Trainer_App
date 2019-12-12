package orionedutech.`in`.lmstrainerapp.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_answers.view.*
import orionedutech.`in`.lmstrainerapp.adapters.HeaderItemDecoration
import orionedutech.`in`.lmstrainerapp.ItemType
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.AnswersAdapter
import orionedutech.`in`.lmstrainerapp.model.Answers
import orionedutech.`in`.lmstrainerapp.model.activityAnswer.ActivityAnswersModel

/**
 * A simple [Fragment] subclass.
 */
class AnswersFragment : Fragment() {
    var answers : ArrayList<Answers> =  ArrayList()
    var activityCount = 0
    var totalq = 0
    var corectans = 0
    var incorrectans = 0
    lateinit var recyclerView : RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_answers, container, false)
        val activityAnswerPref = activity!!.getSharedPreferences("activityanswers",Context.MODE_PRIVATE)
        val mModel = arguments!!.getParcelable<ActivityAnswersModel>("data")
        val activitites = mModel!!.data
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = AnswersAdapter()
        recyclerView.addItemDecoration(
            HeaderItemDecoration(
                recyclerView,
                isHeader()
            )
        )
        recyclerView.adapter  = adapter
        activitites.forEach { ans->
            activityCount++
            answers.add(Answers(ans.activity_name,"","","","",ans.total_questions,ans.correct_answers,ans.incorrect_answers,ItemType.header))
            val list  = ans.activity_data
               list.forEach { answer->
                   answers.add(Answers("",answer.question,answer.answer,answer.correctAnswer,answer.status,"","","",ItemType.item))
                   if(answer.status=="1"){
                       corectans++
                   }
               }
        }
        adapter.submitList(answers)

        return view
    }

    private fun isHeader(): (itemPosition: Int) -> Boolean {
        return {
            (recyclerView.adapter as AnswersAdapter).isHeader(it)
        }
    }


}
