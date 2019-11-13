package orionedutech.`in`.lmstrainerapp.network.downloader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CancelDownloadListener : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
       val intent = Intent(p0,MDownloaderService::class.java)
        intent.action = MActions.stop
        p0!!.startService(intent)
    }

}
