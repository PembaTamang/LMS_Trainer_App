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
import androidx.fragment.app.FragmentTransaction
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
import orionedutech.`in`.lmstrainerapp.*
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityAnswerDao
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityDao
import orionedutech.`in`.lmstrainerapp.database.dao.ActivityQuestionsDao
import orionedutech.`in`.lmstrainerapp.database.entities.Activities
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityAnswers
import orionedutech.`in`.lmstrainerapp.database.entities.ActivityQuestions
import orionedutech.`in`.lmstrainerapp.fragments.activityquestionTypes.ActivityMCQFragment
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAnswer
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions.DCActivityQuestion
import orionedutech.`in`.lmstrainerapp.network.dataModels.activityQuestions.DCTrainingActivity
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.concurrent.CountDownLatch

class TrainerActivity : AppCompatActivity(), ActivityAnswer {
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
    var activityQuestionsID: ArrayList<String> = ArrayList()
    var questionIndex = 0
    var activityIndex = 0
    var db: MDatabase? = null
    var questionsDao: ActivityQuestionsDao? = null
    var answersDao: ActivityAnswerDao? = null
    var activityDao: ActivityDao? = null
    var currentActivityID = ""
    var totalQuestionsInActivity = 0
    var firstQuestion = true

    var lastAnwerID = ""
    var lastActivityID = ""
    var activityQuestionsOver = false
    var activityOver = false
    private var ft: FragmentTransaction? = null
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
        extraViews = arrayListOf(qcount, acount, qcountTV, acountTV, heading, nextQuestion)
        fragContainer = trainerAcitivityContainer
        db = MDatabase(this)
        questionsDao = db!!.getQuestionsDao()
        answersDao = db!!.getAnswersDao()
        activityDao = db!!.getActivityDao()

        val int = intent!!
        chapterID = int.getStringExtra("chapter_id")!!
        Handler().postDelayed({
            getActivityData(chapterID)
        }, 500)

        nextQuestion.setOnClickListener {

            mLog.i(TAG, "activity id : $currentActivityID index : $activityIndex")
            mLog.i(
                TAG,
                "question id : ${activityQuestionsID[questionIndex]} index : $questionIndex"
            )
            if (firstQuestion) {
                nextQuestion.text = "Next"
                changeFragment(getLastIdByIndex(),activityQuestionsID[questionIndex])
                firstQuestion = false
            } else {
                //load by checking if questions are over or if activities are over or not
                if (activityIndex <= activityIDS.size) {
                    //activity within index
                    if (questionIndex < activityQuestionsID.size) {
                       //question within index
                        changeFragment(getLastIdByIndex(),activityQuestionsID[questionIndex])
                        questionIndex+=1

                    } else {
                        //question out of index so increase activity index
                        activityIndex += 1
                        mLog.i(TAG,"getting new questions list")
                        // new questionsIDList
                        activityQuestionsID = getQuestionsIdsByActivity(getLastIdByIndex())
                        changeFragment(getLastIdByIndex(),activityQuestionsID[questionIndex])
                        questionIndex+=1

                    }
                } else {
                    //finished
                    showToast("assessment over")
                    activityOver = true
                }
            }


        }
    }

    private fun getLastIdByIndex(): String {
        return activityIDS[activityIndex]
    }



    private fun changeFragment(aid: String, qid: String) {
        ft = supportFragmentManager.beginTransaction()
        ft!!.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        val type = questionsDao!!.getQuestionsTypeByID(aid, qid)
        when (type) {
            "1" -> {
                val fragment = ActivityMCQFragment()
                val bundle = Bundle()
                bundle.putString("qid", qid)
                fragment.arguments = bundle
                ft!!.replace(R.id.container, fragment, "tag")
                ft!!.commit()

            }
            "2" -> {

                mToast.showToast(this, "new undefined data")

            }
            else -> {

            }
        }
    }

    private fun getActivitName(activityID: String): String {
        return activityDao!!.getActivityName(activityID)
    }

    private fun getQuestionsIdsByActivity(activityID: String): ArrayList<String> {
        val countDownLatch = CountDownLatch(1)
        var questions = ArrayList<String>()
        CoroutineScope(IO).launch {
            applicationContext?.let {
                questions = questionsDao!!.getQuestionsByActivityID(activityID) as ArrayList<String>
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return questions
    }

    fun showActivityViews() {

        addBorderandMargin(true)
        extraViews.forEach {
            it.visibility = View.VISIBLE
        }
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

        //  json.put("chapter_id", chapterID) //todo change later
        json.put("chapter_id", "75")
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
                        val RoomQuestions: ArrayList<ActivityQuestions> = ArrayList()
                        val RoomAnswers: ArrayList<ActivityAnswers> = ArrayList()
                        val RoomActivities: ArrayList<Activities> = ArrayList()
                        activityIDS.clear()
                        activityList.forEach { act ->
                            activityIDS.add(act.activity_id)
                            RoomActivities.add(
                                Activities(
                                    act.activity_chapter_id,
                                    act.activity_description,
                                    act.activity_id,
                                    act.activity_name
                                )
                            )
                            val activityQuestions: List<DCActivityQuestion> = act.activity_questions
                            activityQuestions.forEach { ques ->
                                RoomQuestions.add(
                                    ActivityQuestions(
                                        ques.activity_qns_id,
                                        ques.activity_qns_language,
                                        ques.activity_qns_type,
                                        ques.activity_qns_value,
                                        act.activity_id
                                    )
                                )
                                val answers = ques.activity_ans
                                answers.forEach { ans ->
                                    RoomAnswers.add(
                                        ActivityAnswers(
                                            ans.answer_right_wrong,
                                            ans.answer_value,
                                            ans.question_ans_id,
                                            ans.question_id,
                                            act.activity_id
                                        )
                                    )
                                }
                            }
                        }
                        CoroutineScope(IO).launch {
                            applicationContext?.let {
                                questionsDao!!.insert(RoomQuestions.toMutableList())
                                answersDao!!.insert(RoomAnswers.toMutableList())
                                activityDao!!.insert(RoomActivities.toMutableList())
                            }
                        }
                        runOnUiThread {
                            animation.visibility = View.GONE
                            animation.cancelAnimation()
                            lowerStatus.visibility = View.GONE
                            topStatus.text = "Press start to begin"
                            currentActivityID = getLastIdByIndex()
                            activityQuestionsID = questionsDao!!.getQuestionsByActivityID(currentActivityID) as ArrayList<String>
                            updateCounters()

                            showActivityViews()

                        }
                    }
                } else {
                    runOnUiThread {

                        animation.visibility = View.GONE
                        animation.cancelAnimation()
                        lowerStatus.visibility = View.GONE
                        topStatus.visibility = View.GONE

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

    private fun updateCounters() {
        acountTV.text = String.format( "%d/%d",activityIndex,activityIDS.size)
        qcountTV.text = String.format( "%d/%d",questionIndex,activityQuestionsID.size)
    }


    private fun noDataAlert() {
        runOnUiThread {
            animation.visibility = View.GONE
            animation.cancelAnimation()
            lowerStatus.visibility = View.GONE
            topStatus.visibility = View.GONE

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

    override fun answer(questionID: String, answerID: String) {
        mLog.i(TAG, "answerValue ${answersDao!!.getAnswerValue(questionID, answerID)}")
    }

}
