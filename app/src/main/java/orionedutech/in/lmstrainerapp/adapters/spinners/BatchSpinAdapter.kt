package orionedutech.`in`.lmstrainerapp.adapters.spinners

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import androidx.core.content.ContextCompat

import java.util.ArrayList

import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.entities.Batch

class BatchSpinAdapter(context: Context, textViewResourceId: Int, private val values: ArrayList<Batch>,private val colors : Int) : ArrayAdapter<Batch>(context, textViewResourceId, values) {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): Batch? {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val label = super.getView(position, convertView, parent) as TextView
        label.setTextAppearance(context, R.style.Baloo)
        if(colors==0) {
            label.setTextColor(ContextCompat.getColor(context, R.color.greyText))
        }else{
            label.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        label.text = values[position].batch_name


        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextAppearance(context, R.style.Baloo)
        label.setTextColor(ContextCompat.getColor(context,R.color.greyText))
        label.text = values[position].batch_name

        return label
    }
}
