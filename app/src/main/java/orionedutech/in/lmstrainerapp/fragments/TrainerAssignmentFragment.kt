package orionedutech.`in`.lmstrainerapp.fragments


import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_trainer_assignment.view.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.TrainerAssignmentAdapter
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.model.TrainerAssignmentModel
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */
class TrainerAssignmentFragment : Fragment(), RecyclerItemClick {


    private val arrayList = ArrayList<TrainerAssignmentModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_trainer_assignment, container, false)

        view.upload.setOnClickListener {
            //upload code here
        }

        view.download.setOnClickListener {             //put download code here

        }
        view.give_marks.setOnClickListener {


        }


        val ascendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
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


        val ascendingBatches = AtomicBoolean(true)
        val ascendingBatch = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingBatch = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }

        view.batch.setOnClickListener {
            if (ascendingBatches.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingBatch, "level", 0, 10000).setDuration(500)
                animator.start()
                view. batch.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingBatch, null)
                ascendingBatches.set(false)
                //sort by descending names


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingBatch, "level", 0, 10000).setDuration(500)
                animator.start()
                view.batch.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingBatch, null)
                ascendingBatches.set(true)
                //sort by ascending names


            }
        }

        view.recycler.layoutManager = LinearLayoutManager(context)
        val adapter = TrainerAssignmentAdapter(arrayList, this)
        view.recycler.adapter = adapter


        for (i in 0..49) {
            val model = TrainerAssignmentModel("name : $i", "batch : $i", "course : $i")
            arrayList.add(model)
        }
        adapter.notifyDataSetChanged()





    return view
    }

    override fun click(itempos: Int) {

    }
}
