package orionedutech.in.lmstrainerapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class mToast {
public static void showToast(Context context,String message){
    View view = LayoutInflater.from(context).inflate(R.layout.custom_toast, null, false);
    TextView text = view.findViewById(R.id.textView);
            text.setText(message);
    Toast toast = new Toast(context);
    toast.setGravity(Gravity.BOTTOM, 0, 250);
    toast.setDuration(Toast.LENGTH_SHORT);
    toast.setView(view);
    toast.show();
}

    public static View inflateView(Context context,int layout){
        return LayoutInflater.from(context).inflate(layout, null, false);
    }
    public static void noInternetSnackBar(Activity activity) {
        if (activity != null) {
            Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "Your internet is not working", Snackbar.LENGTH_LONG);
            // change snackbar text color
            View snackbarView = snackbar.getView();
            int snackbarTextId = R.id.snackbar_text;
            TextView textView = snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(activity.getResources().getColor(R.color.white));
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.setAction("Settings", v -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                activity.startActivity(intent);
            });
            snackbar.show();
        }
    }
}
