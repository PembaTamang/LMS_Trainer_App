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
import kotlinx.android.synthetic.main.fragment_sub_unit.view.*
import kotlinx.android.synthetic.main.fragment_sub_unit.view.title
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
    private var chapterid = ""
    lateinit var name: TextView
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var cType: TextView
    lateinit var adapter: ChapterAdapter1
    lateinit var refreshLayout: SwipeRefreshLayout
    var arrayList = ArrayList<String>()
    var subUnitsData = ArrayList<TrainingIndividualSubUnitsData>()
    lateinit var ft: FragmentTransaction
    var trainingID = ""
    var trainerID = ""
    var centerID = ""
    var batchID = ""
    var courseID = ""
    var storageID = " "
    var unitID = ""
    var subUnitID = ""
    var moduleID = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sub_unit, container, false)
        val bundle = arguments!!
        name = view.heading
        unitid = bundle.getString("unit_id", "")
        chapterid = bundle.getString("chapter_id", "")
        name.text = bundle.getString("unit_name", "")

        trainingID = bundle.getString("training_id")!!
        trainerID = bundle.getString("user_id")!!
        centerID = bundle.getString("center_id")!!
        batchID = bundle.getString("batch_id")!!
        courseID = bundle.getString("course_id")!!
        storageID = bundle.getString("storage_id")!!
        moduleID = bundle.getString("module_id")!!
        unitID = bundle.getString("unit_id")!!
        subUnitID = bundle.getString("subunit_id")!!
        refreshLayout = view.swipe
        recyclerView = view.recycler
        cType = view.title
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChapterAdapter1(arrayList, this)
        recyclerView.adapter = adapter
        isUnitData = false
        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }
        getData()

        return view
    }

    private fun getData() {
        val json = JSONObject()
        json.put("unit_id", unitid)
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.subunitUrl, json.toString(), context, object : response {
            override fun onInternetfailure() {
                if (activity == null) {
                    return
                }
                activity!!.runOnUiThread {
                    noInternetSnackBar(activity)
                }
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "response $string ")
                if (string.isNullOrEmpty()) {
                    onfailure()
                    return
                }
                val data = JSONObject(string)
                if (data.getString("success") == "1") {
                    val data1 = data.getJSONArray("response")

                    val founderArray = Gson().fromJson(
                        data1.toString(),
                        Array<TrainingIndividualSubUnitsData>::class.java
                    )
                    subUnitsData.clear()
                    subUnitsData.addAll(founderArray)
                    arrayList.clear()
                    founderArray.forEach {
                        arrayList.add(it.lesson_name)
                    }
                    activity!!.runOnUiThread {
                        recyclerView.hideShimmerAdapter()
                        adapter.notifyDataSetChanged()
                    }
                    mLog.i(TAG, "subunits ${subUnitsData.size}")
                } else {
                    onfailure()
                }
            }

            override fun onfailure() {
                if (activity == null) {
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

        val dialogueView = LayoutInflater.from(context)
            .inflate(R.layout.custom_default_alert, null, false)
        val builder = MaterialAlertDialogBuilder(context)
            .setCancelable(true)
            .setView(dialogueView)
        val dialogue = builder.create()
        val ok = dialogueView.button2
        ok.text = "play video"
        val dmessage = dialogueView.message
        val title = dialogueView.titlee
        title.text = "Alert"
        val message = SpannableStringBuilder()
            .append("Do you want to play ")
            .bold { append("${subUnitsData[itempos].lesson_name} ?") }
        dmessage.text = message
        ok.setOnClickListener {
            dialogue.dismiss()
            val fragment = VideoFragment()
            val bundle = Bundle()

            bundle.putString("chapter_id", chapterid)
            bundle.putString("name", subUnitsData[itempos].lesson_name)
            bundle.putString("url", subUnitsData[itempos].media_disk_path_relative)
            bundle.putString("training_id",trainingID)
            bundle.putString("user_id",trainerID)
            bundle.putString("center_id",centerID)
            bundle.putString("batch_id",batchID)
            bundle.putString("course_id",courseID)
            bundle.putString("storage_id",subUnitsData[itempos].storage_id)
            bundle.putString("module_id",moduleID)
            bundle.putString("unit_id",unitID)
            bundle.putString("subunit_id",subUnitsData[itempos].lesson_id)

            fragment.arguments = bundle
            ft = activity!!.supportFragmentManager.beginTransaction()
            ft.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                0,
                0
            )
            ft.add(R.id.chapterContainer, fragment)
            ft.addToBackStack(null)
            ft.commit()

        }
        dialogue.show()
    }


}
