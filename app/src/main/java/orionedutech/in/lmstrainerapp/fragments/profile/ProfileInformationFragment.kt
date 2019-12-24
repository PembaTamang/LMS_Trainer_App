package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_profile_information.view.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class ProfileInformationFragment : BaseFragment() {
    lateinit var nameET: TextInputEditText
    lateinit var phoneET: TextInputEditText
    lateinit var emailET: TextInputEditText
    lateinit var centerET: TextInputEditText
    lateinit var dobET: TextInputEditText
    lateinit var submit: MaterialButton
    lateinit var editButton: TextView
    lateinit var animation:LottieAnimationView
    var arraylist: ArrayList<TextInputEditText> = ArrayList()

    var arraylistLines: ArrayList<ImageView> = ArrayList()
    var editing = false
    var colorStateListBlue: ColorStateList? = null
    var colorStateListGreen: ColorStateList? = null
    var name = ""
    var dob = ""
    var email = ""
    var phoneNumber = ""
    var center = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_information, container, false)
        val color = ContextCompat.getColor(context!!, R.color.blue)
        val green = ContextCompat.getColor(context!!, R.color.green)

        colorStateListBlue = ColorStateList.valueOf(color)
        colorStateListGreen = ColorStateList.valueOf(green)

        nameET = view.name
        phoneET = view.phone
        emailET = view.email
        centerET = view.centerName
        submit = view.updateProfile
        dobET = view.dob

        animation = view.lottie

        arraylist.addAll(arrayListOf(nameET, phoneET, emailET, centerET))
        disableETs()
        setProfileDataFromDB()
        arraylistLines.addAll(
            arrayListOf(
                view.line1,
                view.line2,
                view.line3,
                view.line4,
                view.line5
            )
        )
        editButton = view.edit
        editButton.setOnClickListener {
            if (!editing) {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.green))
                submit.isEnabled = true
                editing = true
                enableETs()
                showToast(context!!, "edit enabled")
            } else {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.default_text))
                editing = false
                submit.isEnabled = false
                disableETs()
                showToast(context!!, "edit disabled")
            }
        }

        nameET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                name = p0.toString()
            }

        })
        emailET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                email = p0.toString()
            }

        })
        phoneET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phoneNumber = p0.toString()
            }

        })

        centerET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                center = p0.toString()
            }

        })



        dobET.setOnClickListener {
            //show date dialogue
            mLog.i(TAG, "clicked")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                activity!!,
                R.style.DateDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year1, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    mLog.i(TAG, "$dayOfMonth ${monthOfYear + 1}, $year1")
                    dobET.setText(String.format("%d-%d%d", dayOfMonth, (monthOfYear + 1), year1))
                    dob = String.format("%d-%d%d", dayOfMonth, (monthOfYear + 1), year1)
                },
                year,
                month,
                day
            )
            dpd.setCancelable(false)
            dpd.show()
            val negButton: Button = dpd.getButton(DialogInterface.BUTTON_NEGATIVE)
            negButton.setBackgroundColor(ContextCompat.getColor(context!!, R.color.blue))


            val pButton: Button = dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
            pButton.setBackgroundColor(ContextCompat.getColor(context!!, R.color.blue))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 20, 0)
            negButton.layoutParams = params
        }

        submit.setOnClickListener {
            if(name.isNotBlank()&&dob.isNotBlank()&&email.isNotBlank()&&phoneNumber.isNotBlank()&&center.isNotBlank()) {
                submit.text = ""
                animation.visibility = VISIBLE
                animation.playAnimation()
                //make network call here

            }else{
                showToast(context,"Please fill all fields")
            }
        }
        return view
    }

    private fun setProfileDataFromDB() {
        launch {
            context?.let {

                val dao = MDatabase(it).getUserDao()
                val name = dao.getadminName()
                val phone = dao.getPhone()
                val email = dao.getEmail()
                val center = dao.getCenterName()
                val dob = dao.getDob()
                mLog.i(TAG, "$name $phone $email $center $dob")
                withContext(Main) {
                    nameET.setText(name)
                    phoneET.setText(phone)
                    emailET.setText(email)
                    centerET.setText(center)
                    dobET.setText(dob)
                }

            }
        }
    }

    private fun disableETs() {
        dobET.isClickable = true
        arraylist.forEach {
            it.isEnabled = false
        }
        arraylistLines.forEach {
            it.imageTintList = colorStateListBlue
        }
    }

    private fun enableETs() {
        dobET.isClickable = true
        arraylist.forEach {
            it.isEnabled = true
        }
        arraylistLines.forEach {
            it.imageTintList = colorStateListGreen
        }
    }

}
