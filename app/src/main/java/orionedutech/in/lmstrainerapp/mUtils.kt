package orionedutech.`in`.lmstrainerapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan


@SuppressLint("InflateParams")
    fun Context.showToast(message: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.custom_toast, null, false)
        val text = view.findViewById<TextView>(R.id.textView)
        text.text = message
        val toast = Toast(this)
        toast.setGravity(Gravity.BOTTOM, 0, 50)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }
@Suppress("DEPRECATION")
fun Context.isConnected():Boolean {
    var result = false
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }
    return result
}


fun Context.noInternetSnackBar(view: View) {

        val snackbar = Snackbar.make(
            view,
            "Your internet is not working",
            Snackbar.LENGTH_LONG
        )
        // change snackbar text color
        val snackbarView = snackbar.view
        val snackbarTextId = R.id.snackbar_text
        val textView = snackbarView.findViewById<TextView>(snackbarTextId)
        textView.setTextColor(ContextCompat.getColor(view.context,R.color.white))
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction("Settings") { v ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.component = ComponentName(
                "com.android.settings",
                "com.android.settings.Settings\$DataUsageSummaryActivity"
            )
           this.startActivity(intent)
        }
        snackbar.show()
    }

fun getFileUri(context : Context,file : File) : Uri {
    return  FileProvider.getUriForFile(context, "orionedutech.in.lmstrainerapp.fileprovider", file)
}
 fun getOrientation(context: Context, photoUri: Uri): Int {
    var cursor = context.contentResolver.query(
        photoUri,
        arrayOf(MediaStore.Images.ImageColumns.ORIENTATION), null, null, null
    )

    if (cursor!!.count != 1) {
        cursor.close()
        return -1
    }

    cursor.moveToFirst()
    val orientation = cursor.getInt(0)
    cursor.close()
    cursor = null
    return orientation
}

fun versionCompare(str1:String, str2:String):Int {
    //here 1 means update 0 means no update
    val vals1 = str1.split(("\\.").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val vals2 = str2.split(("\\.").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var i = 0
    while (i < vals1.size && i < vals2.size && vals1[i] == vals2[i])
    {
        i++
    }
    if (i < vals1.size && i < vals2.size)
    {
        val diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]))
        return Integer.signum(diff)
    }
    return Integer.signum(vals1.size - vals2.size)
}



