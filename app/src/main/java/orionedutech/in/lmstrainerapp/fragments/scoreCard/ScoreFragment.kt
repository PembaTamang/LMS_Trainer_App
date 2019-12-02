package orionedutech.`in`.lmstrainerapp.fragments.scoreCard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.fragment_trainer_assessment_upload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.ScoreAdapter
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.interfaces.RecyclerItemClick
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScore
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCScoreData
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response

/**
 * A simple [Fragment] subclass.
 */
class ScoreFragment : Fragment(), RecyclerItemClick {


    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var adapter: ScoreAdapter
    var arrayList: ArrayList<DCScore> = ArrayList()
    var userID = ""
    var batchID = ""
    var name = ""
    var email = ""
   lateinit var json : JSONObject
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_score, container, false)
        recyclerView = view.recycler
        adapter = ScoreAdapter(arrayList, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        swipeRefreshLayout = view.swipe

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            recyclerView.showShimmerAdapter()
            getScoreData(json.toString())
        }
        CoroutineScope(IO).launch {
            context?.let {
                json = JSONObject()
                val userDao = MDatabase(it).getUserDao()
                userID = userDao.getUserID()
                batchID = userDao.getBatchID()
                name = userDao.getadminName()
                email = userDao.getEmail()
                json.put("user_id", userID)
                json.put("language_id", "1")
                withContext(Main){
                    recyclerView.showShimmerAdapter()
                }
                getScoreData(json.toString())
            }
        }
        return view
    }

    private fun getScoreData(json: String) {
        NetworkOps.post(Urls.scoreList, json, context, object : response {
            override fun onrespose(string: String?) {
                mLog.i(TAG,"response $string ")
                val scoreData = Gson().fromJson(string, DCScoreData::class.java)
                if (scoreData == null) {
                    runFailureCode()
                    return
                }
                if (scoreData.success == "1") {
                    val scoreList = scoreData.response

                    arrayList.clear()
                    arrayList.addAll(scoreList)
                    if(arrayList.size==0){
                        noData.visibility = VISIBLE
                    }
                    activity!!.runOnUiThread {
                        recyclerView.hideShimmerAdapter()
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    runFailureCode()
                }
            }

            override fun onfailure() {
                runFailureCode()

            }

            override fun onInternetfailure() {
                activity!!.runOnUiThread {
                    mToast.noInternetSnackBar(activity)
                }
            }

        }) { _, _, _ ->

        }
    }

    private fun runFailureCode() {
        activity!!.runOnUiThread{
            mToast.showToast(context, "failed")
            recyclerView.hideShimmerAdapter()
        }
    }

    override fun click(itempos: Int) {
        //move to other fragment with assessment id
        val bundle = Bundle()
        bundle.putString("aid", arrayList[itempos].assesment_id)
        bundle.putString("uid",userID)
        bundle.putString("batch_id",batchID)
        bundle.putString("name",name)
        bundle.putString("email",email)
        val fragment = ScoreListFragment()
        fragment.arguments = bundle
        moveToFragment(fragment)

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
