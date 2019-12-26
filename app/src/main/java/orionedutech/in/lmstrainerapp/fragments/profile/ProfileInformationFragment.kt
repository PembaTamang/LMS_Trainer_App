package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.dao.UserDao
import orionedutech.`in`.lmstrainerapp.fragments.BaseFragment
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast.showToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.*
import java.util.concurrent.CountDownLatch
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
    lateinit var animation: LottieAnimationView
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
    var trainer_id = ""
    var db: MDatabase? = null
    var userDao: UserDao? = null
    var busy = false
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

        arraylist.addAll(arrayListOf(nameET, phoneET, emailET))
        disableETs()
        db = MDatabase(context!!)
        userDao = db!!.getUserDao()
        trainer_id = getTrainerID()
        setProfileDataFromDB()
        arraylistLines.addAll(
            arrayListOf(
                view.line1,
                view.line2,
                view.line3,
                view.line4
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
                    dobET.setText(String.format("%d-%d-%d", dayOfMonth, (monthOfYear + 1), year1))
                    dob = String.format("%d-%d-%d", dayOfMonth, (monthOfYear + 1), year1)
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
            if (!busy) {
                busy = true
                if (name.isNotBlank() && dob.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank()) {
                    submit.text = ""
                    animation.visibility = VISIBLE
                    animation.playAnimation()
                    //make network call here
                    val json = JSONObject()
                    json.put("user_id", trainer_id)
                    json.put("user_name", name)
                    json.put("user_dob", dob)
                    json.put("user_email", email)
                    json.put("user_ph", phoneNumber)
                  //  json.put("user_center", center)
                    json.put("user_pan", "")
                    json.put("user_aadhar", "")
                    json.put("user_doj", "")
                    json.put("user_qualification", "")
                    json.put("user_exp", "")

                    mLog.i(TAG, "json ${json.toString()}")
                    NetworkOps.post(
                        Urls.profileUpdate,
                        json.toString(),
                        context,
                        object : response {
                            override fun onInternetfailure() {
                            busy = false
                            }

                            override fun onrespose(string: String?) {
                                mLog.i(TAG,"reponse profile update $string")
                                if (string.isNullOrEmpty()) {
                                    onfailure()
                                    return
                                }
                                val jsonObject = JSONObject(string)
                                if (jsonObject.length() == 0) {
                                    onfailure()
                                    return
                                }
                                if (jsonObject.getString("success") == "1") {
                                    CoroutineScope(IO).launch {
                                        delay(2500)
                                        userDao!!.updateProfileInfo(
                                            name,
                                            phoneNumber,
                                            center,
                                            email,
                                            dob
                                        )
                                        setProfileDataFromDB()
                                        disableETs()
                                        editing = false
                                        if(activity==null){
                                            return@launch
                                        }
                                        activity!!.runOnUiThread {
                                            showToast(context, "data updated successfully")
                                            submit.text = "Update Info"
                                            animation.cancelAnimation()
                                            animation.visibility = GONE
                                            editButton.setTextColor(
                                                ContextCompat.getColor(
                                                    context!!,
                                                    R.color.default_text
                                                )
                                            )
                                            submit.isEnabled = false
                                            busy = false
                                        }


                                    }
                                } else {
                                    onfailure()
                                }
                            }

                            override fun onfailure() {
                                activity!!.runOnUiThread {
                                    showToast(context, "failed")
                                    animation.visibility = GONE
                                    animation.cancelAnimation()
                                    submit.text = "Update info"
                                    busy = false
                                    //make network call here

                                }
                            }
                        }) { _, _, _ -> }

                } else {
                    showToast(context, "Please fill all fields")
                }
            }

        }
        return view
    }

    private fun getTrainerID(): String {
        val cl = CountDownLatch(1)
        var id = ""
        CoroutineScope(IO).launch {
            id = userDao!!.getUserID()
            cl.countDown()
        }
        cl.await()
        return id
    }


    private fun setProfileDataFromDB() {
        launch {
            context?.let {

                val nameStr = userDao!!.getadminName()
                val phoneStr = userDao!!.getPhone()
                val emailStr = userDao!!.getEmail()
                val centerStr = userDao!!.getCenterName()
                val dobStr = userDao!!.getDob()
                mLog.i(TAG, "$nameStr $phoneStr $emailStr $centerStr $dobStr")

                withContext(Main) {

                    nameET.setText(nameStr)
                    phoneET.setText(phoneStr)
                    emailET.setText(emailStr)
                    centerET.setText(centerStr)
                    dobET.setText(dobStr)
                    name = nameStr
                    phoneNumber = phoneStr
                    email = emailStr
                    center = centerStr
                    dob = dobStr

                }

            }
        }
    }

    private fun disableETs() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            dobET.isClickable = true
            arraylist.forEach {
                it.isEnabled = false
            }
            arraylistLines.forEach {
                it.imageTintList = colorStateListBlue
            }
        }

    }

    private fun enableETs() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            dobET.isClickable = true
            arraylist.forEach {
                it.isEnabled = true
            }
            arraylistLines.forEach {
                it.imageTintList = colorStateListGreen
            }
        }

    }

}
