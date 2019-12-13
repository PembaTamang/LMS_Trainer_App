package orionedutech.`in`.lmstrainerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_assessment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.AssesmentQuestion
import orionedutech.`in`.lmstrainerapp.fragments.assessmentquestionTypes.AssessmentMCQFragment
import orionedutech.`in`.lmstrainerapp.interfaces.AssessmentAnswer
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.assessmentQuestions.DCAsseessmentQ
import orionedutech.`in`.lmstrainerapp.network.response
import orionedutech.`in`.lmstrainerapp.showToast
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AssessmentActivity : AppCompatActivity(), AssessmentAnswer {


    override fun answer(answer: String) {
        lastAnswerID = answer
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    private var ft: FragmentTransaction? = null
    private var lastAnswerID = ""
    lateinit var animation: LottieAnimationView
    lateinit var status: TextView
    lateinit var mainStatus: TextView
    lateinit var button: MaterialButton
    lateinit var questionTV: TextView
    lateinit var assessmentName: TextView

    lateinit var hourTV: TextView
    lateinit var minuteTV: TextView
    lateinit var secondsTV: TextView
    lateinit var fragContainter: FrameLayout
    lateinit var countDownTimer: CountDownTimer
    var totalQuestions = 0
    var currentQuestion = 0
    var firstQ = true
    var examOn = false
    var questionsarrayList: MutableList<AssesmentQuestion> = ArrayList()
    var lastquestionID = ""
    var answerjson = JSONObject()
    var questionJSON = JSONObject()
    var mainJSON = JSONObject()
    var oldjson = JSONObject()
    var timeinMins = ""
    var answerID: HashMap<String, String> = HashMap()
    var dual = false
    var extraViews: ArrayList<View> = ArrayList()
    var review = false
    var onfinish = false
    var assessmentID = ""
    var uid = ""
    var batchID = ""
    var centerID = ""
    var busy = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)
        assessmentID = intent.getStringExtra("assessmentID")!!
        uid = intent.getStringExtra("uid")!!
        batchID = intent.getStringExtra("batch_id")!!
        centerID = intent.getStringExtra("center_id")!!
        val json = JSONObject()

        assessmentName = name
        fragContainter = container
        mainStatus = topstatus
        hourTV = hours
        minuteTV = minutes
        secondsTV = seconds

        json.put("assesment_id", assessmentID)
        json.put("language_id", "1")
        json.put("user_id", uid)
        json.put("user_type", "3")

        button = next
        animation = lottie
        status = data
        button.isEnabled = false
        questionTV = textView13
        extraViews =
            arrayListOf(assessmentName, questionTV, button, hourTV, minuteTV, secondsTV, dot, dot1)

        button.setOnClickListener {
            if (currentQuestion < totalQuestions) {

                val type = questionsarrayList[currentQuestion].question_type
                val id = questionsarrayList[currentQuestion].assesment_question_id
                val qString = questionsarrayList[currentQuestion].assesment_question
                if (firstQ) {
                    firstQ = false
                    textView19.visibility = VISIBLE
                    if (!review) {
                        changeFragment(
                            type, id, qString
                            , currentQuestion, ""
                        )

                    } else {
                        changeFragment(
                            type, id, qString
                            , currentQuestion,
                            getAnswerIDfromOldJson(id)
                        )
                    }
                    button.text = "Next Question"
                    status.visibility = GONE
                    mainStatus.visibility = GONE

                    currentQuestion += 1
                    questionTV.text = String.format("%s / %s ", currentQuestion, totalQuestions)
                    lastquestionID = id
                    if (!review) {
                        startTimer()
                    }

                    return@setOnClickListener

                } else {
                    if (lastAnswerID == "") {
                        mToast.showToast(this, "please choose an answer")
                        return@setOnClickListener
                    }

                    mLog.i(TAG, "lastAnswerID $lastAnswerID")
                    answerjson = JSONObject()

                    answerjson.put(lastAnswerID, getAnswerValue(lastAnswerID))
                    questionJSON.put(lastquestionID, answerjson)

                    lastAnswerID = ""

                    mLog.i(TAG, "")
                    if (!review) {
                        changeFragment(
                            type, id, qString
                            , currentQuestion, ""
                        )

                    } else {
                        changeFragment(
                            type, id, qString
                            , currentQuestion,
                            getAnswerIDfromOldJson(id)
                        )


                    }
                    lastquestionID = id
                    currentQuestion += 1
                    questionTV.text = String.format(" %s / %s ", currentQuestion, totalQuestions)

                }

            } else {
                if (lastAnswerID == "") {
                    mToast.showToast(this, "please choose an answer")
                    return@setOnClickListener
                }
                mLog.i(TAG, "past null check")
                answerjson = JSONObject()

                answerjson.put(lastAnswerID, getAnswerValue(lastAnswerID))
                questionJSON.put(lastquestionID, answerjson)

                if (!review) {
                    MaterialAlertDialogBuilder(this).setTitle("Assessment Over")
                        .setCancelable(false)
                        .setMessage("Do you want to review your answers?")
                        .setPositiveButton("submit") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            mLog.i(TAG, "dialogue")
                            runSubmitCode()

                        }.setNegativeButton("review") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            oldjson = JSONObject()
                            oldjson = questionJSON
                            mLog.i(TAG, "copying")
                            questionJSON = JSONObject()
                            currentQuestion = 0
                            firstQ = true
                            lastAnswerID = ""
                            review = true
                            button.callOnClick()

                        }.create().show()
                } else {
                    busy = true
                    MaterialAlertDialogBuilder(this).setTitle("Assessment Over")
                        .setCancelable(false)
                        .setMessage("Do you want to review your answers?")
                        .setPositiveButton("submit") { dialogInterface, i ->
                            dialogInterface.dismiss()
                            mLog.i(TAG, "dialogue")
                            runSubmitCode()

                        }.create().show()
                    mLog.i(TAG, "assessment over")

                }


            }

        }


        getAssessmentQuestions(json.toString())


    }

    private fun runSubmitCode() {

        if (!review) {
            mainJSON.put("ans_data", questionJSON)
            mLog.i(TAG, "json - $questionJSON")
        } else {
            mLog.i(TAG, "old json - $oldjson")
            mLog.i(TAG, "new json - $questionJSON")

            val overwrittenJson = overwriteJson(questionJSON, oldjson)
            mainJSON.put("ans_data", overwrittenJson)
            mLog.i(TAG, "over written - $overwrittenJson")

        }

        mainJSON.put("assesment_id", assessmentID)
        mainJSON.put("language_id", "1")
        mainJSON.put("user_id", uid)
        mainJSON.put("assesment_unique_id", UUID.randomUUID())
        mainJSON.put("user_type", "3")
        mainJSON.put("batch_id", batchID)
        mainJSON.put("center_id", centerID)

        fragContainter.alpha = 0.0f
        animation.visibility = VISIBLE
        animation.playAnimation()
        status.visibility = VISIBLE
        mainStatus.text = "Assessment Over"
        mainStatus.visibility = VISIBLE
        status.text = "Pushing data to server...please wait"
        button.isEnabled = false
        uploadJson(mainJSON)

    }

    private fun startTimer() {
        timeinMins = "2"
        val minutes = timeinMins.toLong()
        countDownTimer = object : CountDownTimer(minutes * 60 * 1000, 1000) {
            override fun onFinish() {
                if (!onfinish) {
                  if(!busy){
                    onfinish = true
                    updateTimer("0", "0", "0")
                    showToast("time over")
                    mLog.i(TAG, "counter")
                    runSubmitCode()
                }
                }
            }

            override fun onTick(p0: Long) {
                val hours = getHours(p0)
                val mins = getMinutes(p0)
                val seconds = getSeconds(p0)
                updateTimer(hours, mins, seconds)
            }
        }
        countDownTimer.start()
    }

    private fun getAnswerIDfromOldJson(id: String): String {
        var answerID = ""
        val iterator = oldjson.keys()
        while (iterator.hasNext()) {
            val key2 = iterator.next()
            if (key2 == id) {
                val value2 = oldjson.getJSONObject(key2)
                answerID = value2.keys().next()
                mLog.i(TAG, "matching aid $answerID")
            }
        }
        return answerID
    }

    private fun overwriteJson(newJson: JSONObject, oldjson: JSONObject): JSONObject {
        val iterator = newJson.keys()
        while (iterator.hasNext()) {
            //json 2
            val key2 = iterator.next()
            val value2 = newJson.getJSONObject(key2)
            oldjson.remove(key2)
            oldjson.put(key2, value2)
        }
        return oldjson
    }

    private fun getTV(): TextView {
        val t = TextView(this@AssessmentActivity)
        t.textSize = 18f
        t.setTextColor(ContextCompat.getColor(this, R.color.white))
        return t
    }

    private fun updateTimer(hours: String, mins: String, seconds: String) {
        hourTV.text = String.format("%02d", hours.toInt())
        minuteTV.text = String.format("%02d", mins.toInt())
        secondsTV.text = String.format("%02d", seconds.toInt())

    }

    private fun getMinutes(p0: Long): String {
        return (TimeUnit.MILLISECONDS.toMinutes(p0) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(
                p0
            )
        )).toString()

    }

    private fun getHours(p0: Long): String {
        return TimeUnit.MILLISECONDS.toHours(p0).toString()

    }

    private fun getSeconds(p0: Long): String {
        return (TimeUnit.MILLISECONDS.toSeconds(p0) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                p0
            )
        )).toString()

    }

    private fun uploadJson(mainJSON: JSONObject) {
        countDownTimer.onFinish()
        countDownTimer.cancel()
        NetworkOps.post(Urls.assessmentAnsSubmit, mainJSON.toString(), this, object : response {
            override fun onrespose(string: String?) {
                mLog.i(TAG, "response : $string")
                if(string!!.isEmpty()){
                    runOnUiThread {
                        showToast("null response by server")
                    }
                    return
                }
                val json = JSONObject(string)
                if (json.getString("success") == "1") {
                    runOnUiThread {
                        examOn = false
                        //  mToast.showToast(this@AssessmentActivity, "answers successfully submitted")
                        animation.visibility = INVISIBLE
                        animation.cancelAnimation()
                        status.visibility = INVISIBLE
                        mainStatus.visibility = INVISIBLE
                        MaterialAlertDialogBuilder(this@AssessmentActivity).setTitle("Assessment Submitted Successfully")
                            .setCancelable(false)
                            .setMessage("press ok to exit")
                            .setPositiveButton("ok") { dialogInterface, i ->
                                dialogInterface.dismiss()
                                onBackPressed()
                            }.create().show()
                    }
                } else {
                    runUploadfailurecode()
                }
            }

            override fun onfailure() {
                runUploadfailurecode()
            }

            override fun onInternetfailure() {
                mToast.noInternetSnackBar(this@AssessmentActivity)
            }

        }) { _, _, _ ->

        }

    }

    private fun runUploadfailurecode() {
        runOnUiThread {
            mToast.showToast(this@AssessmentActivity, "submit failed...retrying again")
        }
        Handler().postDelayed({
            uploadJson(mainJSON)
        }, 1500)

    }

    private fun getAnswerValue(lastAnswerID: String): String {
        mLog.i(TAG, "last answer ID : $lastAnswerID")
        return answerID[lastAnswerID]!!
    }

    private fun getAssessmentQuestions(json: String) {
        animation.visibility = VISIBLE
        animation.playAnimation()
        status.visibility = VISIBLE

        NetworkOps.post(Urls.assessmentQuestionsUrl, json, this, object : response {
            override fun onrespose(string: String?) {
                mLog.i(TAG, "assessmesnt response : $string")
                val mainjson = Gson().fromJson(string, DCAsseessmentQ::class.java)

                if (mainjson == null) {
                    runFailureCode()
                    return
                }
                val database = MDatabase(this@AssessmentActivity)
                CoroutineScope(IO).launch {

                    val answerDao = database.getAssessmentAnswersDao()
                    if (mainjson.success == "1") {
                        timeinMins = mainjson.assesment_time
                        withContext(Main) {
                            assessmentName.text = mainjson.assesment_name
                        }
                        answerDao.deleteAssessmentAnswersTable()
                        val questionsList = mainjson.assesment_questions

                        questionsList.forEach { q ->
                            val questions = AssesmentQuestion(
                                q.assesment_question,
                                q.assesment_question_id,
                                q.assesment_id,
                                q.question_type
                            )
                            questionsarrayList.add(questions)
                            val answer = q.assesment_ans
                            answer.forEach { ans ->
                                answerID[ans.question_ans_id] = ans.answer_right_wrong
                                mLog.i(
                                    TAG,
                                    "map values ${ans.question_ans_id} : ${ans.answer_right_wrong} "
                                )
                            }
                            answerDao.insert(answer.toMutableList())
                        }

                        runOnUiThread {
                            totalQuestions = questionsarrayList.size
                            animation.visibility = GONE
                            animation.cancelAnimation()
                            status.text = "PRESS START TO BEGIN"
                            button.isEnabled = true
                            mainStatus.text =
                                "You have $timeinMins minutes to attempt $totalQuestions questions"
                            examOn = true

                            fragContainter.background =
                                ContextCompat.getDrawable(
                                    this@AssessmentActivity,
                                    R.drawable.round_rect_white_stroke
                                )
                            val layoutParams =
                                fragContainter.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(16, 16, 16, 16)
                            fragContainter.layoutParams = layoutParams
                            fragContainter.requestLayout()
                            extraViews.forEach {
                                it.visibility = VISIBLE
                            }


                        }

                    } else {
                        runOnUiThread {
                            animation.visibility = GONE
                            animation.cancelAnimation()
                            topstatus.visibility = GONE
                            status.visibility = GONE
                            MaterialAlertDialogBuilder(this@AssessmentActivity).setTitle("Alert")
                                .setCancelable(false)
                                .setMessage(mainjson.response)
                                .setPositiveButton("Go back") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    finish()
                                }.create().show()
                        }

                    }


                }

            }

            override fun onfailure() {

                runFailureCode()
            }

            override fun onInternetfailure() {

                runFailureCode()
                runOnUiThread {
                    mToast.noInternetSnackBar(this@AssessmentActivity)
                }
            }
        }) { _, _, _ ->

        }

    }

    private fun runFailureCode() {
        runOnUiThread {
            animation.visibility = GONE
            animation.cancelAnimation()
            status.visibility = GONE
            mToast.showToast(this, "failed")
        }
    }

    private fun changeFragment(
        type: String,
        qid: String,
        qString: String,
        sl: Int,
        lastaid: String
    ) {
        ft = supportFragmentManager.beginTransaction()
        ft!!.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        when (type) {
            "1" -> {
                val fragment = AssessmentMCQFragment()
                val bundle = Bundle()
                bundle.putString("qid", qid)
                bundle.putString("qString", qString)
                bundle.putString("sl", (sl + 1).toString())
                if (!lastaid.isNullOrEmpty()) {
                    bundle.putBoolean("review", true)
                    bundle.putString("aid", lastaid)
                } else {
                    bundle.putBoolean("review", false)
                }
                fragment.arguments = bundle
                ft!!.replace(R.id.container, fragment, "tag")
                ft!!.commit()
                dual = false

            }
            "2" -> {
                dual = true
                mToast.showToast(this, "new undefined data")

            }
            else -> {

            }
        }
    }

    override fun onBackPressed() {
        if (examOn) {
            mToast.showToast(this, "Please complete the assessment before leaving")
        } else {
            super.onBackPressed()
        }
    }
}
