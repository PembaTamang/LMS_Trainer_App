package orionedutech.`in`.lmstrainerapp.fragments.scoreCard


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_score_list.view.*
import kotlinx.android.synthetic.main.fragment_score_list.view.name
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
    lateinit var startDate: TextView
    lateinit var endDate : TextView
    lateinit var percentageTV: TextView
    lateinit var progressBar: CircularProgressBar
    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var profileImage : CircleImageView
    lateinit var profilePref: SharedPreferences
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
        startDate = view.dates
        endDate = view.textView4
        profileImage = view.imageView8
        percentageTV = view.progressTV
        progressBar = view.circularProgressBar
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ScoreListAdapter(arrayList)
        recyclerView.adapter = adapter
        profilePref = activity!!.getSharedPreferences("profile", Context.MODE_PRIVATE)
        val imageURL = profilePref.getString("image", "")
        if (imageURL != "") {
            Glide.with(this).load(imageURL).skipMemoryCache(true).diskCacheStrategy(
                DiskCacheStrategy.NONE).into(profileImage)
        }
        getScoreData(json.toString())
        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }
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
                    val startDateStr = scoredata.assesment_start_date
                    val endDateStr = scoredata.assesment_end_date
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
                        totalQ.text = String.format("Total Questions : %s ",totalQues)
                        val text = String.format("Right Answers : %d ",correct)
                        val spannableString  = SpannableString(text)
                        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!,R.color.green)), text.indexOf(":"),text.length , 0)
                        rightans.text = spannableString
                        startDate.text = String.format("Start Date :  %s",startDateStr)
                        endDate.text = String.format("End Date :  %s",endDateStr)
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
