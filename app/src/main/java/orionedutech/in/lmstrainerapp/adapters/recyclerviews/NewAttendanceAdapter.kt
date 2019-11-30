package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews

import android.content.Context
import android.content.res.ColorStateList
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_student_list.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.AttendanceModel
import java.util.logging.Handler

class NewAttendanceAdapter(private val arrayList: ArrayList<AttendanceModel>) :
    RecyclerView.Adapter<NewAttendanceAdapter.AVH>() {
    private val itemStateArray = SparseBooleanArray()
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AVH {
        context = parent.context
        return AVH(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_student_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = arrayList.size

    override fun onBindViewHolder(holder: AVH, position: Int) {
        val m = arrayList[position]
        holder.root.setBackgroundColor(if (position % 2 == 0) context?.let {
            ContextCompat.getColor(
                it,
                R.color.white
            )
        }!! else context?.let { ContextCompat.getColor(it, R.color.light_grey) }!!)
        holder.slNo.text = (position+1).toString()
        holder.name.text = m.name
        holder.radio.setOnClickListener {
            if(m.status=="present"){
                mLog.i(TAG,"false")
                m.status = "absent"
                holder.radio.text = "Absent"
                holder.radio.isChecked = false
                android.os.Handler().postDelayed({
                    holder.radio.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!,R.color.radio_button_color))
                    holder.radio.isChecked = true

                },500)

            }else{
                mLog.i(TAG,"true")
                m.status = "present"
                holder.radio.text = "Present"
                holder.radio.isChecked = false
                android.os.Handler().postDelayed({
                    holder.radio.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!,R.color.radio_button_color1))
                    holder.radio.isChecked = true

                },500)
            }

        }


        if(m.status=="present"){
            holder.radio.text = "Present"
            holder.radio.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!,R.color.radio_button_color1))
            holder.radio.isChecked = true
        }else{
            holder.radio.text = "Absent"
            holder.radio.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!,R.color.radio_button_color))
            holder.radio.isChecked = true
        }

    }

    class AVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slNo = itemView.sl
        val name = itemView.name
        val radio = itemView.present
        val root = itemView.root

    }
    fun clearSparseArray(){
        itemStateArray.clear()
    }
}