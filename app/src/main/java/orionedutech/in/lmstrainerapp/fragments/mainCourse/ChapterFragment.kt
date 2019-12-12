package orionedutech.`in`.lmstrainerapp.fragments.mainCourse


import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_default_alert.view.*
import kotlinx.android.synthetic.main.fragment_chapter.view.*
import org.json.JSONObject
import org.w3c.dom.Text
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ChapterAdapter.Companion.isUnitData
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

    lateinit var name: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var cType: TextView
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
    lateinit var ft: FragmentTransaction
    var isUnit = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chapter, container, false)
        name = view.heading
        refreshLayout = view.swipe
        recyclerView = view.recycler
        cType = view.title
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
                isUnit = true
                cType.text = "Unit List"
                getUnitData()
            }

            "2" -> {
                isUnit = false
                isUnitData = false
                cType.text = "Subunit List"
                getSubUnitData()
            }
            else -> {

            }
        }
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            if (isUnitData) {
                getUnitData()
            } else {
                getSubUnitData()
            }
        }
        return view
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
                if (activity == null) {
                    return
                }
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
                if (activity == null) {
                    return
                }
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
                mLog.i(TAG, "units ${arrayList.size}")
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    adapter.notifyDataSetChanged()
                }


            }

            override fun onfailure() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    showToast(context, "failed to get data")
                }
            }

        }) { _, _, _ ->
        }

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
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "response $string")
                val data = Gson().fromJson(string, DCSubUnitMainData::class.java)
                if (data == null) {
                    onfailure()
                    return
                }
                val data1 = data.training_data
                if (activity == null) {
                    return
                }
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
                mLog.i(TAG, "subunits ${arrayList.size}")
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    recyclerView.hideShimmerAdapter()
                    adapter.notifyDataSetChanged()
                }


            }

            override fun onfailure() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    showToast(context, "failed to get data")
                }
            }

        }) { _, _, _ ->
        }
    }


    override fun click(itempos: Int) {
        if (isUnit) {
            //go to sub - unit fragment
            val fragment = SubUnitFragment()
            val bundle = Bundle()
            bundle.putString("chapter_id",chapterID)
            bundle.putString("unit_name", unitData[itempos].course_unit_name)
            bundle.putString("unit_id", unitData[itempos].course_unit_id)
            fragment.arguments = bundle
            ft = activity!!.supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            ft.add(R.id.chapterContainer, fragment)
            ft.addToBackStack(null)
            ft.commit()

        } else {
            val dialogueView = LayoutInflater.from(context)
                .inflate(R.layout.custom_default_alert, null, false)
            val builder =   MaterialAlertDialogBuilder(context)
                .setCancelable(true)
            builder.setView(dialogueView)
            val dialogue = builder.create()
            val ok = dialogueView.button
            ok.text = "play video"
            val dmessage  = dialogueView.message
            val title = dialogueView.titlee
            title.text = "Alert"
            val message  = SpannableStringBuilder()
                .append("Do you want to play ")
                .bold {append("${subUnitsData[itempos].lesson_name} ?")}

            dmessage.text = message
            ok.setOnClickListener {
                dialogue.dismiss()
                val fragment = VideoFragment()
                val bundle = Bundle()
                bundle.putString("chapter_id",chapterID)
                bundle.putString("name", subUnitsData[itempos].lesson_name)
                bundle.putString("url", subUnitsData[itempos].media_disk_path_relative)
                fragment.arguments = bundle
                ft = activity!!.supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.fadeout,
                    R.anim.fadeout
                )
                ft.add(R.id.chapterContainer, fragment)
                ft.addToBackStack(null)
                ft.commit()
            }
            dialogue.show()


        }
    }
}
