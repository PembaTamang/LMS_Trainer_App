package orionedutech.`in`.lmstrainerapp.fragments.batch


import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_batch.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.BatchAdapterAlt
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatchFragment
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatchFragmentItem
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */

class BatchFragment : Fragment() {


    private var uid: String = ""
    private var centerID: String = ""
    lateinit var createBatch: MaterialButton
    lateinit var batchName: TextView
    lateinit var batchCenter: TextView
    lateinit var batchCourse: TextView
    lateinit var nosTV : TextView
    lateinit var course: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var adapter: BatchAdapterAlt
    lateinit var refresh: SwipeRefreshLayout
    var arrayList: ArrayList<DCBatchFragmentItem> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_batch, container, false)
        createBatch = view.create_batch
        batchName = view.name
        batchCenter = view.batch
        batchCourse = view.course
        course = view.course
        recyclerView = view.recycler
        nosTV = view.nostds
        refresh = view.swipe
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = BatchAdapterAlt(arrayList)
        recyclerView.adapter = adapter
        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }

        val ascending =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descending =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingNames = AtomicBoolean(true)

        batchName.setOnClickListener {
            if (ascendingNames.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascending, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, ascending, null)
                ascendingNames.set(false)
                //sort by descending names
                CoroutineScope(IO).launch {
                    val templist =
                        arrayList.sortedWith(compareByDescending(DCBatchFragmentItem::batch_name))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                val animator =
                    ObjectAnimator.ofInt(descending, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, descending, null)
                ascendingNames.set(true)
                //sort by ascending names
                CoroutineScope(IO).launch {
                    val templist = arrayList.sortedWith(compareBy(DCBatchFragmentItem::batch_name))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }

                }
            }
        }

        val ascending1 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descending1 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingCenters = AtomicBoolean(true)
        batchCenter.setOnClickListener {
            if (ascendingCenters.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascending1, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCenter.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ascending1,
                    null
                )
                ascendingCenters.set(false)
                //sort by descending centers
                CoroutineScope(IO).launch {
                    val templist =
                        arrayList.sortedWith(compareByDescending(DCBatchFragmentItem::batch_center_name))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                val animator =
                    ObjectAnimator.ofInt(descending1, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCenter.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descending1,
                    null
                )
                ascendingCenters.set(true)
                //sort by ascending centers
                CoroutineScope(IO).launch {
                    val templist = arrayList.sortedWith(compareBy(DCBatchFragmentItem::batch_center_name))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }

            }
        }

        val ascending2 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descending2 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingCourses = AtomicBoolean(true)
        batchCourse.setOnClickListener {
            if (ascendingCourses.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascending2, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCourse.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ascending2,
                    null
                )
                ascendingCourses.set(false)
                //sort by descending centers
                CoroutineScope(IO).launch {
                    val templist =
                        arrayList.sortedWith(compareByDescending(DCBatchFragmentItem::batch_code))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                val animator =
                    ObjectAnimator.ofInt(descending2, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCourse.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descending2,
                    null
                )
                ascendingCourses.set(true)
                //sort by ascending centers
                CoroutineScope(IO).launch {
                    val templist = arrayList.sortedWith(compareBy(DCBatchFragmentItem::batch_code))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }

            }
        }
        val ascending3 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descending3 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingNos = AtomicBoolean(true)
        nosTV.setOnClickListener {
            if (ascendingNos.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascending3, "level", 0, 10000).setDuration(500)
                animator.start()
                nosTV.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ascending3,
                    null
                )
                ascendingNos.set(false)
                //sort by descending centers
                CoroutineScope(IO).launch {
                    val templist =
                        arrayList.sortedWith(compareByDescending(DCBatchFragmentItem::batch_total_students))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                val animator =
                    ObjectAnimator.ofInt(descending3, "level", 0, 10000).setDuration(500)
                animator.start()
                nosTV.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descending3,
                    null
                )
                ascendingNos.set(true)
                //sort by ascending centers
                CoroutineScope(IO).launch {
                    val templist = arrayList.sortedWith(compareBy(DCBatchFragmentItem::batch_total_students))
                    arrayList.clear()
                    arrayList.addAll(templist)
                    withContext(Main) {
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                }

            }
        }

        createBatch.setOnClickListener {
            moveToFragment(BatchCreationFragment())
        }


        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            getData()
        }

        getData()


        return view
    }

    private fun getData() {
        CoroutineScope(IO).launch {
            context?.let {
                val userDao = MDatabase(it).getUserDao()
                uid = userDao.getUserID()
                val batches = userDao.getBatchID()
                centerID = userDao.getCenterID()
                val json = JSONObject()
                json.put("trainer_id", uid)
                json.put("batch_id", batches)
                getBatches(json.toString())
            }
        }
    }

    private fun getBatches(json: String) {
        if (activity == null) {
            return
        }
        activity!!.runOnUiThread {
            recyclerView.showShimmerAdapter()
        }
        NetworkOps.post(Urls.batchesUrl, json, context, object : response {
            override fun onrespose(string: String?) {
                if(string.isNullOrEmpty()){
                    onfailure()
                    return
                }
                val batchData = Gson().fromJson(string, DCBatchFragment::class.java)

                if (batchData == null) {
                    if (activity == null) {
                        return
                    }
                    activity!!.runOnUiThread {
                        showToast(context, "data error")
                        return@runOnUiThread
                    }
                }
                if (batchData!!.success == "1") {
                    val batch = batchData.batches
                    arrayList.clear()
                    arrayList.addAll(batch)
                    if (activity == null) {
                        return
                    }
                    activity!!.runOnUiThread {
                        recyclerView.hideShimmerAdapter()
                        // notifydatasetchanged()
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    onfailure()
                }

            }


            override fun onfailure() {
                runFailureCode()
            }

            override fun onInternetfailure() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    mToast.noInternetSnackBar(activity!!)
                }
            }

        }
        ) { _, _, _ ->

        }
    }

    private fun notifydatasetchanged() {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(
            context,
            R.anim.layout_animation_fall_down
        )
        recyclerView.layoutAnimation = controller
        adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    private fun runFailureCode() {
        if (activity == null) {
            return
        }
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
