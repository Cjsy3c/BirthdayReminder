package edu.umkc.cjsy3c.birthdayreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

/**
 * Created by Cody on 3/17/2016.
 */
public class DailyBirthdayNotification extends IntentService {


    public DailyBirthdayNotification() {
        super("DailyBirthdayNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // set up notification service, and cancel previous notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

        String temp;
        ArrayList<String> arr = birthdaysToday();
        // check if there are events to display
        if (arr == null)
            return;
        if( arr.size() == 1)
            temp = arr.get(0).substring(5).trim() + " has a Birthday Today";    // show "[name] has a Birthday Today"
        else
            temp = "There are " + arr.size() + " Events Today";     // Show a count of events today

        // intent to start up app
        Intent i = new Intent(this,MainActivity.class);
        PendingIntent pIntent;
        pIntent = PendingIntent.getActivity(getApplicationContext(),0,i , PendingIntent.FLAG_UPDATE_CURRENT);


        /////////////////////////////////////////////////////////////////// set notification
        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(temp)
                .setContentText("Click to open Birthday Reminder")
                .setContentIntent(pIntent)
                //.setOngoing(true)         // set non removable
                .build();

        mNotificationManager.notify(0, notify); // show notification

    }

    /**
     * Use ContactList to find all birthdays that are today.
     *
     * @return List of Birthdays today
     */
    public ArrayList<String> birthdaysToday()
    {
        // null means none today

        ContactList temp = new ContactList(this);
        // pull settings for anniversaries
        boolean show = getSharedPreferences(Settings.filePref, Context.MODE_PRIVATE).getBoolean(Settings.anniversaryPref, false);
        temp.findBirthdays(1, show);

        // check if array is actually empty.
        String cmp = temp.getContacts().get(0);

        // format is either "01-01 name"
        // or "No Birthdays Found"
        // so if the first character is not a digit then there are none
        if (!Character.isDigit(cmp.charAt(0)))
            return null;
        else
            return temp.getContacts();
    }
}
