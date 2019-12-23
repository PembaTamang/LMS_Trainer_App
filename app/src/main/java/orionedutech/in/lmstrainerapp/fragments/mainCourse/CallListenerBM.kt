package orionedutech.`in`.lmstrainerapp.fragments.mainCourse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager


class CallListenerBM : BroadcastReceiver() {
    var telephony: TelephonyManager? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
    val phoneListener = CallListener(p0!!)
        telephony = p0.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephony!!.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
}