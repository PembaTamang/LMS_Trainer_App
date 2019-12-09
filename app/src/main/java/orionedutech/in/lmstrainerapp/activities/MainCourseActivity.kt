package orionedutech.`in`.lmstrainerapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main_course.*
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.fragments.mainCourse.ChapterFragment
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG


class MainCourseActivity : AppCompatActivity() {
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
    var courseName = ""

    lateinit var courseNameTV: TextView
    lateinit var ft: FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_course)

        courseNameTV = titleText

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
        courseName = intent.getStringExtra("course_name")!!
        courseNameTV.text = courseName
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
                ft.add(R.id.chapterContainer, fragment)
                ft.addToBackStack(null)
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
                ft.add(R.id.chapterContainer, fragment)
                ft.addToBackStack(null)
                ft.commit()
            }
        }


    }




    override fun onBackPressed() {
        mLog.i(TAG, "clicked")
        mLog.i(TAG, " count ${supportFragmentManager.backStackEntryCount}")
        val fragCount = supportFragmentManager.backStackEntryCount
        when {
            fragCount > 1 -> {
                supportFragmentManager.popBackStack()
            }
            fragCount == 1 -> {
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

}
