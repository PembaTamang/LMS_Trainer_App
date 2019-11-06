package orionedutech.`in`.lmstrainerapp.activities

import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.database.dao.MDatabase
import orionedutech.`in`.lmstrainerapp.mLog


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        launch {
            applicationContext?.let {
                val hasdata = MDatabase(it).getDao().userDataExists()
                mLog.i("mTrainer","has user $hasdata")
                startActivity(Intent(it,if(hasdata) MainActivity::class.java else LoginActivity::class.java ))
                overridePendingTransition(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                finish()
            }
        }

    }
}
