package orionedutech.`in`.lmstrainerapp.fragments.feedback


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_feedback_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.FeedbackAdapter
import orionedutech.`in`.lmstrainerapp.interfaces.FeedBackInterface
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.model.FeedbackModel
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCFeedback
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FeedbackListFragment : Fragment(), FeedBackInterface {
    override fun saveResponse(itempos: Int, choice: String) {
        arrayList[itempos].response = choice
    }

    //views
    lateinit var recyclerView: ShimmerRecyclerView

    //data
    internal var arrayList = ArrayList<FeedbackModel>()
    private var adapter: FeedbackAdapter? = null
    lateinit var animation: LottieAnimationView
    lateinit var button: MaterialButton
    var courseID: String = ""
    var userID: String = ""
    lateinit var swipe: SwipeRefreshLayout
    var busy = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feedback_list, container, false)
        recyclerView = view.recycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FeedbackAdapter(arrayList, this)
        recyclerView.adapter = adapter
        animation = view.progress
        swipe = view.swipe
        button = view.submit
        courseID = arguments!!.getString("course_id")!!
        userID = arguments!!.getString("user_id")!!

        button.setOnClickListener {

            arrayList.forEach {
                if (it.response == "") {
                    mToast.showToast(context, "please make all choices ")
                    return@setOnClickListener
                }
            }

            val json = JSONObject()
            for (model in arrayList) {
                try {
                    json.put(model.questionID, model.response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
            animation.visibility = VISIBLE
            animation.playAnimation()
            button.text = ""
            val postjson =
                "{\"user_type\":\"3\",\"uid\":\"$userID\",\"course_id\":\"$courseID\",\"response\":$json}"
            mLog.i(TAG, "response : $postjson")
            NetworkOps.post(Urls.feedbackPostUrl, postjson, context, object : response {
                override fun onrespose(string: String?) {
                    val jsonobj = JSONObject(string!!)
                    if (jsonobj.getString("success") == "1") {
                        activity?.runOnUiThread {
                            animation.cancelAnimation()
                            animation.visibility = GONE
                            button.text = "submit success"
                            Handler().postDelayed({
                                mToast.showToast(context, "feedback submitted successfully")
                                activity!!.onBackPressed()
                            }, 1500)
                        }
                    } else {
                        onCallFailed()
                    }
                }

                override fun onfailure() {
                    onCallFailed()
                }

                override fun onInternetfailure() {
                    activity?.runOnUiThread {
                        mToast.noInternetSnackBar(activity!!)
                    }
                    onCallFailed()
                }
            }

            ) { _, _, _ ->

            }
        }


        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
            if (!busy) {
                getFeedBackQuestions()
            }
        }

        Handler().postDelayed(
            {
                getFeedBackQuestions()
            }, 1000
        )

        return view
    }

    private fun getFeedBackQuestions() {
        CoroutineScope(IO).launch {
            if(activity==null){
                return@launch
            }
            activity!!.runOnUiThread {
                adapter!!.reset()
                busy = true
                recyclerView.showShimmerAdapter()
            }
            val json = JSONObject()
            json.put("user_type", "2")
            json.put("course_id", courseID)
            NetworkOps.post(Urls.feedbackUrl, json.toString(), context, object : response {
                override fun onrespose(string: String?) {
                    val feedback = Gson().fromJson(string, DCFeedback::class.java)
                    if (feedback == null) {
                        activity?.runOnUiThread {
                            mToast.showToast(context, "data error")
                        }
                        return
                    }
                    if (feedback.success == "1") {
                        val feedbackList = feedback.response
                        if (feedbackList.isEmpty()) {
                            activity?.runOnUiThread {
                                mToast.showToast(context, "no questions found")
                                activity?.onBackPressed()
                            }
                            return
                        }
                        arrayList.clear()
                        feedbackList.forEach {
                            val feedbackModel =
                                FeedbackModel(it.feedback_question, it.feedback_id, "")
                            arrayList.add(feedbackModel)
                        }
                        activity?.runOnUiThread {
                            adapter!!.notifyDataSetChanged()
                            recyclerView.hideShimmerAdapter()
                            busy = false
                        }
                    } else {
                        onCallFailed()
                    }

                }

                override fun onfailure() {
                    onCallFailed()
                }

                override fun onInternetfailure() {
                    activity?.runOnUiThread {
                        mToast.noInternetSnackBar(activity!!)
                    }
                    onCallFailed()
                }

            }) { _, _, _ ->

            }
        }
    }

    private fun onCallFailed() {
        activity?.runOnUiThread {
            busy = false
            button.text = "submit"
            mToast.showToast(context, "failed to get questions")
            activity?.onBackPressed()
        }
    }

}
