package orionedutech.`in`.lmstrainerapp.fragments.mainCourse

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import orionedutech.`in`.lmstrainerapp.mLog

class CallListener(val context: Context) : PhoneStateListener() {

    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        super.onCallStateChanged(state, phoneNumber)
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            mLog.i(mLog.TAG,"phone Number $phoneNumber")

            }
    }
}