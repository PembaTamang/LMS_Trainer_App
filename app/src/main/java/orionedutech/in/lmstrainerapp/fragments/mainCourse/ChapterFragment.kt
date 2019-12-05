package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chapter.view.*
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.noInternetSnackBar
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.subunit.DCSubUnitMainData
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.subunit.TrainingIndividualSubUnitsData
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.unit.DCUnitMainData
import orionedutech.`in`.lmstrainerapp.network.dataModels.courseData.unit.TrainingIndividualUnitData
import orionedutech.`in`.lmstrainerapp.network.response


/**
 * A simple [Fragment] subclass.
 */

class ChapterFragment : Fragment(), RecyclerItemClick {
    companion object{
        var isUnitData = false
    }
    lateinit var name: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    var trainerID = ""
    var batchID = ""
    var courseID = ""
    var moduleID = ""
    var chapterID = ""
    var unitID = ""
    var subUnitID = ""
    var trainingID = ""
    var uniqueID = ""
    var chapterType = ""
    lateinit var adapter: ChapterAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    var arrayList = ArrayList<String>()
    var unitData = ArrayList<TrainingIndividualUnitData>()
    var subUnitsData = ArrayList<TrainingIndividualSubUnitsData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chapter, container, false)
        name = view.heading
        refreshLayout = view.swipe
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChapterAdapter(arrayList, this)
        recyclerView.adapter = adapter
        val bundle = arguments!!

        trainerID = bundle.getString("trainerID", trainerID)
        trainingID = bundle.getString("trainingID", trainingID)
        uniqueID = bundle.getString("uniqueID", uniqueID)
        courseID = bundle.getString("courseID", courseID)
        moduleID = bundle.getString("moduleID", moduleID)
        chapterID = bundle.getString("chapterID", chapterID)
        batchID = bundle.getString("batchID", batchID)
        chapterType = bundle.getString("type", "1")
        when (chapterType) {
            "1" -> {
                isUnitData = true
                getUnitData()
            }

            "2" -> {
                getSubUnitData()
            }
            else -> {

            }
        }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            if(isUnitData){
                getUnitData()
            }else{
                getSubUnitData()
            }
        }
        return view
    }

    private fun getSubUnitData() {

        val json = JSONObject()
        json.put("trainer_id", trainerID)
        json.put("training_id", trainingID)
        json.put("uniqid", uniqueID)
        json.put("training_course", courseID)
        json.put("training_module", moduleID)
        json.put("training_chapter", chapterID)
        json.put("training_batch", batchID)

        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.trainingContent, json.toString(), context, object : response {
            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                val data = Gson().fromJson(string, DCSubUnitMainData::class.java)
                if (data == null) {
                    onfailure()
                    return
                }
                val data1 = data.training_data
                activity!!.runOnUiThread {
                    name.text = data1.training_chapter_name
                }
                val subunits = data1.training_sub_units_data
                subUnitsData.clear()
                subUnitsData.addAll(subunits)
                arrayList.clear()
                subUnitsData.forEach {
                    arrayList.add(it.lesson_name)
                }
                mLog.i(TAG,"subunits ${arrayList.size}")
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    adapter.notifyDataSetChanged()
                }


            }

            override fun onfailure() {
                activity!!.runOnUiThread {
                    showToast(context, "failed to get data")
                }
            }

        }) { _, _, _ ->
        }
    }

    private fun getUnitData() {

        val json = JSONObject()
        json.put("trainer_id", trainerID)
        json.put("training_id", trainingID)
        json.put("uniqid", uniqueID)
        json.put("training_course", courseID)
        json.put("training_module", moduleID)
        json.put("training_chapter", chapterID)
        json.put("training_batch", batchID)
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.trainingContent, json.toString(), context, object : response {
            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                val data = Gson().fromJson(string, DCUnitMainData::class.java)
                if (data == null) {
                    onfailure()
                    return
                }
                val data1 = data.training_data
                activity!!.runOnUiThread {
                    name.text = data1.training_chapter_name
                }
                val units = data1.training_units_data
                unitData.clear()
                unitData.addAll(units)
                arrayList.clear()
                unitData.forEach {
                    arrayList.add(it.course_unit_name)
                }
                mLog.i(TAG,"units ${arrayList.size}")
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    adapter.notifyDataSetChanged()
                }


            }

            override fun onfailure() {
                activity!!.runOnUiThread {
                    showToast(context, "failed to get data")
                }
            }

        }) { _, _, _ ->
        }

    }

    override fun click(itempos: Int) {
        if (isUnitData) {

            showToast(context, unitData[itempos].course_unit_name)

        } else {

            showToast(context, subUnitsData[itempos].lesson_name)
        }
    }
}
