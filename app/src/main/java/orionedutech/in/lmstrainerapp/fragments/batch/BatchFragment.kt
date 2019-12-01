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
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.BatchAdapter
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.BatchAdapterAlt
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.model.BatchModel
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatchesLong
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCBatchesLongList
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */

class BatchFragment : Fragment(), RecyclerItemClick {
    override fun click(itempos: Int) {
        showToast(context, "$itempos clicked")
    }

    private var uid: String = ""
    private var centerID: String = ""
    lateinit var createBatch: MaterialButton
    lateinit var batchName: TextView
    lateinit var batchCenter: TextView
    lateinit var course: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var adapter: BatchAdapterAlt
    lateinit var refresh: SwipeRefreshLayout
    var arrayList: ArrayList<DCBatchesLong> = ArrayList()


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
        refresh = view.swipe
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = BatchAdapterAlt(arrayList)
        recyclerView.adapter = adapter


        adapter.notifyDataSetChanged()

        val ascendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingNames = AtomicBoolean(true)

        batchName.setOnClickListener {
            if (ascendingNames.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingName, null)
                ascendingNames.set(false)
                //sort by descending names
                val templist = arrayList.sortedWith(compareByDescending(DCBatchesLong::batch_name))
                arrayList.clear()
                arrayList.addAll(templist)
                adapter.notifyDataSetChanged()

            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingName, "level", 0, 10000).setDuration(500)
                animator.start()
                batchName.setCompoundDrawablesWithIntrinsicBounds(null, null, descendingName, null)
                ascendingNames.set(true)
                //sort by ascending names
                CoroutineScope(IO).launch {
                    val templist = arrayList.sortedWith(compareBy(DCBatchesLong::batch_name))
                    arrayList.clear()
                    arrayList.addAll(templist)
                   withContext(Main){
                       adapter.notifyDataSetChanged()
                   }

                }
            }
        }

        val ascendingCenter =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingCenter =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }
        val ascendingCenters = AtomicBoolean(true)
        batchCenter.setOnClickListener {
            if (ascendingCenters.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCenter.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ascendingCenter,
                    null
                )
                ascendingCenters.set(false)
                //sort by descending centers
                val templist = arrayList.sortedWith(compareByDescending(DCBatchesLong::batch_center))
                arrayList.clear()
                arrayList.addAll(templist)
                adapter.notifyDataSetChanged()

            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingCenter, "level", 0, 10000).setDuration(500)
                animator.start()
                batchCenter.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descendingCenter,
                    null
                )
                ascendingCenters.set(true)
                //sort by ascending centers
                val templist = arrayList.sortedWith(compareBy(DCBatchesLong::batch_center))
                arrayList.clear()
                arrayList.addAll(templist)
                adapter.notifyDataSetChanged()

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
                uid = userDao.getAdminID()
                centerID = userDao.getCenterID()
                val json = JSONObject()
                json.put("admin_id", uid)
                getBatches(json.toString())
            }
        }
    }

    private fun getBatches(json: String) {
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.batchesUrl, json, context, object : response {
            override fun onrespose(string: String?) {
                val batchData = Gson().fromJson(string, DCBatchesLongList::class.java)

                if (batchData == null) {
                    activity!!.runOnUiThread {
                        showToast(context, "data error")
                        return@runOnUiThread
                    }
                }
                if (batchData!!.success == "1") {
                    val batch = batchData.batches
                    arrayList.clear()
                    arrayList.addAll(batch)
                    activity!!.runOnUiThread {
                        recyclerView.hideShimmerAdapter()
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
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    mToast.noInternetSnackBar(activity!!)
                }
            }

        }
        ) { _, _, _ ->

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
