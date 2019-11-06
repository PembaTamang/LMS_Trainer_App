package orionedutech.in.lmstrainerapp;

import android.util.Log;

public class mLog {
    public static final String TAG = "mTrainer";
    public static void i(String cls, String s) {
            if(BuildConfig.DEBUG) {
                Log.i(TAG, String.format("%s : %s", cls, s));
            }
    }

}
