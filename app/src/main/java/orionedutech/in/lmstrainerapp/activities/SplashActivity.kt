package orionedutech.`in`.lmstrainerapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.airbnb.lottie.RenderMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.update_alert.view.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.BuildConfig
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.versionCompare
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.io.IOException


class SplashActivity : BaseActivity() {
    var versionPref :SharedPreferences? = null
    lateinit var imageView : GifImageView
    lateinit var applogo : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the

                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        versionPref = getSharedPreferences("appversion", Context.MODE_PRIVATE)
        imageView = gif
        applogo = logo
        try {
            val gifFromAssets = GifDrawable(assets, "CompNew.gif")
            imageView.setImageDrawable(gifFromAssets)
            gifFromAssets.addAnimationListener { check() }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun check() {
        val sharedVersion = versionPref!!.getString("version","")
        if(sharedVersion!!.isNotEmpty()){
            if(versionCompare(BuildConfig.VERSION_NAME,sharedVersion)==1){
                //new app version so clear the preferences
                mLog.i(TAG,"clearing app preferences")
                versionPref!!.edit().clear().apply()
            }else{
                mLog.i(TAG,"same app preferences not deleted")
            }
        }

        if(!versionPref!!.getBoolean("update",false)){
            launch {
                applicationContext?.let {
                    startActivity(Intent(it,if(MDatabase(it).getUserDao().userDataExists()) MainActivity::class.java else LoginActivity::class.java ))
                    overridePendingTransition(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left
                    )
                    finish()
                }
            }
        }else{
            imageView.alpha = 0.2f
            applogo.alpha = 0.2f
            val dialogueView = LayoutInflater.from(this)
                .inflate(R.layout.update_alert, null, false)
            val builder = MaterialAlertDialogBuilder(this,R.style.mAlertDialogTheme1)
                .setCancelable(false)
                .setView(dialogueView)
            val dialogue = builder.create()

            val lottie = dialogueView.anim
            lottie.setRenderMode(RenderMode.SOFTWARE)
            lottie.playAnimation()
            dialogueView.update.setOnClickListener {
                dialogue.dismiss()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                intent.setPackage("com.android.vending")
                startActivity(intent)
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left)
                finish()
            }
            dialogue.show()
        }



    }
}
