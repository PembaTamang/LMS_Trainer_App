package orionedutech.`in`.lmstrainerapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar



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


