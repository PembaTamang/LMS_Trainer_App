package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.trainer_assignment_item.view.*

import java.util.ArrayList

import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCAssignment

class TrainerAssignmentAdapter(
    private val arrayList: ArrayList<DCAssignment>,
    private val itemClick: RecyclerItemClick
) : RecyclerView.Adapter<TrainerAssignmentAdapter.TVH>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVH {
        context = parent.context
        return TVH(
            LayoutInflater.from(context).inflate(
                R.layout.trainer_assignment_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TVH, position: Int) {
        val model = arrayList[position]
        holder.cardView.setCardBackgroundColor(if (position % 2 == 0) context?.let { ContextCompat.getColor(it,R.color.white) }!! else context?.let { ContextCompat.getColor(it,R.color.light_grey) }!!)
        holder.sl.text = (position + 1).toString()
        holder.name.text = model.assignment_name
        holder.batch.text = model.batch_name
        holder.course.text = model.course_name

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class TVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sl: TextView = itemView.sl
        var name: TextView = itemView.name
        var batch: TextView = itemView.batch
        var course: TextView = itemView.course
        var cardView: MaterialCardView = itemView.root

        init {
            itemView.setOnClickListener { view -> itemClick.click(adapterPosition) }
        }
    }
}
