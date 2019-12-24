package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.experience_layout.view.*
import kotlinx.android.synthetic.main.fragment_additional_information.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.mToast


/**
 * A simple [Fragment] subclass.
 */
class AdditionalInformationFragment : BaseFragment() {
    lateinit var experienceET: TextInputEditText
    lateinit var panET: TextInputEditText
    lateinit var aadharET: TextInputEditText
    lateinit var dojET: TextInputEditText
    lateinit var qualificationET: TextInputEditText
    lateinit var editButton : TextView
    var experience = ""
    var editing = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_additional_information, container, false)
        setDataFromDB()
        experienceET = view.experience
        editButton = view.edit

        experienceET.isEnabled = true
        experienceET.isClickable = true

        editButton.setOnClickListener {
            if (!editing) {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.green))
             //   submit.isEnabled = true
                editing = true
             //   enableETs()
                mToast.showToast(context!!, "edit enabled")
            } else {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.default_text))
                editing = false
            //    submit.isEnabled = false
                //disableETs()
                mToast.showToast(context!!, "edit disabled")
            }
        }

        experienceET.setOnClickListener {
            val numberPickerView =
                LayoutInflater.from(context).inflate(R.layout.experience_layout, null, false)
            val dialogBuilder = MaterialAlertDialogBuilder(context)
            dialogBuilder.setView(numberPickerView)
            val dialogue = dialogBuilder.create()
            val numberPicker = numberPickerView.picker
            numberPicker.minValue = 1
            numberPicker.maxValue = 30
            numberPickerView.ok.setOnClickListener {
                experience = numberPicker.value.toString()
                dialogue.dismiss()

            }
            dialogue.show()
        }
        panET = view.pan
        aadharET = view.aadhar
        dojET = view.doj
        qualificationET = view.qualification
        experienceET = view.experience
        return view
    }

    private fun setDataFromDB() {
        launch {
            context?.let {
                val dao = MDatabase(it).getUserDao()
                val pan = dao.getPan()
                val aadhar = dao.getAadhar()
                val doj = dao.getDoj()
                val qualification = dao.getLQualification()
                val experience = dao.getWorkExperience()
                withContext(Main) {
                    panET.setText(pan)
                    aadharET.setText(aadhar)
                    dojET.setText(doj)
                    qualificationET.setText(qualification)
                    experienceET.setText(experience)

                }

            }
        }
    }


}
