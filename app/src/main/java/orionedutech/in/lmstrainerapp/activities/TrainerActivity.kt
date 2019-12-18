package orionedutech.`in`.lmstrainerapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_trainer.*
import kotlinx.android.synthetic.main.custom_default_alert.view.*
import kotlinx.android.synthetic.main.right_answer_alert.view.*
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
import orionedutech.`in`.lmstrainerapp.fragments.AnswersFragment
import orionedutech.`in`.lmstrainerapp.fragments.activityquestionTypes.ActivityMCQFragment
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAnswer
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.activityAnswer.ActivityAnswersModel
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
    var questionList = JSONArray()
    var mainContainer = JSONObject()
    var newActivity = false
    var isLastQuestionInActivity = false

    var totalQuestions = 0
    var correctAnswers = 0
    var incorrectAnswers = 0

    lateinit var activityAnswerPref : SharedPreferences

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
        extraViews = arrayListOf(qcount, acount, qcountTV, acountTV, heading, nextQuestion)
        fragContainer = trainerAcitivityContainer
        snackAnchor = snackContainer
        db = MDatabase(this)
        questionsDao = db!!.getQuestionsDao()
        answersDao = db!!.getAnswersDao()
        activityDao = db!!.getActivityDao()

        val int = intent!!
        chapterID = int.getStringExtra("chapter_id")!!
        mLog.i(TAG,"chapterID $chapterID")
        activityAnswerPref = getSharedPreferences("activityanswers", Context.MODE_PRIVATE)

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

                        if (lastAnswerID == "") {
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
    private fun getAnsString(): String {
        var ans = ""
        val res = getAnswerValue(lastQuestionID, lastAnswerID)
        mLog.i(TAG,"response $res")
        if(res.isNullOrEmpty()){
            return ""
        }
        ans = when (res) {
            "0" -> {
                "Thik chaina"
            }
            "1" -> {
                "Thikai cha"
            }
            else -> {
                "error"
            }
        }
        return ans
    }
    private fun getQuestionString(qid: String): String {
        val countdown = CountDownLatch(1)
        var question = ""
        CoroutineScope(IO).launch {
            question = questionsDao!!.getQuestionString(qid)
            countdown.countDown()
        }
        countdown.await()
        return question
    }
    private fun processAnswer(isLast: Boolean) {

        //add last answer and question value here

        mLog.i(TAG,"last answer ID : $lastAnswerID   is last : $isLast" )
        val answerJson = JSONObject()
        val answer = getAnswerValue(lastQuestionID, lastAnswerID)
        answerJson.put(lastAnswerID,answer)
        answerJson.put("answer",getAnswerString(lastAnswerID))
        answerJson.put("correctAnswer",getCorrectAnsString(lastQuestionID))
        answerJson.put("question",getQuestionString(lastQuestionID))
        answerJson.put("status",getAnswerValue(lastQuestionID, lastAnswerID))
        totalQuestions++
        when (answer){
            "0"-> incorrectAnswers++

            "1"-> correctAnswers++
        }
        //answerJson.put("question_type",lastQType)
        //answerJson.put("question_id",lastQuestionID)

        questionList.put(answerJson)

        //show answer status
        val ans = getAnsString()
        if(ans.isNullOrEmpty()){
            mLog.i(TAG,"answer error from function")
            return
        }
      //  var snackbar: Snackbar? = null
        when (ans) {
            "Thikai cha" ->
            {

                val dialogueView = LayoutInflater.from(this)
                    .inflate(R.layout.right_answer_alert, null, false)
                val builder = MaterialAlertDialogBuilder(this,R.style.mAlertDialogTheme1)
                    .setCancelable(false)
                    .setView(dialogueView)
                val dialogue = builder.create()
                val ok = dialogueView.button1
                val dmessage = dialogueView.message1
                val message = String.format(" ${getAnswerString(lastAnswerID)} is the correct answer ")
                dmessage.text = message
                ok.setOnClickListener {
                    dialogue.dismiss()
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
                dialogue.show()

            }
            "Thik chaina" -> {

                val correctAns =
                    getCorrectAnsString(activityQuestionsID[if (isLast) questionIndex - 1 else questionIndex])
                val dialogueView = LayoutInflater.from(this)
                    .inflate(R.layout.wrong_answer_alert, null, false)
                val builder = MaterialAlertDialogBuilder(this,R.style.mAlertDialogTheme1)
                    .setCancelable(false)
                          .setView(dialogueView)
                val dialogue = builder.create()
                val ok = dialogueView.buttonb
                val dmessage = dialogueView.messagee
                val message = String.format(" The correct answer is $correctAns ")
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


            lastAnswerID = ""
            lastQuestionID = ""
            lastQType = ""

    }
    private fun getAnswerString(aid:String):String{
        val countDownLatch = CountDownLatch(1)
        var value: String = ""
        CoroutineScope(IO).launch {
            applicationContext?.let {
                value = answersDao!!.getAnsString(aid)
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return value
    }
    private fun showQuestionsOverAlert() {

        val dialogueView = LayoutInflater.from(this)
            .inflate(R.layout.custom_default_alert, null, false)
        val builder = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setView(dialogueView)
        val dialogue = builder.create()
        val ok = dialogueView.button2
        ok.text = "ok"
        val dmessage = dialogueView.message
        val title = dialogueView.titlee
        title.text =
            String.format("Questions over for ${getActivitName(activityIDS[activityIndex])}")
        val message = "Press ok to continue"
        dmessage.text = message
        ok.setOnClickListener {
            dialogue.dismiss()


            //adding last item before changes apply
            val activitylistContainer = JSONObject()
            activitylistContainer.put("activity_data",questionList)
           // activitylistContainer.put("activity_id", activityIDS[activityIndex])
            activitylistContainer.put("activity_name",getActivitName(activityIDS[activityIndex]))
            activitylistContainer.put("total_questions",totalQuestions)
            activitylistContainer.put("correct_answers",correctAnswers)
            activitylistContainer.put("incorrect_answers",incorrectAnswers)

            mainJson.put(activitylistContainer)
            questionList = JSONArray()

            lastAnswerID = ""
            lastQuestionID = ""
            totalQuestions = 0
            correctAnswers = 0
            incorrectAnswers = 0
            lastQType = ""
            newActivity = true
            isLastQuestionInActivity = false


            questionIndex = 0
            activityIndex += 1
            activityOver = true

            if (activityIndex < activityIDS.size) {
                //get next activity questions
                activityQuestionsID = getQuestionsIdsByActivity(activityIDS[activityIndex])
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
                value = answersDao!!.getCorrectAnsString(qid,"1")
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        return value
    }



    private fun over() {
        allover = true

        mainContainer.put("data",mainJson)
        mLog.i(TAG, "json : $mainContainer")
        //show answers in answer fragment
        val data = Gson().fromJson(mainContainer.toString(),ActivityAnswersModel::class.java)

        MaterialAlertDialogBuilder(this).setTitle("All activities are over.")
            .setCancelable(false)
            .setMessage("Press ok to view your result")
            .setPositiveButton("ok"){
                dialogInterface, i ->
                dialogInterface.dismiss()
                hideActivityViews()
                ft = supportFragmentManager.beginTransaction()
                ft!!.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                val fragment = AnswersFragment()
                val bundle = Bundle()
                bundle.putParcelable("data",data)
                fragment.arguments = bundle
                ft!!.replace(R.id.trainerAcitivityContainer, fragment, "tag")
                ft!!.commit()

            }.create().show()







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
                bundle.putString("aid",aid)
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
        //todo remove this later
      //  json.put("chapter_id", chapterID)
        json.put("chapter_id","75")
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
                if(string.isNullOrEmpty()){
                    MaterialAlertDialogBuilder(this@TrainerActivity).setTitle("Alert")
                        .setCancelable(false)
                        .setMessage("No questions found.")
                        .setPositiveButton("Exit"){
                            dialogInterface, i ->
                            dialogInterface.dismiss()
                            onBackPressed()
                        }.create().show()
                    return
                }
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
                    dialogueView.button2.setOnClickListener {
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
       /* fragContainer.background =
            ContextCompat.getDrawable(this, android.R.color.transparent)
        val layoutParams = fragContainer.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        fragContainer.layoutParams = layoutParams
        fragContainer.requestLayout()*/
        extraViews.removeAt(extraViews.size-2)
        heading.text = "Activity Result"
        extraViews.forEach {
            it.visibility = GONE
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

    override fun onBackPressed() {
        if(!allover){
            showToast("Please complete the activity before exiting")
        }else{
            MaterialAlertDialogBuilder(this).setTitle("Alert")
                .setMessage("Do you want to go back to the previous screen?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                }
                .setNegativeButton("cancel") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }.create().show()
        }
    }
}
