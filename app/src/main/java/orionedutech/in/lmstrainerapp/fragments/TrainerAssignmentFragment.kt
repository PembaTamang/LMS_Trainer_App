package orionedutech.`in`.lmstrainerapp.fragments


import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_trainer_assignment.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.TrainerAssignmentAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCAssignment
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCAssignmentList
import orionedutech.`in`.lmstrainerapp.network.downloader.MActions
import orionedutech.`in`.lmstrainerapp.network.downloader.MDownloaderService
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A simple [Fragment] subclass.
 */
class TrainerAssignmentFragment : BaseFragment(), RecyclerItemClick {

    lateinit var giveMarks: MaterialButton
    private lateinit var recyclerView: ShimmerRecyclerView
    private val arrayList = ArrayList<DCAssignment>()
    private lateinit var adapter: TrainerAssignmentAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var busy = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trainer_assignment, container, false)
        giveMarks = view.give_marks
        recyclerView = view.recycler
        swipeRefreshLayout = view.swipe
        view.upload.setOnClickListener {
            //upload code here
        }

        giveMarks.setOnClickListener {


        }


        val ascendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingName =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
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
        val ascendingBatch =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_ascending) }
        val descendingBatch =
            context?.let { ContextCompat.getDrawable(it, R.drawable.animated_descending) }

        view.batch.setOnClickListener {
            if (ascendingBatches.get()) {
                val animator =
                    ObjectAnimator.ofInt(ascendingBatch, "level", 0, 10000).setDuration(500)
                animator.start()
                view.batch.setCompoundDrawablesWithIntrinsicBounds(null, null, ascendingBatch, null)
                ascendingBatches.set(false)
                //sort by descending names


            } else {
                val animator =
                    ObjectAnimator.ofInt(descendingBatch, "level", 0, 10000).setDuration(500)
                animator.start()
                view.batch.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    descendingBatch,
                    null
                )
                ascendingBatches.set(true)
                //sort by ascending names


            }
        }


/*
        for (i in 0..49) {
            val model = TrainerAssignmentModel("name : $i", "batch : $i", "course : $i")
            arrayList.add(model)
        }*/
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TrainerAssignmentAdapter(arrayList, object : RecyclerItemClick{
            override fun click(i: Int) {
                val model = arrayList[i]
                val url = model.media_disk_path_relative
                //todo check with room
                launch {
                    context?.let {
                        val dao = MDatabase(it).getFilesDao()
                        if(dao.urlExists(url)){
                            exportViewAlert(dao.getInternalPath(url),model.media_org_name)
                        }else{
                            downloadAlert(url,model.media_org_name)
                        }
                    }
                }


            }

        })
        recyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener {
            if (!busy) {
                swipeRefreshLayout.isRefreshing = false
                getData()
            }
        }

        getData()

        return view
    }

    private fun exportViewAlert(path : String,name :String) {
   val builder =  MaterialAlertDialogBuilder(context)
        .setTitle("Alert")
        .setMessage("choose an option")
        .setPositiveButton("view"){dialogInterface, i ->
            dialogInterface.dismiss()

        val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            val fragment = PDFFragment()
            val bundle  = Bundle()
            mLog.i(TAG,"path $path")
            bundle.putString("path",path)
            bundle.putString("name",name)
            fragment.arguments = bundle
            ft.add(R.id.mainContainer, fragment)
            ft.addToBackStack(null)
            ft.commit()
        }
        .setNegativeButton("export"){dialogInterface, i ->
            dialogInterface.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun downloadAlert(url:String,name:String) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Alert")
            .setMessage("do want to download $name ?")
            .setPositiveButton("download") { dialogInterface, i ->
                dialogInterface.dismiss()
                //add download code here

                val intent = Intent(context, MDownloaderService::class.java)
                // intent.putExtra("url", model.media_disk_path_relative)
                intent.putExtra("url",url)
                intent.putExtra("name", name)
                intent.putExtra("pdf",false)
                intent.action = MActions.start
                context!!.startService(intent)
            }.setNegativeButton("cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create().show()
    }

    private fun getData() {
        launch {
            context?.let {
                busy = true
                val dao = MDatabase(it).getUserDao()
                val adminID = dao.getAdminID()
                val userID = dao.getUserID()
                val centerID = dao.getCenterID()
                val json = JSONObject()
                json.put("user_admin_id", adminID)
                json.put("center_id", centerID)
                json.put("user_id", userID)
                recyclerView.showShimmerAdapter()
                NetworkOps.post(Urls.assignmentUrl, json.toString(), context, object :
                    response {

                    override fun onInternetfailure() {
                        mToast.noInternetSnackBar(activity!!)
                    }

                    override fun onrespose(string: String?) {
                        val assignments = Gson().fromJson(string, DCAssignmentList::class.java)
                        if (assignments == null) {
                            mToast.showToast(context, "data error")
                            return
                        }
                        if (assignments.success == "1") {
                            arrayList.clear()
                            arrayList.addAll(assignments.assignments)
                        } else {
                            mToast.showToast(context, "no assignment found")
                        }
                        activity?.runOnUiThread {
                            adapter.notifyDataSetChanged()
                            recyclerView.hideShimmerAdapter()
                            busy = false
                        }
                    }

                    override fun onfailure() {
                        mToast.showToast(context, "failed")
                        busy = false
                        activity?.runOnUiThread {
                            recyclerView.hideShimmerAdapter()
                        }
                    }


                }) { _, _, _ -> }


            }
        }


    }

    override fun click(itempos: Int) {

    }
}
