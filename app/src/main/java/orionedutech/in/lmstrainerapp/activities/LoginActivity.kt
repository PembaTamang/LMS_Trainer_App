package orionedutech.`in`.lmstrainerapp.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast

import androidx.core.content.ContextCompat

import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.BuildConfig
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.network.NetworkOps
import orionedutech.`in`.lmstrainerapp.network.Urls
import orionedutech.`in`.lmstrainerapp.network.response
import orionedutech.`in`.lmstrainerapp.mLog.TAG;
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.dao.MDao
import orionedutech.`in`.lmstrainerapp.database.dao.MDatabase
import orionedutech.`in`.lmstrainerapp.database.entities.User
import orionedutech.`in`.lmstrainerapp.network.dataModels.UserModel
import orionedutech.`in`.lmstrainerapp.network.dataModels.Userdata
import orionedutech.`in`.lmstrainerapp.showToast


class LoginActivity : BaseActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: MaterialButton
    private var emailText: String = ""
    private var passwordText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.editText)
        password = findViewById(R.id.editText2)
        login = findViewById(R.id.login)
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
                loginUser(emailText, passwordText)
            } else {
                Toast.makeText(this, "please fill both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(emailText: String, passwordText: String) {
        val data = JSONObject()
        data.put("user_name", emailText)
        data.put("user_password", passwordText)
        progress.visibility = View.VISIBLE
        login.isEnabled = false
        login.text = ""
        email.isEnabled = false
        password.isEnabled = false
        NetworkOps.post(Urls.LOGINURL, data.toString(), this, email, object : response {
            override fun onrespose(string: String) {

                val gson = Gson()
                val userModel = gson.fromJson(string, UserModel::class.java)
                if (userModel.success == "1") {
                    mLog.i(TAG, "success")
                    val userData: Userdata = userModel.userdata
                    launch {
                        applicationContext?.let {
                            val dao: MDao = MDatabase(it).getDao()
                            val user = User(
                                userData.userid,
                                userData.userName,
                                userData.userRoleName,
                                userData.userAdminId,
                                userData.userFullname,
                                userData.useremail,
                                userData.userPhoneNo,
                                userData.userAdminType,
                                userData.batchId,
                                userData.centerId,
                                userData.batchName,
                                userData.centerName,
                                userData.userPassword
                            )
                            if (dao.userDataExists()) {
                                mLog.i(TAG, "user data exists")
                                if (user == dao.getuserDetails()) {
                                    mLog.i(TAG, "same user enter it")
                                    goToMainActivity(user.name)
                                } else {
                                    mLog.i(TAG, "different user")
                                    dao.insertUser(user)
                                    goToMainActivity(user.name)
                                }
                            } else {
                                mLog.i(TAG, "new user")
                                dao.insertUser(user)
                                goToMainActivity(user.name)
                            }

                        }


                    }
                } else {
                    showToast("error")
                    runOnUiThread {
                      loginFailed()
                    }
                }

            }


            override fun onfailure() {
                showToast("Error")
               loginFailed()
            }

            override fun internetFailure() {

            }
        }) { progress, speed, secs ->

            mLog.i(TAG, "progress :$progress")
            mLog.i(TAG, "progress :$speed")
            mLog.i(TAG, "progress :$secs")

        }
    }

    private fun loginFailed() {
        login.text = "Log In"
        progress.cancelAnimation()
        progress.visibility = View.GONE
        login.isEnabled = true
        email.isEnabled = true
        password.isEnabled = true
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
        }, 1000)

    }

}
