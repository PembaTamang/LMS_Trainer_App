package orionedutech.`in`.lmstrainerapp.network.downloader

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MDownloader : Service(){
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action
        if(action.equals(MActions.start)){
            download()
        }else{
            stopDownload()
        }
        return START_STICKY
    }

    fun download(){

    }
    fun stopDownload(){

    }
}