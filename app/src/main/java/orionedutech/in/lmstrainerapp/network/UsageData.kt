package orionedutech.`in`.lmstrainerapp.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UsageData(ctx: Context, params: WorkerParameters) : Worker(ctx,params) {
   val cl : CountDownLatch = CountDownLatch(1)

    var result : Result = Result.retry()
    override fun doWork(): Result {

        val appContext = applicationContext
        // ADD THIS LINE
        val data = inputData.getString("json")
        mLog.i(TAG,"inp data : $data")
        NetworkOps.post(Urls.usageData,data,appContext,object : response {
            override fun onInternetfailure() {
              cl.countDown()
            }

            override fun onrespose(string: String?) {
                mLog.i(TAG,"response : $string")
               if(string.isNullOrEmpty()){
                   mLog.i(TAG,"error in usage url")
                   cl.countDown()
                   return
               }
                val json = JSONObject(string)
                if( json.getString("success")=="1"){
                    result = Result.success()
                    mLog.i(TAG,"success")
                }else{
                  mLog.i(TAG,"error")
                }
                cl.countDown()
            }

            override fun onfailure() {
            cl.countDown()

            }
        }){_, _, _ ->  }

        cl.await(15,TimeUnit.SECONDS)
        return  result
    }
}