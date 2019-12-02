package orionedutech.`in`.lmstrainerapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import ir.samanjafari.easycountdowntimer.CountDownInterface
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview
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
import orionedutech.`in`.lmstrainerapp.fragments.questionTypes.MCQFragment
import orionedutech.`in`.lmstrainerapp.interfaces.ActivityAns
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.assessmentQuestions.DCAsseessmentQ
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AssessmentActivity : AppCompatActivity(), ActivityAns, CountDownInterface {
    override fun onFinish() {
    }

    override fun onTick(time: Long) {

    }

    override fun answer(answer: String) {
        lastAnswerID = answer
    }

    override fun finish() {
        super.finish()
    overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right)
    }
    private var ft: FragmentTransaction? = null
    private var lastAnswerID = ""
    lateinit var animation: LottieAnimationView
    lateinit var status: TextView
    lateinit var mainStatus: TextView
    lateinit var button: MaterialButton
    lateinit var questionTV: TextView
    lateinit var countdown: EasyCountDownTextview
    lateinit var assessmentName: TextView
    lateinit var fragContainter: FrameLayout
    var totalQuestions = 0
    var currentQuestion = 0
    var firstQ = true
    var questionsarrayList: MutableList<AssesmentQuestion> = ArrayList()
    var lastquestionID = ""
    var answerjson = JSONObject()
    var questionJSON = JSONObject()
    var mainJSON = JSONObject()
    var timeinMins = ""
    var answerID: HashMap<String, String> = HashMap()
    var dual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)
        title = "Trainer Assessment"
        val assessmentID = intent.getStringExtra("assessmentID")!!
        val uid = intent.getStringExtra("uid")!!
        val batchID = intent.getStringExtra("batch_id")!!
        val centerID = intent.getStringExtra("center_id")!!
        val json = JSONObject()
        countdown = easyCountDownTextview
        assessmentName = name
        fragContainter = container
        mainStatus = topstatus
        json.put("assesment_id", assessmentID)
        json.put("language_id", "1")
        json.put("user_id", uid)
        json.put("user_type", "3")
        button = next
        animation = lottie
        status = data
        button.isEnabled = false
        questionTV = textView13
        button.setOnClickListener {
            if (currentQuestion < totalQuestions) {

                val type = questionsarrayList[currentQuestion].question_type
                val id = questionsarrayList[currentQuestion].assesment_question_id
                val qString = questionsarrayList[currentQuestion].assesment_question
                if (firstQ) {
                    firstQ = false
                    changeFragment(
                        type,
                        id, qString
                    )
                    button.text = "Next"
                    status.visibility = GONE
                    currentQuestion += 1
                    questionTV.text = String.format("%s / %s ", currentQuestion, totalQuestions)
                    lastquestionID = id
                    countdown.setOnTick(this)
                    countdown.setTime(0, timeinMins.toInt(), 0)
                    countdown.startTimer()
                    return@setOnClickListener

                } else {
                    if (lastAnswerID == "") {
                        mToast.showToast(this, "please choose an answer")
                        return@setOnClickListener
                    }
                    mLog.i(TAG, "past null check")
                    answerjson = JSONObject()

                    answerjson.put(lastAnswerID, getAnswerValue(lastAnswerID))
                    questionJSON.put(lastquestionID, answerjson)

                    lastAnswerID = ""

                    mLog.i(TAG, "")
                    changeFragment(
                        type, id, qString
                    )
                    lastquestionID = id
                    currentQuestion += 1

                    questionTV.text = String.format("%s / %s ", currentQuestion, totalQuestions)

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

                mainJSON.put("assesment_id", assessmentID)
                mainJSON.put("language_id", "1")
                mainJSON.put("user_id", uid)
                mainJSON.put("assesment_unique_id", UUID.randomUUID())
                mainJSON.put("user_type", "3")
                mainJSON.put("batch_id", batchID)
                mainJSON.put("center_id", centerID)
                mainJSON.put("ans_data", questionJSON)
                countdown.stopTimer()
                mLog.i(TAG, "json : $mainJSON")
                countdown.alpha = 0.2f
                fragContainter.visibility = GONE
                animation.visibility = VISIBLE
                animation.playAnimation()
                status.visibility = VISIBLE
                mainStatus.text = "Assessment Over"
                mainStatus.visibility = VISIBLE
                status.text = "Pushing data to server...please wait"
                button.visibility = GONE
                uploadJson(mainJSON)
            }

        }


        getAssessmentQuestions(json.toString())


    }

    private fun uploadJson(mainJSON: JSONObject) {

        NetworkOps.post(Urls.assessmentAnsSubmit, mainJSON.toString(), this, object : response {
            override fun onrespose(string: String?) {
                mLog.i(TAG, "response : $string")
                val json = JSONObject(string!!)
                if (json.getString("success") == "1") {
                    runOnUiThread {
                        mToast.showToast(this@AssessmentActivity, "answers successfully submitted")
                        onBackPressed()
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
                                animation.visibility = GONE
                                animation.cancelAnimation()
                                status.text = "PRESS START TO BEGIN"
                                button.isEnabled = true
                                mainStatus.visibility = GONE
                                totalQuestions = questionsarrayList.size

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

    private fun changeFragment(type: String, qid: String, qString: String) {
        ft = supportFragmentManager.beginTransaction()
        ft!!.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        when (type) {
            "1" -> {
                val fragment = MCQFragment()
                val bundle = Bundle()
                bundle.putString("qid", qid)
                bundle.putString("qString", qString)
                fragment.arguments = bundle
                ft!!.replace(R.id.container, fragment, "tag")
                ft!!.commit()
                dual = false

            }
            "2" -> {
                dual = true

            }
            else -> {

            }
        }
    }
}
