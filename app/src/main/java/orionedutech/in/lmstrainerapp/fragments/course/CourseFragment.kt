package orionedutech.`in`.lmstrainerapp.fragments.course


import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.model.AttendanceModel


/**
 * A simple [Fragment] subclass.
 */
class CourseFragment : Fragment() {

    lateinit var recyclerView: ShimmerRecyclerView
    lateinit var allPresent: TextView
    lateinit var allAbsent: TextView

    var arrayList: ArrayList<AttendanceModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course, container, false)
        allPresent = view.present
        allAbsent = view.absent
        recyclerView = view.shimmerRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = AttendanceAdapter(arrayList)
        recyclerView.adapter = adapter


        for (i in 0 until 10) {
            val student = AttendanceModel("name ${i + 1}", "id ${i + 1}", "present")
            arrayList.add(student)
        }
        adapter.notifyDataSetChanged()
        val rotatingCheck =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_green) }
        allPresent.setOnClickListener {
            for (m in arrayList) {
                m.status = "present"

            }
            adapter.clearSparseArray()
            adapter.notifyDataSetChanged()
            val animator = ObjectAnimator.ofInt(rotatingCheck, "level", 0, 10000).setDuration(500)
            animator.start()
            allPresent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck, null)
        }

        val rotatingCheck1 =
            context?.let { ContextCompat.getDrawable(it, R.drawable.rotating_check_red) }
        allAbsent.setOnClickListener {
            for (m in arrayList) {
                m.status = "absent"
            }
            adapter.clearSparseArray()
            adapter.notifyDataSetChanged()
            val animator = ObjectAnimator.ofInt(rotatingCheck1, "level", 0, 10000).setDuration(500)
            animator.start()
            allAbsent.setCompoundDrawablesWithIntrinsicBounds(null, null, rotatingCheck1, null)
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
