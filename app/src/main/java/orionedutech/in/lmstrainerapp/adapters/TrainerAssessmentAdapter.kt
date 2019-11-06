package orionedutech.`in`.lmstrainerapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.util.ArrayList
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.model.TrainerAssessmentModel

class TrainerAssessmentAdapter(
    private val arrayList: ArrayList<TrainerAssessmentModel>,
    private val click: RecyclerItemClick
) : RecyclerView.Adapter<TrainerAssessmentAdapter.TVH>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVH {
        context = parent.context
        return TVH(
            LayoutInflater.from(context).inflate(
                R.layout.trainer_assessment_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TVH, position: Int) {
        val m = arrayList[position]
        holder.cardView.setCardBackgroundColor(if (position % 2 == 0) context?.let { ContextCompat.getColor(it,R.color.white) }!! else context?.let { ContextCompat.getColor(it,R.color.light_grey) }!!)
        holder.sl.text = (position + 1).toString()
        holder.name.text = m.name
        holder.type.text = m.type
        holder.centers.text = m.center
        holder.batces.text = m.batch
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class TVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sl: TextView = itemView.findViewById(R.id.sl)
        var name: TextView = itemView.findViewById(R.id.name)
        var type: TextView = itemView.findViewById(R.id.type)
        var centers: TextView = itemView.findViewById(R.id.centers)
        var batces: TextView = itemView.findViewById(R.id.batches)
        var cardView: MaterialCardView = itemView.findViewById(R.id.root)
        init {
            itemView.setOnClickListener { click.click(adapterPosition) }
        }
    }
}
