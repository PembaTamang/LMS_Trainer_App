package orionedutech.`in`.lmstrainerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview
import kotlinx.android.synthetic.main.activity_trainer.*
import kotlinx.android.synthetic.main.custom_default_alert.view.*
import kotlinx.android.synthetic.main.wrong_answer_alert.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONArray
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

    var lastAnswerID = ""
    var lastQuestionID = ""
    var lastQType: String = ""
    var lastActivityID = ""
    var activityQuestionsOver = false
    var activityOver = false
    lateinit var snackAnchor: CoordinatorLayout
    private var ft: FragmentTransaction? = null
    var allover = false
    var mainJson = JSONArray()
    var questionsJson = JSONObject()
    var questionType = JSONObject()
    var newActivity = false
    var isLastQuestionInActivity = false
    override fun answer(questionID: String, answerID: String) {
        lastAnswerID = answerID
        lastQuestionID = questionID
        mLog.i(TAG, "answerValue ${getAnswerValue(questionID, answerID)}")
    }


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
        snackAnchor = snackContainer
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

            topStatus.visibility = GONE
            if (activityIndex < activityIDS.size) {
                mLog.i(TAG, "number of questions : ${activityQuestionsID.size}")
                if (questionIndex < activityQuestionsID.size) {
                    isLastQuestionInActivity =
                        questionIndex == (activityQuestionsID.size - 1) && activityQuestionsID.size > 0

                    mLog.i(TAG, "1 activity index $activityIndex question index $questionIndex ")

                    if (firstQuestion) {
                        nextQuestion.text = "Next Question"
                        firstQuestion = false
                        //first question
                        changeFragment(
                            activityIDS[activityIndex],
                            activityQuestionsID[questionIndex],
                            questionIndex
                        )
                        questionIndex += 1

                    } else {
                        if (lastAnswerID == "") {
                            if (newActivity) {
                                newActivity = false
                                //first question for another activity
                                changeFragment(
                                    activityIDS[activityIndex],
                                    activityQuestionsID[questionIndex],
                                    questionIndex
                                )
                                questionIndex += 1

                                return@setOnClickListener
                            }
                            mToast.showToast(this, "please choose an answer")
                            return@setOnClickListener
                        }

                        processAnswer(false)

                    }

                } else {

                    if (isLastQuestionInActivity) {
                        processAnswer(true)
                        isLastQuestionInActivity = true
                        return@setOnClickListener
                    }
                    showQuestionsOverAlert()

                }
            } else {
                over()
            }
        }
    }

    private fun processAnswer(isLast: Boolean) {
        //add last answer and question value here

        val answerJson = JSONObject()
        answerJson.put(lastAnswerID, getAnswerValue(lastQuestionID, lastAnswerID))
        questionType = JSONObject()
        questionType.put(lastQType, answerJson)
        questionsJson.put(lastQuestionID, questionType)

        //show answer status
        val ans = getAnsString()
        var snackbar: Snackbar? = null
        when (ans) {
            "Correct Answer" -> {
                snackbar = Snackbar.make(snackAnchor, ans, Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                snackbar.addCallback(object : Snackbar.Callback() {
                    @Override
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {

                            if (activityIndex < activityIDS.size) {
                                if (!isLast) {
                                    changeFragment(
                                        activityIDS[activityIndex],
                                        activityQuestionsID[questionIndex],
                                        questionIndex
                                    )
                                    questionIndex += 1

                                } else {
                                    showQuestionsOverAlert()
                                }

                            }
                        }
                    }
                })
                snackbar.show()
            }
            "Incorrect Answer" -> {

                val correctAns =
                    getCorrectAnsString(activityQuestionsID[if (isLast) questionIndex - 1 else questionIndex])
                val dialogueView = LayoutInflater.from(this)
                    .inflate(R.layout.wrong_answer_alert, null, false)
                val builder = MaterialAlertDialogBuilder(this)
                    .setCancelable(false)
                    .setBackground(ContextCompat.getDrawable(this, android.R.color.transparent))
                    .setView(dialogueView)
                val dialogue = builder.create()
                val ok = dialogueView.buttonb
                val dmessage = dialogueView.messagee
                val message = String.format("The correct answer is $correctAns")
                dmessage.text = message
                ok.setOnClickListener {
                    dialogue.dismiss()
                    if (!isLast) {
                        if (activityIndex < activityIDS.size) {
                            changeFragment(
                                activityIDS[activityIndex],
                                activityQuestionsID[questionIndex],
                                questionIndex
                            )
                            questionIndex += 1
                        }
                    } else {
                        showQuestionsOverAlert()
                    }
                }
                dialogue.show()

            }
            else -> {
                showToast("answer error")
            }
        }
        if (!isLast) {
            lastAnswerID = ""
            lastQuestionID = ""
            lastQType = ""
        }
    }

    private fun showQuestionsOverAlert() {

        //adding last item json
        val answerJson = JSONObject()
        answerJson.put(lastAnswerID, getAnswerValue(lastQuestionID, lastAnswerID))
        questionType = JSONObject()
        questionType.put(lastQType, answerJson)
        questionsJson.put(lastQuestionID, questionType)

        //clearing fields
        lastAnswerID = ""
        lastQuestionID = ""
        lastQType = ""

        val dialogueView = LayoutInflater.from(this)
            .inflate(R.layout.custom_default_alert, null, false)
        val builder = MaterialAlertDialogBuilder(this)
            .setCancelable(true)
            .setView(dialogueView)
        val dialogue = builder.create()
        val ok = dialogueView.button
        ok.text = "ok"
        val dmessage = dialogueView.message
        val title = dialogueView.titlee
        title.text =
            String.format("Questions over for ${getActivitName(activityIDS[activityIndex])}")
        val message = "Press ok to continue"
        dmessage.text = message
        ok.setOnClickListener {
            dialogue.dismiss()
            questionIndex = 0
            activityIndex += 1
            activityOver = true

            if (activityIndex < activityIDS.size) {
                //get next activity questions

                activityQuestionsID = getQuestionsIdsByActivity(activityIDS[activityIndex])
                val activityJson = JSONObject()
                activityJson.put(activityIDS[activityIndex], questionsJson)
                val activityLabel = JSONObject()
                activityLabel.put("activity", activityJson)
                mainJson.put(activityIndex, activityLabel)
                questionType = JSONObject()
                questionsJson = JSONObject()
                activityOver = false

                lastAnswerID = ""
                lastQuestionID = ""
                lastQType = ""
                newActivity = true
                isLastQuestionInActivity = false

            }
            nextQuestion.callOnClick()
        }
        dialogue.show()



    }

    private fun getCorrectAnsString(qid: String): String {
        val countDownLatch = CountDownLatch(1)
        var value: String = ""
        CoroutineScope(IO).launch {
            applicationContext?.let {
                value = answersDao!!.getCorrectAnsString(qid)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return value
    }

    private fun getAnsString(): String {
        var ans = ""
        val res = getAnswerValue(lastQuestionID, lastAnswerID)
        ans = when (res) {
            "0" -> {
                "Incorrect Answer"
            }
            "1" -> {
                "Correct Answer"
            }
            else -> {
                "error"
            }
        }
        return ans
    }

    private fun over() {
        allover = true
        showToast("over")
        mLog.i(TAG, "json : $mainJson")
    }


    private fun changeFragment(aid: String, qid: String, sl: Int) {
        ft = supportFragmentManager.beginTransaction()
        ft!!.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )

        lastQType = getQuestionsTypeByID(aid, qid)
        when (lastQType) {
            "1" -> {
                val fragment = ActivityMCQFragment()
                val bundle = Bundle()
                bundle.putString("qid", qid)
                bundle.putString("sl", (sl + 1).toString())
                fragment.arguments = bundle
                ft!!.replace(R.id.trainerAcitivityContainer, fragment, "tag")
                ft!!.commit()
                updateCounters()

            }
            "2" -> {

                mToast.showToast(this, "new undefined data")

            }
            else -> {

            }
        }
    }

    private fun getQuestionsTypeByID(aid: String, qid: String): String {
        val countDownLatch = CountDownLatch(1)
        var type: String = ""
        CoroutineScope(IO).launch {
            applicationContext?.let {
                type = questionsDao!!.getQuestionsTypeByID(aid, qid)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return type
    }

    private fun getActivitName(activityID: String): String {
        val countDownLatch = CountDownLatch(1)
        var activity: String = ""
        CoroutineScope(IO).launch {
            applicationContext?.let {
                activity = activityDao!!.getActivityName(activityID)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return activity
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
                            mLog.i(TAG, "activity id size : ${activityIDS.size}")
                            activityQuestionsID =
                                getQuestionsIdsByActivity(activityIDS[activityIndex])
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
        val a = activityIndex
        val q = questionIndex
        acount.text = String.format("%d/%d", (a + 1), activityIDS.size)
        qcount.text = String.format("%d/%d", (q + 1), activityQuestionsID.size)
        heading.text = getActivitName(activityIDS[activityIndex])
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


    private fun getAnswerValue(questionID: String, answerID: String): String {
        val countDownLatch = CountDownLatch(1)
        var value: String = ""
        CoroutineScope(IO).launch {
            applicationContext?.let {
                value = answersDao!!.getAnswerValue(questionID, answerID)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return value

    }

}
