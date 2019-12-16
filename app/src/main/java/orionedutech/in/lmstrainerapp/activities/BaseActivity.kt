package orionedutech.`in`.lmstrainerapp.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(),CoroutineScope{
    private lateinit var job : Job

     override val coroutineContext: CoroutineContext
     get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
    job.cancel()
    }
}