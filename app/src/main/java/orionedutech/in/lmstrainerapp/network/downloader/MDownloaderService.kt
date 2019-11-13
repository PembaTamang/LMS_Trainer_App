@file:Suppress("DEPRECATION")

package orionedutech.`in`.lmstrainerapp.network.downloader

import android.app.*
import android.content.ContentResolver
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import java.util.*
import java.util.concurrent.TimeUnit
import orionedutech.`in`.lmstrainerapp.R
import java.io.*


class MDownloaderService : Service() {
    private var downloadView: RemoteViews? = null
    private val foreground_notificationID = 12312
    private var forgroundNotification: Notification? = null
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder : NotificationCompat.Builder
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action
        if (action.equals(MActions.start)) {
            val url = intent.getStringExtra("url")!!
            val fileName = intent.getStringExtra("name")!!
            forgroundNotification = getForegroundNotification(fileName)
            startForeground(foreground_notificationID, forgroundNotification)
            download(url, fileName)
        } else {
            stopService()
        }
        return START_STICKY
    }

    private fun getForegroundNotification(fileName: String): Notification? {
        val channelId = fileName + "ID"
        downloadView = RemoteViews(applicationContext.packageName, R.layout.notification_download)
        val cancelIntent = Intent(applicationContext, CancelDownloadListener::class.java)
        cancelIntent.action = MActions.stop
        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            123,
            cancelIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        downloadView!!.setOnClickPendingIntent(R.id.cancel, cancelPendingIntent)
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.notification)
        notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "My Background Service"
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            chan.setSound(  sound,
                AudioAttributes.Builder().setUsage(
                    AudioAttributes.USAGE_NOTIFICATION_RINGTONE
                ).build())

            notificationManager.createNotificationChannel(chan)
        }
        notificationBuilder = NotificationCompat.Builder(this, channelId)
        forgroundNotification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.notification)
            .setSound(sound)
            .setContentTitle("Downloading file $fileName")
            .setTicker("ticker")
            .addAction(R.drawable.stop,"cancel download",cancelPendingIntent)
            /*.setContent(downloadView)*/
            .setProgress(100,0,false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        forgroundNotification!!.flags = NotificationCompat.FLAG_ONLY_ALERT_ONCE
        return forgroundNotification
    }

    private fun download(url: String, fileName: String) {
        downloadView!!.setTextViewText(R.id.fileName, fileName)
        refreshNotification()
        MDownloader.downloadFile(applicationContext, url, fileName, fileName, object : progress {
            override fun progress(progress: Double, speed: Float, secs: Long) {

               /* downloadView!!.setProgressBar(R.id.progress, 100, progress.toInt(), false)
                downloadView!!.setTextViewText(
                    R.id.time_speed,
                    String.format("%s / %s ", getSecs(secs), getSpeed(speed))
                )*/
                notificationBuilder.setProgress(100,progress.toInt(),false)
                notificationBuilder.setSubText(String.format("%s / %s ", getSecs(secs), getSpeed(speed)))
                notificationBuilder.setTicker("ticker ")
                refreshNotification()
            }

            override fun failed() {
                mLog.i(TAG,"failed")
                MDownloader.showNotification(
                    applicationContext,
                    " $fileName could not be downloaded ",
                    "",
                    false
                )
                stopService()
            }

            override fun completed(totalBytes: Long) {
                mLog.i(TAG,"completed")

                val path = applicationContext.filesDir.path + "/" + fileName.trim()
               CoroutineScope(IO).launch {
                   copyToExternalStorage(path,totalBytes)
               }

            }

        })
    }

    private suspend fun copyToExternalStorage(path:String,totalBytes: Long) {
        val sourceFile = File(path)
        if(sourceFile.exists()){
            val destinationFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"+sourceFile.name)
                mLog.i(TAG,"destination path : ${destinationFile.absolutePath}")
                destinationFile.parentFile!!.mkdirs()
                destinationFile.createNewFile()
             //   file.copyTo(to,true)
                val inp = FileInputStream(sourceFile)
                val out = FileOutputStream(destinationFile)
            var percentage : Long
            notificationBuilder.setContentTitle("Please wait")
            notificationBuilder.setSubText("copying file")
            notificationBuilder.setTicker("ticker ")
            notificationBuilder.setProgress(100,0,true)
            refreshNotification()
            inp.copyTo(out){ current, _ ->
              /*  percentage = ((current.toFloat()/totalBytes.toFloat())*100).toLong()
                notificationBuilder.setProgress(100,percentage.toInt(),false)*/

            }
            MDownloader.showNotification(
                applicationContext,
                " ${destinationFile.name} has been downloaded ",
                "tap to view",
                true
            )
                mLog.i(TAG,"copy complete")

        }else{
            mLog.i(TAG,"file does not exist")
        }
        stopService()
    }
    fun InputStream.copyTo(out: OutputStream, onCopy: (Long, Int) -> Unit): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            onCopy(bytesCopied, bytes)
            bytes = read(buffer)
        }
        return bytesCopied
    }
    private fun stopService() {
        MDownloader.cancelAllDownloads()
        stopForeground(true)
        stopSelf()
    }

    private fun getSecs(secs: Long): String {
        val seconds = TimeUnit.SECONDS.convert(secs, TimeUnit.NANOSECONDS).toInt()
        if (seconds < 60) {
            val s: String
            if (seconds > 0) {

                s = "$seconds secs "

            } else if (seconds == 0) {

                s = "finishing up..."

            } else {
                s = "unknown"
            }
            return s

        } else {
            val min = seconds / 60
            val mins: String
            if (min == 1) {
                mins = "$min min "
            } else {
                mins = "$min mins "
            }
            return mins
        }

    }

    private fun getSpeed(speed: Float): String {
        return if (speed < 1024) {
            if (speed < 200) {
                downloadView!!.setViewVisibility(R.id.slow, VISIBLE)
                downloadView!!.setViewVisibility(R.id.exclaim, VISIBLE)
                refreshNotification()
                String.format(Locale.getDefault(), "%.2f kbps", speed)
            } else {
                downloadView!!.setViewVisibility(R.id.slow, INVISIBLE)
                downloadView!!.setViewVisibility(R.id.exclaim, INVISIBLE)
                refreshNotification()
                String.format(Locale.getDefault(), "%.2f kbps", speed)
            }
        } else {
            String.format(Locale.getDefault(), "%.2f Mbps", speed / 1024)
        }
    }

    private fun refreshNotification() {
        notificationManager.notify(foreground_notificationID, notificationBuilder.build())
    }
}