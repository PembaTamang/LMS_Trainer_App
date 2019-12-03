package orionedutech.`in`.lmstrainerapp.adapters.spinners

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCModule
import java.util.ArrayList

class ModuleAdapter(context: Context, textViewResourceId: Int, private val values: ArrayList<DCModule>) : ArrayAdapter<DCModule>(context, textViewResourceId, values) {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): DCModule? {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val label = super.getView(position, convertView, parent) as TextView
        label.setTextAppearance(context, R.style.Baloo)

        label.setTextColor(ContextCompat.getColor(context, R.color.default_text))

        label.text = values[position].course_module_name


        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextAppearance(context, R.style.Baloo)
        label.setTextColor(ContextCompat.getColor(context, R.color.default_text))
        label.text = values[position].course_module_name

        return label
    }
}