package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.trainer_batch_item.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.model.BatchModel

class BatchAdapter(private val arrayList: ArrayList<BatchModel>,private val click: RecyclerItemClick) :
    RecyclerView.Adapter<BatchAdapter.BVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BVH {
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
       val batchModel = arrayList[position]
        holder.sl.text = (position+1).toString()
        holder.batchName.text = batchModel.batchName
        holder.batchCenter.text = batchModel.batchCenter
        holder.course.text = batchModel.course

    }

    inner class BVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sl = itemView.sl!!
        var batchName = itemView.name!!
        var batchCenter = itemView.center!!
        var course = itemView.course!!
        init {
           itemView.setOnClickListener {
            click.click(adapterPosition)
           }
        }
    }

}