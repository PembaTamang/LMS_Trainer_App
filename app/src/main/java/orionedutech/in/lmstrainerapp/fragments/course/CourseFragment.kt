package orionedutech.`in`.lmstrainerapp.fragments.course


import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_course.view.*
import kotlinx.android.synthetic.main.fragment_course.view.present
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.AttendanceAdapter
import orionedutech.`in`.lmstrainerapp.adapters.recyclerviews.NewAttendanceAdapter
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.AttendanceModel


/**
 * A simple [Fragment] subclass.
 */
class CourseFragment : Fragment() {

    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var allPresent: TextView
    var allPrsnt = true
    var arrayList: ArrayList<AttendanceModel> = ArrayList()
    lateinit var batchSpinner: Spinner
    lateinit var courseSpinner: Spinner
    lateinit var moduleSpinner: Spinner
    lateinit var chapterSpinner: Spinner

    // 1 module

    // 2 3 chapter

    // 4 unit


    // 5 sub unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course, container, false)
        allPresent = view.present




        recyclerView = view.shimmerRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = NewAttendanceAdapter(arrayList)
        recyclerView.adapter = adapter


        for (i in 0 until 20) {
            val student = AttendanceModel("name ${i + 1}", "id ${i + 1}", "present")
            arrayList.add(student)
        }
        adapter.notifyDataSetChanged()
        val rotatingCheck =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_green) }


        val rotatingCheck1 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_red) }

        allPresent.setOnClickListener {
            if (allPrsnt) {
                for (m in arrayList) {
                    m.status = "absent"
                }
                allPrsnt = false
                val animator =
                    ObjectAnimator.ofInt(rotatingCheck1, "level", 0, 10000).setDuration(500)
                animator.start()
                allPresent.text = "All Absent"
                allPresent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck1, null)

            } else {

                for (m in arrayList) {
                    m.status = "present"
                }

                allPrsnt = true
                val animator =
                    ObjectAnimator.ofInt(rotatingCheck, "level", 0, 10000).setDuration(500)
                animator.start()
                allPresent.text = "All Present"
                allPresent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck, null)

            }
            //adapter.clearSparseArray()
            adapter.notifyDataSetChanged()
        }

        view.markAttendance.setOnClickListener {
            val json = JSONObject()
            mLog.i(TAG, "size ${arrayList.size}")
            arrayList.forEach {
                json.put(it.id!!, it.status)
            }
            mLog.i(TAG, "json $json")
            MaterialAlertDialogBuilder(context).setTitle("Alert")
                .setMessage("Generated json : $json")
                .create().show()
        }

        return view
    }

}
