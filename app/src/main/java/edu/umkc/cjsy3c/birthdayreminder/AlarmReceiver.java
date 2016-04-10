package edu.umkc.cjsy3c.birthdayreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Cody on 3/17/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 37829;
    // so I can check if it is running

    @Override
    public void onReceive(Context context, Intent intent) {
        // alarm calls this method
        context.startService(new Intent(context, DailyBirthdayNotification.class));
        // send intent to create notification
    }
}
