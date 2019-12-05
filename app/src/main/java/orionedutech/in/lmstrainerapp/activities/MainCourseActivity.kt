package orionedutech.`in`.lmstrainerapp.activities

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview
import kotlinx.android.synthetic.main.activity_main_course.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.fragments.mainCourse.ChapterFragment
import orionedutech.`in`.lmstrainerapp.interfaces.ShowActivityViews
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG

class MainCourseActivity : AppCompatActivity(), ShowActivityViews {
    var trainerID = ""
    var batchID = ""
    var courseID = ""
    var moduleID = ""
    var chapterID = ""
    var unitID = ""
    var subUnitID = ""
    var trainingID = ""
    var uniqueID = ""
    var chapterType = ""

    lateinit var heading: TextView
    lateinit var count: TextView
    lateinit var topStatus: TextView
    lateinit var lowerStatus: TextView
    lateinit var animation: LottieAnimationView
    lateinit var nextQuestion: MaterialButton
    lateinit var countDownTimer: EasyCountDownTextview

    lateinit var ft: FragmentTransaction
    lateinit var fragContainer : FrameLayout
    var extraViews: ArrayList<View> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_course)
        heading = name
        count = textView13
        topStatus = topstatus
        animation = lottie
        lowerStatus = data
        nextQuestion = next
        countDownTimer = easyCountDownTextview
        fragContainer = chapterContainer
        extraViews =
            arrayListOf(count, heading, topStatus, lowerStatus, nextQuestion, countDownTimer)
        val intent = intent!!
        trainerID = intent.getStringExtra("trainerID")!!
        trainingID = intent.getStringExtra("trainingID")!!
        uniqueID = intent.getStringExtra("uniqueID")!!
        courseID = intent.getStringExtra("courseID")!!
        moduleID = intent.getStringExtra("moduleID")!!
        chapterID = intent.getStringExtra("chapterID")!!
        batchID = intent.getStringExtra("batchID")!!
        unitID = intent.getStringExtra("unitID")!!
        subUnitID = intent.getStringExtra("subunitID")!!
        chapterType = intent.getStringExtra("chapter_type")!!
        mLog.i(TAG, "chapter type : $chapterType")



        when (chapterType) {
            "1" -> {
                val fragment = ChapterFragment()
                val bundle = Bundle()
                bundle.putString("trainerID", trainerID)
                bundle.putString("trainingID", trainingID)
                bundle.putString("uniqueID", uniqueID)
                bundle.putString("courseID", courseID)
                bundle.putString("moduleID", moduleID)
                bundle.putString("chapterID", chapterID)
                bundle.putString("batchID", batchID)
                bundle.putString("type", "1")
                fragment.arguments = bundle
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.chapterContainer, fragment)
                ft.commit()
            }
            else -> {
                val fragment = ChapterFragment()
                val bundle = Bundle()
                bundle.putString("trainerID", trainerID)
                bundle.putString("trainingID", trainingID)
                bundle.putString("uniqueID", uniqueID)
                bundle.putString("courseID", courseID)
                bundle.putString("moduleID", moduleID)
                bundle.putString("chapterID", chapterID)
                bundle.putString("batchID", batchID)
                bundle.putString("type", "2")
                fragment.arguments = bundle
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.chapterContainer, fragment)
                ft.commit()
            }
        }


    }

    fun showActivityViews() {
        extraViews.forEach {
            it.visibility = VISIBLE
        }
    }

    fun hideActivityViews() {
        extraViews.forEach {
            it.visibility = GONE
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

    override fun show(show: Boolean) {
        mLog.i(TAG, "show $show")
        //todo make views visible
        fragContainer.background = ContextCompat.getDrawable(this,R.drawable.round_rect_white_stroke)
        showActivityViews()
    }
}
