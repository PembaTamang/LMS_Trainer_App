package orionedutech.`in`.lmstrainerapp.fragments.assessment


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_trainer_assessment_upload.view.*
import orionedutech.`in`.lmstrainerapp.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AssessmentUploadFragment : Fragment() {
    private lateinit var centerSpinner: Spinner
    private lateinit var batchSpinner: Spinner
    private lateinit var userSpinner: Spinner
    private lateinit var exSpinner: Spinner
    private lateinit var qTypeSpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private lateinit var assessmentSpinner: Spinner
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var published: TextView
    private lateinit var selectedCenter: String
    private lateinit var selectedBatch: String
    private lateinit var selectedUser: String
    private lateinit var selectedExempted: String
    private lateinit var selectedType: String
    private lateinit var selectedTime: String
    private lateinit var selectedAssessment: String
    private lateinit var status: SwitchCompat
    private lateinit var calendar: Calendar

    private var centerList = ArrayList<String>()
    private lateinit var centerAdapter: ArrayAdapter<String>

    private var batchList = ArrayList<String>()
    private lateinit var batchAdapter: ArrayAdapter<String>

    private var userList = ArrayList<String>()
    private lateinit var userAdapter: ArrayAdapter<String>

    private var exList = ArrayList<String>()
    private lateinit var exAdapter: ArrayAdapter<String>

    private var qTypeList = ArrayList<String>()
    private lateinit var qTypeAdapter: ArrayAdapter<String>

    private var timeList = ArrayList<String>()
    private lateinit var timeAdapter: ArrayAdapter<String>

    private var assessmentList = ArrayList<String>()
    private lateinit var assessmentAdapter: ArrayAdapter<String>
    private lateinit var mcontext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trainer_assessment_upload, container, false)
        calendar = Calendar.getInstance(TimeZone.getDefault())
        centerSpinner = view.center
        mcontext = context!!
        centerAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, centerList)
        centerSpinner.adapter = centerAdapter

        batchSpinner = view.batch
        batchAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, batchList)
        batchSpinner.adapter = batchAdapter

        userSpinner = view.user
        userAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, userList)
        userSpinner.adapter = userAdapter

        exSpinner = view.exStudents
        exAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, exList)
        exSpinner.adapter = exAdapter


        qTypeSpinner = view.qType
        qTypeAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, qTypeList)
        qTypeSpinner.adapter = qTypeAdapter

        timeSpinner = view.time
        timeAdapter = ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, timeList)
        timeSpinner.adapter = timeAdapter

        assessmentSpinner = view.name
        assessmentAdapter =
            ArrayAdapter(mcontext, R.layout.spinner_item, R.id.text, assessmentList)
        assessmentSpinner.adapter = assessmentAdapter

        published = view.published
        status = view.mSwitch
        status.setOnCheckedChangeListener { _, b ->
            if (b) {
                published.text = "published"
                published.setTextColor(ContextCompat.getColor(mcontext, R.color.green))
            } else {
                published.text = "not published"
                published.setTextColor(ContextCompat.getColor(mcontext, R.color.orange))
            }
        }

        view.back.setOnClickListener {
            activity!!.onBackPressed()
        }
        startDate = view.start_date
        startDate.setOnClickListener {
            selectStartDate()
        }
        endDate = view.end_date
        endDate.setOnClickListener {
            selectEndDate()
        }

        view.start.setOnClickListener {
            selectStartDate()
        }
        view.end.setOnClickListener {
            selectEndDate()
        }

        view.create.setOnClickListener {
            // todo add checks according to api
        }
        return view
    }

    private fun selectStartDate() {
        DatePickerDialog(
            mcontext,
            R.style.datetheme,
            { _, i, i1, i2 ->
                val startDate = "$i2 - ${(i1 + 1)} - $i"
                this.startDate.text = startDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun selectEndDate() {

        DatePickerDialog(
            mcontext,
            R.style.datetheme,
            { _, i, i1, i2 ->
                val endDate = "$i2 - ${(i1 + 1)} - $i"
                this.endDate.text = endDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

    }
}
