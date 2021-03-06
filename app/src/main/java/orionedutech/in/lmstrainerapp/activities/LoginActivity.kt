package orionedutech.`in`.lmstrainerapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.core.content.ContextCompat

import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.response
import orionedutech.`in`.lmstrainerapp.mLog.TAG;
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.*
import orionedutech.`in`.lmstrainerapp.database.dao.UserDao
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.User
import orionedutech.`in`.lmstrainerapp.network.dataModels.DCUserData


class LoginActivity : BaseActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: MaterialButton
    private var emailText: String = ""
    private var passwordText: String = ""
    lateinit var loginPrefs : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.editText)
        password = findViewById(R.id.editText2)
        login = findViewById(R.id.login)
        loginPrefs = getSharedPreferences("login", Context.MODE_PRIVATE)

        val ok: Drawable = ContextCompat.getDrawable(
            this,
            R.drawable.check
        )!!
        ok.setBounds(0, 0, ok.intrinsicWidth, ok.intrinsicHeight)

        val notOk = ContextCompat.getDrawable(
            this,
            R.drawable.check_grey
        )!!
        notOk.setBounds(0, 0, ok.intrinsicWidth, ok.intrinsicHeight)

        if (BuildConfig.DEBUG) {
            email.setText(getString(R.string.demo_user))
            password.setText(getString(R.string.demo_user_pass))
            emailText = (getString(R.string.demo_user))
            passwordText = (getString(R.string.demo_user_pass))
        }
        if(loginPrefs.getBoolean("logged_out",false)){
            val mail = loginPrefs.getString("username","")
            val pass = loginPrefs.getString("password","")
            email.setText(mail)
            password.setText(pass)
            emailText = mail!!
            passwordText = pass!!
            password.setCompoundDrawables(null, null, ok, null)
        }
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                emailText = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                passwordText = charSequence.toString()
                if (charSequence.length >= 6) {
                    password.setCompoundDrawables(null, null, ok, null)
                } else {
                    password.setCompoundDrawables(null, null, notOk, null)

                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        login.setOnClickListener {
            if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)) {
                //login
                if(passwordText.length>=6){

               loginUser(emailText, passwordText)
                }else{
                    showToast("password should be min 6 characters")
                }
            } else {
                showToast("Please fill both fields")
            }
        }
    }
    private fun loginUser(emailText: String, passwordText: String) {
        val data = JSONObject()
        data.put("user_name", emailText)
        data.put("user_password", passwordText)
        progress.visibility = View.VISIBLE
        login.isEnabled = false
        val layoutParams =
            login.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
        login.layoutParams = layoutParams

        login.text = ""
        email.isEnabled = false
        password.isEnabled = false
        NetworkOps.post(Urls.loginUrl, data.toString(), this, object : response {
            override fun onInternetfailure() {
            mToast.noInternetSnackBar(this@LoginActivity)
            }

            override fun onrespose(string: String) {
                if(string.isNullOrEmpty()){
                    runOnUiThread {
                        loginFailed()
                    }
                    return
                }
                val gson = Gson()
                val userModel = gson.fromJson(string, DCUserData::class.java)
                if(userModel ==null){
                    runOnUiThread {
                        loginFailed()
                    }
                    return
                }
                if (userModel.success == "1") {
                    mLog.i(TAG, "success")
                    val userData = userModel.userdata
                    val trainer = userModel.trainer_data
                    loginPrefs.edit().putBoolean("logged_out",false).apply()
                    launch {
                        applicationContext?.let {
                            val dao: UserDao = MDatabase(it)
                                .getUserDao()
                            val user = User(
                                userData.userid,
                                userData.user_name,
                                userData.user_role_name,
                                userData.user_admin_id,
                                userData.user_fullname,
                                userData.useremail,
                                userData.user_phone_no,
                                userData.user_admin_type,
                                userData.batch_id,
                                userData.center_id,
                                userData.batch_name,
                                userData.center_name,
                                userData.user_password,
                                trainer.user_pan,
                                trainer.user_aadhar,
                                trainer.user_doj,
                                trainer.user_dob,
                                trainer.user_last_qualification,
                                trainer.user_prof_qualification,
                                trainer.user_work_experience,
                                userData.userprofileid
                            )
                            if (dao.userDataExists()) {
                                mLog.i(TAG, "user data exists")
                                if (user == dao.getuserDetails()) {
                                    mLog.i(TAG, "same user enter it")
                                    goToMainActivity(user.name!!)


                                } else {
                                    mLog.i(TAG, "different user")
                                    dao.insertUser(user)
                                    goToMainActivity(user.name!!)
                                    loginPrefs.edit().putString("username",emailText).putString("password",passwordText).apply()
                                }
                            } else {
                                mLog.i(TAG, "new user")
                                dao.insertUser(user)
                                goToMainActivity(user.name!!)

                                loginPrefs.edit().putString("username",emailText).putString("password",passwordText).apply()
                            }

                        }


                    }

                } else {

                    loginFailed()
                }

            }
            override fun onfailure() {

                val snackbar = Snackbar.make(coordinator,"Login failed",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(ContextCompat.getColor(this@LoginActivity,R.color.orange))
                snackbar.show()
                val layoutParams =
                    login.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                login.layoutParams = layoutParams
                login.text = "Log In"
                progress.cancelAnimation()
                progress.visibility = View.GONE
                login.isEnabled = true
                email.isEnabled = true
                password.isEnabled = true
            }

        }) { progress, speed, secs ->

            mLog.i(TAG, "progress :$progress")
            mLog.i(TAG, "progress :$speed")
            mLog.i(TAG, "progress :$secs")

        }

    }

    fun loginFailed() {
        runOnUiThread {
            val snackbar = Snackbar.make(coordinator,"Wrong credentials",Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(ContextCompat.getColor(this,R.color.orange))
            snackbar.show()
            val layoutParams =
                login.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            login.layoutParams = layoutParams
            login.text = "Log In"
            progress.cancelAnimation()
            progress.visibility = View.GONE
            login.isEnabled = true
            email.isEnabled = true
            password.isEnabled = true
        }
    }

    private fun goToMainActivity(name: String) {
        progress.cancelAnimation()
        progress.visibility = View.GONE
        login.text = "welcome $name"
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(
                R.anim.enter_from_right, R.anim.exit_to_left
            )
            finish()
        }, 1500)

    }

}
