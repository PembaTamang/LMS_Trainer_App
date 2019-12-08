package orionedutech.`in`.lmstrainerapp.fragments


import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_password_reset.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCPassword
import orionedutech.`in`.lmstrainerapp.network.progress
import orionedutech.`in`.lmstrainerapp.network.response

/**
 * A simple [Fragment] subclass.
 */
class PasswordResetFragment : BaseFragment() {
    var oldpassword: String = ""
    var newPassword: String = ""
    var newPassword1: String = ""

    lateinit var oldTil: TextInputLayout
    lateinit var newTil: TextInputLayout
    lateinit var newTil1: TextInputLayout
    lateinit var animation : LottieAnimationView
    lateinit var button : MaterialButton
    var busy = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_password_reset, container, false)
        oldTil = view.oldInp
        newTil = view.newInp
        newTil1 = view.newInp1
        animation = view.progress
        button = view.updatePassword
        view.oldPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                oldpassword = p0.toString()
            }

        })

        view.new_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                newPassword = p0.toString()
                if (newPassword.length < 6) {
                    newTil.error = "min 6 characters"
                } else {
                    newTil.error = ""
                    newTil1.error = ""
                }
            }

        })

        view.new_password1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                newPassword1 = p0.toString()
                if (newPassword1.length < 6) {
                    newTil1.error = "min 6 characters"
                } else {
                    newTil1.error = ""
                    newTil.error = ""
                }
            }

        })








        button.setOnClickListener {

           if(!busy){
               updatePassword()
           }else{
               mToast.showToast(context,"please wait")
           }
        }
        return view
    }
    private fun updatePassword(){
        launch {
            context?.let {
                busy = true
                val dao = MDatabase(it).getUserDao()
                val userID = dao.getUserID()
                if(oldpassword.isBlank()||newPassword.isBlank()||newPassword1.isBlank()){
                    mToast.showToast(context,"please fill all fields")
                  return@launch
                }

                if (newPassword != newPassword1) {
                    newTil.error = "passwords do not match"
                    newTil1.error = "passwords do not match"
                    return@launch
                }

                val json = JSONObject()
                json.put("user_id", userID)
                json.put("user_old_password", oldpassword)
                json.put("user_new_password", newPassword)

                oldTil.isEnabled = false
                newTil.isEnabled = false
                newTil1.isEnabled = false
                button.text = ""
                animation.visibility = View.VISIBLE
                animation.playAnimation()
                NetworkOps.post(Urls.passwordUpdateUrl, json.toString(), context, object :
                    response {
                    override fun onrespose(string: String?) {
                        mLog.i(TAG,"resp $string")
                        val password = Gson().fromJson(string, DCPassword::class.java)
                        if (password != null) {
                            if (password.success == "1") {
                                if(activity==null){
                                    return
                                }
                                activity!!.runOnUiThread {
                                    animation.visibility = View.GONE
                                    animation.cancelAnimation()
                                    button.text = password.updated
                                    Handler().postDelayed({
                                        mToast.showToast(context,"password updated successfully")
                                        activity!!.onBackPressed()
                                    },1000)
                                }

                            } else {
                                activity!!.runOnUiThread{
                                    mToast.showToast(context, password.error)
                                }

                                runFailureCode()
                            }
                        } else {
                            runFailureCode()
                        }
                        busy = false
                    }

                    override fun onfailure() {
                        mToast.showToast(context, "failed")
                        runFailureCode()
                    }

                    override fun onInternetfailure() {
                        runFailureCode()
                    }

                }) { _, _, _ ->
                }

            }
        }
    }
    private fun runFailureCode() {
        if(activity==null){
            return
        }
        activity!!.runOnUiThread {
            busy = false
            oldTil.isEnabled = true
            newTil.isEnabled = true
            newTil1.isEnabled = true
            animation.visibility = View.GONE
            animation.cancelAnimation()
            button.text = "UPDATE"
        }
    }


}
