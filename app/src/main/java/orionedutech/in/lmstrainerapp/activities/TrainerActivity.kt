package orionedutech.`in`.lmstrainerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview
import kotlinx.android.synthetic.main.activity_trainer.*
import kotlinx.android.synthetic.main.custom_default_alert.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityAnswers
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityQuestions
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions.DCActivityAns
import orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions.DCActivityQuestion
import orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions.DCTrainingActivity
import orionedutech.`in`.lmstrainerapp.network.response
import orionedutech.`in`.lmstrainerapp.noInternetSnackBar

class TrainerActivity : AppCompatActivity() {
    lateinit var heading: TextView
    lateinit var qcount: TextView
    lateinit var acount: TextView
    lateinit var topStatus: TextView
    lateinit var lowerStatus: TextView
    lateinit var animation: LottieAnimationView
    lateinit var nextQuestion: MaterialButton
    lateinit var countDownTimer: EasyCountDownTextview
    var extraViews: ArrayList<View> = ArrayList()
    var retries = 1
    lateinit var acountTV: TextView
    lateinit var qcountTV: TextView
    lateinit var timer: CountDownTimer
    lateinit var fragContainer: FrameLayout
    var chapterID = ""
    var activityIDS: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer)
        heading = name
        qcount = textView13
        acount = textView14
        topStatus = topstatus
        animation = lottie
        lowerStatus = data
        nextQuestion = next
        acountTV = textView17
        qcountTV = textView18
        countDownTimer = easyCountDownTextview
        extraViews =
            arrayListOf(qcount, acount, qcountTV, acountTV, heading, nextQuestion, countDownTimer)
        fragContainer = trainerAcitivityContainer
        val int = intent!!
        chapterID = int.getStringExtra("chapter_id")!!
        Handler().postDelayed({
            getActivityData(chapterID)
        }, 500)
    }

    fun showActivityViews() {
        extraViews.forEach {
            it.visibility = View.VISIBLE
        }
        addBorderandMargin(true)
    }

    private fun getActivityData(chapterID: String) {
        if (isFinishing) {
            return
        }
        runOnUiThread {
            animation.visibility = View.VISIBLE
            animation.playAnimation()
            lowerStatus.visibility = View.VISIBLE
            topStatus.visibility = View.VISIBLE
        }
        val json = JSONObject()
        json.put("chapter_id", chapterID)
        NetworkOps.post(Urls.trainingActivity, json.toString(), this, object : response {
            override fun onInternetfailure() {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    noInternetSnackBar(qcount)
                }
            }

            override fun onrespose(string: String?) {
                val activityData = Gson().fromJson(string, DCTrainingActivity::class.java)
                if (activityData == null) {
                    noDataAlert()
                    return
                }
                if (activityData.success == "1") {
                    val activityList = activityData.activity_data
                    if (activityList.isNullOrEmpty()) {
                        noDataAlert()
                    } else {
                        activityIDS.clear()
                        activityList.forEach { act ->
                            // todo add all data here
                            activityIDS.add(act.activity_id)
                            //single acitivities
                            val activityQuestions: ArrayList<DCActivityQuestion> = ArrayList()
                            activityQuestions.forEach {ques->

                                val answers: ArrayList<DCActivityAns> = ArrayList()
                                answers.forEach {
                                    
                                }
                            }
                        }
                        /*  val activityQuestions: ArrayList<DCActivityQuestion> = ArrayList()
                          activityQuestions.clear()*/

                        //val RoomQuestions : ArrayList<ActivityQuestions> = ArrayList()
                      //  val RoomActivities : ArrayList<ActivityAnswers> =  ArrayList()




/*
                        val answers: ArrayList<DCActivityAns> = ArrayList()
                        answers.clear()
                        activityQuestions.forEach { ques ->
                          //  RoomQuestions.add(ActivityQuestions(ques.activity_qns_id,ques.activity_qns_language,ques.activity_qns_type,ques.activity_qns_value,activityList[0].activity_id))
                            answers.addAll(ques.activity_ans)
                        }
                        *//*val answer_right_wrong: String,
                        val answer_value: String,
                        val question_ans_id: String,
                        val question_id: String,
                        val activity_id:String*//*
                        answers.forEach {ans->

                           // RoomActivities.add(ActivityAnswers(ans.answer_right_wrong,ans.answer_value,ans.question_ans_id,ans.question_id,""))
                        }*/


                    }
                } else {
                    runOnUiThread {
                        MaterialAlertDialogBuilder(this@TrainerActivity)
                            .setCancelable(false)
                            .setTitle("Error")
                            .setMessage(activityData.error)
                            .setPositiveButton("Go back") { dialogInterface, i ->
                                dialogInterface.dismiss()
                                finish()
                            }.create().show()
                    }

                }


            }

            override fun onfailure() {
                if (isFinishing) {
                    return
                }
                runOnUiThread {
                    animation.visibility = View.GONE
                    animation.cancelAnimation()
                    lowerStatus.visibility = View.GONE
                    topStatus.visibility = View.GONE

                    val dialogueView = LayoutInflater.from(this@TrainerActivity)
                        .inflate(R.layout.custom_default_alert, null, false)
                    val builder = MaterialAlertDialogBuilder(this@TrainerActivity)
                        .setView(dialogueView)
                        .setCancelable(false)
                    val dialog = builder.create()
                    val message: TextView = dialogueView.message
                    dialogueView.button.setOnClickListener {
                        dialog.dismiss()
                        retries = 1
                        timer.cancel()
                        getActivityData(chapterID)

                    }
                    val retryTime = 5000
                    timer = object : CountDownTimer((retryTime * retries).toLong(), 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            message.text =
                                String.format("Retrying in ${millisUntilFinished / 1000} seconds")
                            mLog.i(
                                mLog.TAG,
                                "dialog message \"Retrying in ${millisUntilFinished / 1000} seconds\""
                            )

                        }

                        override fun onFinish() {
                            if (retries <= 3) {
                                retries += 1
                            }
                            dialog.dismiss()
                            getActivityData(chapterID)
                        }
                    }
                    timer.start()
                    dialog.show()
                }
            }

        }) { _, _, _ -> }
    }

    private fun noDataAlert() {
        runOnUiThread {
            MaterialAlertDialogBuilder(this@TrainerActivity)
                .setCancelable(false)
                .setTitle("Error")
                .setMessage("No data was returned")
                .setPositiveButton("Go back") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                }.create().show()
        }
    }

    fun hideActivityViews() {
        extraViews.forEach {
            it.visibility = View.GONE
        }
    }

    fun addBorderandMargin(show: Boolean) {
        mLog.i(mLog.TAG, "show $show")
        fragContainer.background =
            ContextCompat.getDrawable(this, R.drawable.round_rect_white_stroke)
        val layoutParams = fragContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(16, 16, 16, 16)
        fragContainer.layoutParams = layoutParams
        fragContainer.requestLayout()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

}
