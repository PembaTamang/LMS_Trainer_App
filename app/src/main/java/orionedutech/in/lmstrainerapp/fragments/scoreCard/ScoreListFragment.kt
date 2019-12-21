package orionedutech.`in`.lmstrainerapp.fragments.scoreCard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.fragment_score_list.view.*
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ScoreListAdapter
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScoreList
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScoreListData
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ScoreListFragment : Fragment() {

    lateinit var title: TextView
    lateinit var name: TextView
    lateinit var email: TextView
    lateinit var time: TextView
    lateinit var totalQ: TextView
    lateinit var rightans: TextView
    lateinit var dates: TextView
    lateinit var percentageTV: TextView
    lateinit var progressBar: CircularProgressBar
    lateinit var recyclerView: ShimmerRecyclerView
    var naam = ""
    var mail = ""
    var arrayList: ArrayList<DCScoreList> = ArrayList()
    lateinit var adapter : ScoreListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_score_list, container, false)
        val aid = arguments!!.getString("aid")!!
        val uid = arguments!!.getString("uid")!!
        val batchID = arguments!!.getString("batch_id")!!
        naam = arguments!!.getString("name")!!
        mail = arguments!!.getString("email")!!

        val json = JSONObject()
        json.put("assesment_id", aid)
        json.put("language_id", "1")
        json.put("user_id", uid)
        json.put("batch_id", batchID)
        title = view.textView16
        name = view.name
        email = view.email
        time = view.time
        totalQ = view.totalQ
        rightans = view.rightAns
        dates = view.dates
        percentageTV = view.progressTV
        progressBar = view.circularProgressBar
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ScoreListAdapter(arrayList)
        recyclerView.adapter = adapter
        getScoreData(json.toString())

        return view
    }

    private fun getScoreData(json: String) {
        recyclerView.showShimmerAdapter()
        NetworkOps.post(Urls.scoreDetailList, json, context, object : response {
            override fun onrespose(string: String?) {
                mLog.i(TAG,"response $string")
                val scoredata = Gson().fromJson(string, DCScoreListData::class.java)
                if (scoredata == null) {
                    runFailureCode()
                    return
                }
                if (scoredata.success == "1") {
                    val assessmentTitle = scoredata.assesment_name
                    val samay = scoredata.assesment_time
                    val startDate = scoredata.assesment_start_date
                    val endDate = scoredata.assesment_end_date
                    val totalQues = scoredata.assesment_total_questions
                    val correct = scoredata.assesment_total_questions_right
                    var percentage  = scoredata.assesment_percentage
                    val scorelist = scoredata.response
                    arrayList.clear()
                    arrayList.addAll(scorelist)
                    if(activity==null){
                        return
                    }
                    activity!!.runOnUiThread {
                        title.text = assessmentTitle
                        email.text = mail
                        name.text = naam
                        time.text = String.format("Time : %s minutes",samay)
                        totalQ.text = String.format("Total Ques : %s ",totalQues)
                        rightans.text = String.format("Right Ans : %d ",correct)
                        dates.text = String.format("Start and End Date :  %s - %s",startDate,endDate)
                        percentage = percentage.replace(",","")
                        progressBar.progress = percentage.toFloat()
                        percentageTV.text = String.format(" %s %%",percentage)
                        adapter.notifyDataSetChanged()
                        recyclerView.hideShimmerAdapter()
                    }
                } else {
                    runFailureCode()
                }
            }

            override fun onfailure() {
                runFailureCode()
            }

            override fun onInternetfailure() {
                if(activity==null){
                    return
                }
                activity!!.runOnUiThread {
                    mToast.noInternetSnackBar(activity)
                }
                runFailureCode()
            }

        }) { _, _, _ ->
        }
    }

    private fun runFailureCode() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread{
            mToast.showToast(context, "failed")
            recyclerView.hideShimmerAdapter()
        }
    }

}
