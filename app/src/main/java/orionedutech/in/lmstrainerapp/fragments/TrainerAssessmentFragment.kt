package orionedutech.`in`.lmstrainerapp.fragments


import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_trainer_assessment.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.TrainerAssessmentAdapter
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.model.TrainerAssessmentModel
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */
class TrainerAssessmentFragment : Fragment(), RecyclerItemClick {
    private val arrayList = ArrayList<TrainerAssessmentModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_trainer_assessment, container, false)

        view.upload.setOnClickListener {
          //upload code here
        }

        view.download.setOnClickListener {             //put download code here

        }
        val ascendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }
        val ascendingNames = AtomicBoolean(true)


        view.name.setOnClickListener {
            if (ascendingNames.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                view.name.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingName, null)
                ascendingNames.set(false)
                //sort by descending names


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                view.name.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingName, null)
                ascendingNames.set(true)
                //sort by ascending names


            }
        }

        val ascendingType = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingType = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }
        val ascendingTypes = AtomicBoolean(true)
        view.type.setOnClickListener {
            if (ascendingTypes.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingType, "level", 0, 10000).setDuration(500)
                animator.start()
                view.type.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingType, null)
                ascendingTypes.set(false)
                //sort by descending types


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingType, "level", 0, 10000).setDuration(500)
                animator.start()
                view.type.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingType, null)
                ascendingTypes.set(true)
                //sort by ascending types


            }
        }

        val ascendingCenter = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingCenter = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }
        val ascendingCenters = AtomicBoolean(true)
        view.centers.setOnClickListener {
            if (ascendingCenters.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                view.centers.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingCenter, null)
                ascendingCenters.set(false)
                //sort by descending centers


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                view.centers.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingCenter, null)
                ascendingCenters.set(true)
                //sort by ascending centers


            }
        }


        view.recycler.layoutManager = LinearLayoutManager(context)
        val adapter = TrainerAssessmentAdapter(arrayList, this)
        view.recycler.adapter = adapter

        for (i in 0..49) {
            val m = TrainerAssessmentModel("name : $i", "type : $i", "center :$i", "batch : $i")
            arrayList.add(m)
        }
        adapter.notifyDataSetChanged()


    return view
    }

    override fun click(itempos: Int) {

    }

}
