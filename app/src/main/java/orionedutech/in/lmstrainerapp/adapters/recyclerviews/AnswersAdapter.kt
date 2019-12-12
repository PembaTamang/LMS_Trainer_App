package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.header_item.view.*
import kotlinx.android.synthetic.main.score_item.view.*
import orionedutech.`in`.lmstrainerapp.ItemType
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.HeaderInterface
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.Answers
import java.util.*


class AnswersAdapter : ListAdapter<Answers, RecyclerView.ViewHolder>(ModelDiffUtilCallback()),
    HeaderInterface {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemview: View? = null
        return if (viewType == ItemType.header) {
            itemview =
                LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
            HeaderVH(itemview)
        } else {
            itemview =
                LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false)
            AnswerVH(itemview)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ItemType.header) {
            holder as HeaderVH
            holder.bind(getItem(position))
        } else {
            holder as AnswerVH
            holder.bind(getItem(position))
        }
    }


    inner class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.heading
        private val details = itemView.details
        fun bind(answers: Answers) {
            title.text = answers.header
            val totalQ = answers.totalQuestions
            val correct = answers.totalcorrect
            val incorrect = answers.totalincorrect
            val percentage = ((correct.toFloat()/totalQ.toFloat())*100f).toInt()
            mLog.i(TAG,"total $totalQ correct $correct  incorrect $incorrect percentage $percentage")
            details.text = String.format(Locale.getDefault(),"Total Q : %s, Total Correct: %s, Total Incorrect: %s, Percentage: %d %% ",totalQ,correct,incorrect,percentage)

        }
    }

    inner class AnswerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sl = itemView.sl
        private val ques = itemView.question
        private val ans = itemView.student_ans
        private val correctAns = itemView.correct_ans

        fun bind(answers: Answers) {
            sl.text = (adapterPosition + 1).toString()
            ques.text = answers.question
            ans.text = answers.correctAnswer
            correctAns.text = answers.answer
        }
    }


    override fun getHeaderPositionForItem(itemPosition: Int?): Int? {
        var headerPosition: Int? = 0
        for (i in itemPosition!! downTo 1) {
            if (isHeader(i)!!) {
                headerPosition = i
                return headerPosition
            }
        }
        return headerPosition

    }

    override fun getHeaderLayout(headerPosition: Int?) = R.layout.header_item

    override fun bindHeaderData(header: View?, headerPosition: Int?) {
        val tv = header!!.heading
        tv.text = getItem(headerPosition!!).header
    }

    override fun isHeader(itemPosition: Int?) = getItem(itemPosition!!).type == ItemType.header


    class ModelDiffUtilCallback : DiffUtil.ItemCallback<Answers>() {
        override fun areItemsTheSame(model: Answers, t1: Answers): Boolean {
            return model.header == t1.header
        }

        override fun areContentsTheSame(model: Answers, t1: Answers): Boolean {
            return model == t1
        }
    }


}