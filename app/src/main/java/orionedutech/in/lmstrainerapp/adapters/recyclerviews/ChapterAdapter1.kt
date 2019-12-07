package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_unit_item.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG

class ChapterAdapter1(val arrayList: ArrayList<String>, val recyclerItemClick: RecyclerItemClick) : RecyclerView.Adapter<ChapterAdapter1.CVH>() {

    lateinit var context: Context
    inner class CVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sl = itemView.serial!!
        val name = itemView.name!!
        val bg = itemView.root!!
        val image = itemView.video_icon!!
        init {
            itemView.setOnClickListener {
                recyclerItemClick.click(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CVH {
        context = parent.context
        return CVH(LayoutInflater.from(context).inflate(R.layout.recycler_unit_item, parent, false))
    }

    override fun getItemCount() = arrayList.size

    override fun onBindViewHolder(holder: CVH, position: Int) {
        holder.sl.text = (position + 1).toString()
        holder.name.text = arrayList[position]
        holder.image.visibility = VISIBLE

        holder.bg.setBackgroundColor(if (position % 2 == 0) context.let { ContextCompat.getColor(it,R.color.white) } else context.let { ContextCompat.getColor(it,R.color.light_grey) })
    }
}