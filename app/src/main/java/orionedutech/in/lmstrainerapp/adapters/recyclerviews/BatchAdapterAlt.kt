package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.trainer_batch_item.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatchesLong

class BatchAdapterAlt(val arrayList: ArrayList<DCBatchesLong>) : RecyclerView.Adapter<BatchAdapterAlt.BVH>() {
     lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BVH {
        context = parent.context
        return BVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.trainer_batch_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = arrayList.size

    override fun onBindViewHolder(holder: BVH, position: Int) {
        val m = arrayList[position]
        holder.bg.setBackgroundColor(if (position % 2 == 0) context.let { ContextCompat.getColor(it,R.color.white) } else context.let { ContextCompat.getColor(it,R.color.light_grey) })
        holder.serialNumber.text = (position+1).toString()
        holder.batchName.text = m.batch_name
        holder.centerName.text = m.batch_center
        holder.course.text = m.courses_wbt

    }

    inner class BVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var serialNumber = itemView.sl
        var batchName = itemView.name
        var centerName = itemView.center
        var course = itemView.course
        var bg  = itemView.root
    }
}
