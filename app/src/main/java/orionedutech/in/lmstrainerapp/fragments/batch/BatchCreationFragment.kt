package orionedutech.`in`.lmstrainerapp.fragments.batch


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_batch_creation.view.*
import orionedutech.`in`.lmstrainerapp.R

/**
 * A simple [Fragment] subclass.
 */
class BatchCreationFragment : Fragment() {
    lateinit var active: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_batch_creation, container, false)
        active = view.active

        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }

        view.mSwitch.setOnCheckedChangeListener { _, b ->
            if (b) {
                active.text = "active"
                active.setTextColor(ContextCompat.getColor(context!!, R.color.green))
            } else {
                active.text = "inactive"
                active.setTextColor(ContextCompat.getColor(context!!, R.color.orange))
            }

        }
        return view
    }


}
