package orionedutech.`in`.lmstrainerapp.fragments.batch


import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_batch.view.*
import org.w3c.dom.Text
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.BatchAdapter
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.model.BatchModel
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass.
 */
class BatchFragment : Fragment(), RecyclerItemClick {
    override fun click(itempos: Int) {
        showToast(context,"$itempos clicked")
    }

    lateinit var createBatch: MaterialButton
    lateinit var batchName : TextView
    lateinit var batchCenter : TextView
    lateinit var course : TextView
    lateinit var recyclerView: ShimmerRecyclerView
    var arrayList : ArrayList<BatchModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_batch, container, false)
        createBatch = view.create_batch
        batchName = view.name
        batchCenter = view.batch
        course = view.course
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BatchAdapter(arrayList,this)
        recyclerView.adapter = adapter


        for(i in 0 until 20){
            val model = BatchModel("name $i","center $i","course $i")
            arrayList.add(model)
        }
        adapter.notifyDataSetChanged()

        val ascendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingName = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }
        val ascendingNames = AtomicBoolean(true)

        batchName.setOnClickListener {
            if (ascendingNames.get()) {
                val animator = ObjectAnimator.ofInt(ascendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingName, null)
                ascendingNames.set(false)
                //sort by descending names


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingName, null)
                ascendingNames.set(true)
                //sort by ascending names

            }
        }

        val ascendingCenter = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_ascending) }
        val descendingCenter = context?.let { ContextCompat.getDrawable(it,R.drawable.animated_descending) }
        val ascendingCenters = AtomicBoolean(true)
            batchCenter.setOnClickListener {
                if (ascendingCenters.get()) {
                    val animator =
                        ObjectAnimator.ofInt(ascendingCenter, "level", 0, 10000).setDuration(500)
                    animator.start()
                    batchCenter.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingCenter, null)
                    ascendingCenters.set(false)
                    //sort by descending centers


                } else {
                    val animator =
                        ObjectAnimator.ofInt(descendingCenter, "level", 0, 10000).setDuration(500)
                    animator.start()
                    batchCenter.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingCenter, null)
                    ascendingCenters.set(true)
                    //sort by ascending centers


                }
            }


          createBatch.setOnClickListener {
            moveToFragment(BatchCreationFragment())
          }

        return view
    }

    private fun moveToFragment(fragment: Fragment) {
        val ft = activity?.supportFragmentManager!!.beginTransaction()
        ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        ft.add(R.id.mainContainer, fragment)
        ft.addToBackStack(null)
        ft.commit()
    }
}
