package orionedutech.`in`.lmstrainerapp.adapters.recyclerviews


import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feedback_layout.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.interfaces.FeedBackInterface
import orionedutech.`in`.lmstrainerapp.model.FeedbackModel
import java.util.ArrayList

class FeedbackAdapter(val arraylist: ArrayList<FeedbackModel>, private val feedBackInterface: FeedBackInterface) : RecyclerView.Adapter<FeedbackAdapter.FVH>() {
    private val itemStateArray = SparseBooleanArray()
    private val hashMap = SparseArray<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FVH {
        return FVH(LayoutInflater.from(parent.context).inflate(R.layout.feedback_layout, parent, false))
    }
    fun reset(){
        itemStateArray.clear()
        hashMap.clear()
    }
    override fun onBindViewHolder(holder: FVH, position: Int) {
        holder.sl.text = (position + 1).toString()
        holder.question.text = arraylist[position].question
        holder.one.setOnClickListener {
            feedBackInterface.saveResponse(position, if (holder.one.isChecked) "excellent" else "")
            itemStateArray.put(position, holder.one.isChecked)
            hashMap.put(position, arraylist[position].response)
            unCheck(arrayOf(holder.two, holder.three, holder.four))
        }
        holder.two.setOnClickListener {
            feedBackInterface.saveResponse(position, if (holder.two.isChecked) "good" else "")
            itemStateArray.put(position, holder.two.isChecked)
            hashMap.put(position, arraylist[position].response)
            unCheck(arrayOf(holder.one, holder.three, holder.four))
        }
        holder.three.setOnClickListener {
            feedBackInterface.saveResponse(position, if (holder.three.isChecked) "ok" else "")
            itemStateArray.put(position, holder.three.isChecked)
            hashMap.put(position, arraylist[position].response)
            unCheck(arrayOf(holder.two, holder.one, holder.four))
        }
        holder.four.setOnClickListener {
            feedBackInterface.saveResponse(position, if (holder.four.isChecked) "bad" else "")
            itemStateArray.put(position, holder.four.isChecked)
            hashMap.put(position, arraylist[position].response)
            unCheck(arrayOf(holder.two, holder.three, holder.one))
        }

        //settings checkboxes
        if (itemStateArray.get(position)) {

            val resp = hashMap.get(position)

            assert(resp != null)
            when (resp) {
                "excellent" -> {
                    holder.one.isChecked = true
                    unCheck(arrayOf(holder.two, holder.three, holder.four))
                }
                "good" -> {
                    holder.two.isChecked = true
                    unCheck(arrayOf(holder.one, holder.three, holder.four))
                }

                "ok" -> {
                    holder.three.isChecked = true
                    unCheck(arrayOf(holder.two, holder.one, holder.four))
                }

                "bad" -> {
                    holder.four.isChecked = false
                    unCheck(arrayOf(holder.two, holder.three, holder.one))
                }
            }

        } else {
            unCheck(arrayOf(holder.two, holder.three, holder.one, holder.four))
        }
    }

    private fun unCheck(checkBoxes: Array<CheckBox>) {
        for (c in checkBoxes) {
            c.isChecked = false
        }
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    inner class FVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sl: TextView = itemView.sl
        var question: TextView = itemView.question
        var one: CheckBox = itemView.happy
        var two: CheckBox = itemView.confused
        var three: CheckBox = itemView.sad
        var four: CheckBox = itemView.angry

    }
}
