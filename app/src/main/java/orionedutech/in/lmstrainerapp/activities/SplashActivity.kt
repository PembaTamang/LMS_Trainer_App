package orionedutech.`in`.lmstrainerapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.MDatabase
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.IOException


class SplashActivity : BaseActivity() {

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

        val imageView = gif
        try {
            val gifFromAssets = GifDrawable(assets, "CompNew.gif")
            imageView.setImageDrawable(gifFromAssets)
            gifFromAssets.addAnimationListener { check() }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun check() {
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
    }
}
