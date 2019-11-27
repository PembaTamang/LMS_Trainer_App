package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.attendance_item.view.root
import kotlinx.android.synthetic.main.attendance_item.view.sl
import kotlinx.android.synthetic.main.score_item.view.*
import java.util.ArrayList
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScoreList

class ScoreListAdapter(
    private val arrayList: ArrayList<DCScoreList>
) : RecyclerView.Adapter<ScoreListAdapter.SVH>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVH {
        context = parent.context
        return SVH(LayoutInflater.from(parent.context).inflate(R.layout.score_item, parent, false))
    }

    override fun onBindViewHolder(holder: SVH, position: Int) {
        val m = arrayList[position]
        holder.root.setBackgroundColor(if (position % 2 == 0) context?.let {
            ContextCompat.getColor(
                it,
                R.color.white
            )
        }!! else context?.let { ContextCompat.getColor(it, R.color.light_grey) }!!)
        holder.sl.text = (position + 1).toString()
        holder.question.text = m.assesment_question
        holder.correctAns.text = m.correct_answers
        holder.studentAns.text = m.user_answer_value
        holder.status.setImageDrawable(
            if (m.user_answer_right_wrong == "0") context!!.getDrawable(R.drawable.ic_wrong) else context!!.getDrawable(
                R.drawable.ic_correct
            )
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class SVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sl: TextView = itemView.sl
        var question: TextView = itemView.question
        var correctAns: TextView = itemView.correct_ans
        var studentAns: TextView = itemView.student_ans
        var status: ImageView = itemView.status
        var root: ConstraintLayout = itemView.root
    }
}
