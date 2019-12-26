package orionedutech.`in`.lmstrainerapp.fragments.profile


import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.experience_layout.view.*
import kotlinx.android.synthetic.main.fragment_additional_information.view.*
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
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.response
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList


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
    var pan =""
    var aadhar = ""
    var doj = ""
    var qualification = ""
    var editing = false
    var arraylistETs : ArrayList<TextInputEditText> = ArrayList()
    var arraylistLines : ArrayList<ImageView> = ArrayList()
    lateinit var submit:MaterialButton
    var colorStateListBlue: ColorStateList? = null
    var colorStateListGreen: ColorStateList? = null
    lateinit var animation: LottieAnimationView
    var db: MDatabase? = null
    var userDao: UserDao?= null
    var trainer_id =""
    var busy =false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_additional_information, container, false)

        experienceET = view.experience
        editButton = view.edit
        panET = view.pan
        aadharET = view.aadhar
        dojET = view.doj
        qualificationET = view.qualification
        submit = view.updateProfile
        animation = view.lottie

        disableETs()
        arraylistETs = arrayListOf(experienceET,panET,aadharET,qualificationET)
        arraylistLines = arrayListOf(view.line1,view.line2,view.line3,view.line4,view.line5)
        val color = ContextCompat.getColor(context!!, R.color.blue)
        val green = ContextCompat.getColor(context!!, R.color.green)

        colorStateListBlue = ColorStateList.valueOf(color)
        colorStateListGreen = ColorStateList.valueOf(green)
        db = MDatabase(context!!)
        userDao = db!!.getUserDao()
        trainer_id = getTrainerID()
        setDataFromDB()
        editButton.setOnClickListener {
            if (!editing) {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.green))
                submit.isEnabled = true
                editing = true
                enableETs()
                mToast.showToast(context!!, "edit enabled")
            } else {
                editButton.setTextColor(ContextCompat.getColor(context!!, R.color.default_text))
                editing = false
               submit.isEnabled = false
                disableETs()
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
                experienceET.setText(if(experience.toInt()==1) String.format("%s year",experience) else String.format("%s years",experience) )
                dialogue.dismiss()
            }
            dialogue.show()
        }

        dojET.setOnClickListener {

            mLog.i(mLog.TAG, "clicked")
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                activity!!,
                R.style.DateDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year1, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    mLog.i(mLog.TAG, "$dayOfMonth ${monthOfYear + 1}, $year1")
                    dojET.setText(String.format("%d-%d-%d", dayOfMonth, (monthOfYear + 1), year1))
                    doj = String.format("%d-%d-%d", dayOfMonth, (monthOfYear + 1), year1)
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



        panET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            pan = p0.toString()
            }

        })
        aadharET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            aadhar = p0.toString()
            }

        })
        dojET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            doj = p0.toString()
            }

        })

        qualificationET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            qualification = p0.toString()
            }

        })

        experienceET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                experience = p0.toString()
            }

        })

        submit.setOnClickListener {

            if(!busy){
                busy = true
                if(pan.isNotBlank()&&aadhar.isNotBlank()&&doj.isNotBlank()&&experience.isNotBlank()&&qualification.isNotBlank()) {
                    submit.text = ""
                    animation.visibility = View.VISIBLE
                    animation.playAnimation()
                    //make network call here
                    if(experience.contains("y")) {
                        experience = experience.substring(0, experience.indexOf("y")).trim()
                    }
                    val json = JSONObject()
                    json.put("user_id",trainer_id)
                    json.put("user_name","")
                    json.put("user_dob","")
                    json.put("user_email","")
                    json.put("user_ph","")
                    json.put("user_center","")
                    json.put("user_pan",pan)
                    json.put("user_aadhar",aadhar)
                    json.put("user_doj",doj)
                    json.put("user_qualification",qualification)
                    json.put("user_exp",experience)

                    NetworkOps.post(Urls.profileUpdate,json.toString(),context,object: response {
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
                                    mLog.i(TAG,"saving experience $experience")
                                    userDao!!.updateAddtionalInfo(pan,aadhar,doj,qualification,experience)
                                    setDataFromDB()
                                    disableETs()
                                    editing = false
                                    if(activity==null){
                                        return@launch
                                    }
                                    activity!!.runOnUiThread{
                                        mToast.showToast(context, "data updated successfully")
                                        submit.text = "Update Info"
                                        animation.cancelAnimation()
                                        animation.visibility = View.GONE
                                        editButton.setTextColor(ContextCompat.getColor(context!!, R.color.default_text))
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
                                mToast.showToast(context, "failed")
                                animation.visibility = View.GONE
                                animation.cancelAnimation()
                                submit.text = "Update info"
                                busy = false


                            }
                        }

                    }){ _, _, _ ->

                    }

                }else{
                    mToast.showToast(context, "Please fill all fields")
                }
            }


        }

        return view
    }

    private fun disableETs() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            dojET.isClickable =false
            experienceET.isClickable = false
            arraylistETs.forEach {
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
           dojET.isClickable = true
           experienceET.isClickable = true
           arraylistETs.forEach {
               it.isEnabled = true
           }
           arraylistLines.forEach {
               it.imageTintList = colorStateListGreen
           }
       }
    }

    private fun setDataFromDB() {
        launch {
            context?.let {
                val panStr = userDao!!.getPan()
                val aadharStr = userDao!!.getAadhar()
                val dojStr = userDao!!.getDoj()
                val qualificationStr = userDao!!.getLQualification()
                val experienceStr = userDao!!.getWorkExperience()
                withContext(Main) {
                    panET.setText(panStr)
                    aadharET.setText(aadharStr)
                    dojET.setText(dojStr)
                    qualificationET.setText(qualificationStr)
                    experienceET.setText(if(experienceStr.toInt()==1) String.format("%s year",experienceStr) else String.format("%s years",experienceStr) )
                    pan = panStr
                    aadhar = aadharStr
                    doj = dojStr
                    qualification = qualificationStr
                    experience = experienceStr
                }

            }
        }
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

}
