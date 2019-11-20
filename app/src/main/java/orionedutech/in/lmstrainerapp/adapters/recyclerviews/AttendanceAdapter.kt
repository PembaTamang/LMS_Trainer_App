package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.attendance_item.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.AttendanceInterface
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.AttendanceModel

class AttendanceAdapter(private val arrayList: ArrayList<AttendanceModel>) : RecyclerView.Adapter<AttendanceAdapter.AVH>() {
    private val itemStateArray = SparseBooleanArray()
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AVH {
        context = parent.context
       return AVH(LayoutInflater.from(context).inflate(R.layout.attendance_item,parent,false))
    }

    override fun getItemCount()  = arrayList.size

    override fun onBindViewHolder(holder: AVH, position: Int) {
       val m = arrayList[position]
        holder.bg.setBackgroundColor(if (position % 2 == 0) context?.let { ContextCompat.getColor(it,R.color.white) }!! else context?.let { ContextCompat.getColor(it,R.color.light_grey) }!!)
        holder.sl.text = (position+1).toString()
        holder.name.text = m.name
        holder.radioButtonP.setOnClickListener {

           itemStateArray.put(position,holder.radioButtonP.isChecked)
            m.status = if(holder.radioButtonP.isChecked)"present" else ""
            mLog.i(TAG,"change 1")
            holder.radioButtonA.isChecked = false

        }
        holder.radioButtonA.setOnClickListener {

            itemStateArray.put(position,holder.radioButtonA.isChecked)
            m.status = if(holder.radioButtonA.isChecked)"absent" else ""
            holder.radioButtonP.isChecked = false
            mLog.i(TAG,"change 2")

        }

        mLog.i(TAG,"status ${m.status}")
        if(m.status=="present"){
            holder.radioButtonP.isChecked = true
            holder.radioButtonA.isChecked = false
        }else if(m.status=="absent"){
            holder.radioButtonA.isChecked = true
            holder.radioButtonP.isChecked = false
        }

    }
        fun clearSparseArray(){
            itemStateArray.clear()
        }
    class AVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sl = itemView.sl!!
        val name = itemView.name!!
        val radioButtonP = itemView.present!!
        val radioButtonA = itemView.absent!!
        val bg  = itemView.root!!
    }
}