package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_score_item.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScore

class ScoreAdapter(
    private val arrayList: ArrayList<DCScore>,
    private val recyclerItemClick: RecyclerItemClick
) :
    RecyclerView.Adapter<ScoreAdapter.SVH>() {
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SVH {
        context = parent.context
        return SVH(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_score_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = arrayList.size


    override fun onBindViewHolder(holder: SVH, position: Int) {
      val m = arrayList[position]
       holder.bg.setBackgroundColor(if (position % 2 == 0) context?.let {
           ContextCompat.getColor(
               it,
               R.color.white
           )
       }!! else context?.let { ContextCompat.getColor(it, R.color.light_grey) }!!)
        holder.slNo.text = (position+1).toString()
        holder.name.text = m.assesment_name
        holder.percentage.text = m.percentage.toString()
    }

    inner class SVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slNo = itemView.sl
        val name = itemView.name
        val percentage = itemView.percentage
        val bg  = itemView.root
        init {
            itemView.setOnClickListener {
                recyclerItemClick.click(adapterPosition)
            }
        }
    }

}