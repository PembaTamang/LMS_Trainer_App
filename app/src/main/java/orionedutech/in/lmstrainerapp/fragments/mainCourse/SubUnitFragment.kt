package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_sub_unit.view.*
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter.Companion.isUnitData
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter1
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.subunit.TrainingIndividualSubUnitsData
import orionedutech.`in`.lmstrainerapp.network.response


/**
 * A simple [Fragment] subclass.
 */

class SubUnitFragment : Fragment(), RecyclerItemClick {
    private var unitid = ""
    lateinit var name: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var cType: TextView
    lateinit var adapter: ChapterAdapter1
    lateinit var refreshLayout: SwipeRefreshLayout
    var arrayList = ArrayList<String>()
    var subUnitsData = ArrayList<TrainingIndividualSubUnitsData>()
    lateinit var ft: FragmentTransaction
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sub_unit, container, false)
        val bundle = arguments!!
        unitid = bundle.getString("unit_id", "")
        name = view.heading
        name.text = bundle.getString("unit_name", "")
        refreshLayout = view.swipe
        recyclerView = view.recycler
        cType = view.title
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChapterAdapter1(arrayList, this)
        recyclerView.adapter = adapter
        isUnitData = false
        getData()

        return view
    }

    private fun getData() {
        val json = JSONObject()
        json.put("unit_id", unitid)
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.subunitUrl, json.toString(), context, object : response {
            override fun onInternetfailure() {
                if(activity==null){
                    return
                }
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "response $string ")
                if(string.isNullOrEmpty()){
                    onfailure()
                    return
                }
                val data= JSONObject(string)
                if(data.getString("success")=="1"){
                   val data1 = data.getJSONArray("response")

                    val founderArray = Gson().fromJson(data1.toString(), Array<TrainingIndividualSubUnitsData>::class.java)
                    subUnitsData.clear()
                    subUnitsData.addAll(founderArray)
                    arrayList.clear()
                    founderArray.forEach {
                        arrayList.add(it.lesson_name)
                    }
                    activity!!.runOnUiThread{
                        recyclerView.hideShimmerAdapter()
                        adapter.notifyDataSetChanged()
                    }
                    mLog.i(TAG,"subunits ${subUnitsData.size}")
                }else{
                 onfailure()
                }
            }

            override fun onfailure() {
                if(activity==null){
                    return
                }
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    showToast(context, "failed")
                }
            }

        }) { progress, speed, secs ->

        }

    }

    override fun click(itempos: Int) {
        MaterialAlertDialogBuilder(context).setTitle("Alert")
            .setMessage("Do you want to play  ${subUnitsData[itempos].lesson_name} ?")
            .setCancelable(true)
            .setPositiveButton("play video") { dialogInterface, i ->
                dialogInterface.dismiss()
                val fragment = VideoFragment()
                val bundle = Bundle()
                bundle.putString("name", subUnitsData[itempos].lesson_name)
                bundle.putString("url", subUnitsData[itempos].media_disk_path_relative)
                fragment.arguments = bundle
                ft = activity!!.supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                ft.replace(R.id.chapterContainer, fragment)
                ft.commit()

            }.create().show()
    }


}
