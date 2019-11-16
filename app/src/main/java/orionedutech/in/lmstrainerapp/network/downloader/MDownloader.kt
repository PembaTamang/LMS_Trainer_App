package orionedutech.`in`.lmstrainerapp.network.downloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import io.github.lizhangqu.coreprogress.ProgressHelper
import io.github.lizhangqu.coreprogress.ProgressUIListener
import okhttp3.*
import okio.buffer
import okio.sink
import orionedutech.`in`.lmstrainerapp.R
import orionedutech.`in`.lmstrainerapp.activities.MainActivity
import orionedutech.`in`.lmstrainerapp.mLog
import orionedutech.`in`.lmstrainerapp.mLog.TAG
import orionedutech.`in`.lmstrainerapp.mToast
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.math.roundToInt

object MDownloader {
    var isFileDownloading = false
    private var filedownloadClient: OkHttpClient? = null

    fun downloadFile(
        context: Context,
        url: String,
        fileName: String,
        tag: String,
        progress: progress
    ) {
        filedownloadClient = OkHttpClient()
        isFileDownloading = true

        val builder = Request.Builder()
        builder.url(url)
        builder.get()
        builder.tag(tag)

        val fileDownloadCall = filedownloadClient!!.newCall(builder.build())
        fileDownloadCall.enqueue(object : Callback {


            override fun onFailure(call: Call, e: IOException) {
                progress.failed()
                isFileDownloading = false
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    try {


                    mLog.i(TAG, "request headers:" + response.request.headers)
                    mLog.i(TAG, "response headers:" + response.headers)
                    val body = response.body
                    val startTime = System.nanoTime()
                    assert(body != null)
                    val responseBody =
                        ProgressHelper.withProgress(body, object : ProgressUIListener() {

                            override fun onUIProgressChanged(
                                numBytes: Long,
                                totalBytes: Long,
                                percent: Float,
                                speed: Float
                            ) {

                                val elapsedTime = System.nanoTime() - startTime
                                val allTimeForDownloading = elapsedTime * totalBytes / numBytes
                                val remainingTime = allTimeForDownloading - elapsedTime
                                val per =
                                    (numBytes.toDouble() / totalBytes * 100).roundToInt().toDouble()
                                mLog.i(TAG, "$numBytes of $totalBytes & download percentage  $per")
                                progress.progress(per, speed, remainingTime)
                                if (totalBytes <= numBytes) {
                                    mLog.i(TAG,"progress from byte comparison")
                                    progress.completed(totalBytes)
                                }

                            }
                        })


                    val source = responseBody.source()
                    val path = context.filesDir.path + "/" + fileName.trim()
                    val outFile = File(path)
                    mLog.i(TAG, "onResponse: out file path" + outFile.absolutePath)
                    outFile.parentFile!!.mkdirs()
                    outFile.createNewFile()
                    val sink = outFile.sink().buffer()
                    source.readAll(sink)
                    sink.flush()
                    source.close()

                    }catch (ste:SocketTimeoutException){
                        mLog.i(TAG,"socket timeout exception")
                        Handler(Looper.getMainLooper()).post {
                            mToast.showToast(context,"SLOW INTERNET : server timed out")
                        }
                        progress.failed()
                    }


                } else {
                    progress.failed()
                    isFileDownloading = false
                }
            }

        })
    }

    fun cancelAllDownloads() {

        mLog.i(TAG, "cancelAllDownloads: stopping all downloads")
        if (filedownloadClient != null) {
            filedownloadClient!!.dispatcher.cancelAll()
            isFileDownloading = false
        }
        run { mLog.i(TAG, "client is null in cancel all ") }
    }


    fun cancelCallWithTag(tag: String) {
        if (filedownloadClient != null) {
            for (call in filedownloadClient!!.dispatcher.queuedCalls()) {
                if (call.request().tag() == tag)
                    call.cancel()
            }
            for (call in filedownloadClient!!.dispatcher.runningCalls()) {
                if (call.request().tag() == tag)
                    call.cancel()
            }
        }
        run { mLog.i(TAG, "client is null") }

    }

    fun  showNotification(context: Context, title: String, body: String, success: Boolean) {

        val id = 112312312
        val channelId = "my_notification_channel"
        val notiDesc = "Channel description"
        val fcmNotificationView: RemoteViews? = when (success) {
            true -> RemoteViews(context.packageName, R.layout.notification_custom_success)
            false -> RemoteViews(context.packageName, R.layout.notification_custom_failure)

        }
        val notificationManager = NotificationManagerCompat.from(context)
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification)


        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("notification", "notification")
        val pendingIntent =
            PendingIntent.getActivity(context, 123321321, intent, PendingIntent.FLAG_ONE_SHOT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
            .setSound(sound)
            .setSmallIcon(R.drawable.notification)
            .setCustomBigContentView(fcmNotificationView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCustomHeadsUpContentView(fcmNotificationView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Configure the notification channel.
            builder.priority = IMPORTANCE_HIGH
            notificationChannel.description = notiDesc
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationChannel.setSound(
                sound,
                AudioAttributes.Builder().setUsage(
                    AudioAttributes.USAGE_NOTIFICATION_RINGTONE
                ).build()
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        fcmNotificationView!!.setTextViewText(R.id.text, title)
        fcmNotificationView.setTextViewText(R.id.subtitle, body)
        val notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(id, notification)

    }

}