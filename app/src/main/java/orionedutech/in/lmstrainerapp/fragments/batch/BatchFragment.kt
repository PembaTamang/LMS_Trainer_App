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
import kotlinx.android.synthetic.main.fragment_trainer_assessment_upload.*
import kotlinx.android.synthetic.main.trainer_assessment_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.w3c.dom.Text
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.BatchAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.model.BatchModel
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass.
 */
class BatchFragment : Fragment(), RecyclerItemClick {
    override fun click(itempos: Int) {
        showToast(context,"$itempos clicked")
    }

    private var uid : String = ""
    private var centerID : String = ""
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

        CoroutineScope(IO).launch {
            context?.let {
                val userDao = MDatabase(it).getUserDao()
                uid = userDao.getUserID()
                centerID = userDao.getCenterID()
            }
        }

        val json = JSONObject()
        json.put("language_id", "1")
        json.put("user_id", uid)
        json.put("user_type", "3")
        json.put("center_id",centerID)

        getBatches(json.toString())

        return view
    }

    private fun getBatches(json: String) {
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.batchesUrl, json, context, object : response {
            override fun onrespose(string: String?) {

            }

            override fun onfailure() {
                runFailureCode()
            }

            override fun onInternetfailure() {
                runFailureCode()
                activity!!.runOnUiThread {
                    mToast.noInternetSnackBar(activity!!)
                }
            }

        }
        ){_,_,_->

        }
    }

    private fun runFailureCode() {
        activity!!.runOnUiThread {
            recyclerView.hideShimmerAdapter()
        }
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
