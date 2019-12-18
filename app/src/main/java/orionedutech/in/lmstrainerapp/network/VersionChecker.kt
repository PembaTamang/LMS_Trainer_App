package orionedutech.`in`.lmstrainerapp.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.BuildConfig
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.versionCompare
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class VersionChecker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    val cl: CountDownLatch = CountDownLatch(1)
    var result: Result = Result.retry()
    override fun doWork(): Result {
        val appContext = applicationContext
        val versionPref = appContext.getSharedPreferences("appversion", Context.MODE_PRIVATE)
        NetworkOps.get(Urls.appVersionUrl, appContext, object : response {
            override fun onInternetfailure() {
                cl.countDown()
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG, "response $string")
                if (string.isNullOrEmpty()) {
                    mLog.i(TAG, "error in version api")
                    cl.countDown()
                    return
                }
                val json = JSONObject(string)
                if (json.getString("success") == "1") {
                    val version = json.getString("response")
                    val appversion = BuildConfig.VERSION_NAME
                    val res = versionCompare(version, appversion)
                    if (res == 1) {
                        versionPref.edit().putBoolean("update", true).apply()
                    } else {
                        versionPref.edit().putBoolean("update", false).apply()
                    }
                    versionPref.edit().putString("version",BuildConfig.VERSION_NAME).apply()
                    result = Result.success()

                    mLog.i(TAG, "success")
                } else {
                    mLog.i(TAG, "error")
                }
                cl.countDown()
            }

            override fun onfailure() {
                cl.countDown()
            }

        })

        cl.await(15, TimeUnit.SECONDS)
        return result
    }

}