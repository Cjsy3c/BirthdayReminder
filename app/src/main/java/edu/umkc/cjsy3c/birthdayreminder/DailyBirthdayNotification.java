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

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

        String temp;
        ArrayList<String> arr = birthdaysToday();
        if (arr == null)
            return;
        if( arr.size() == 1)
            temp = arr.get(0).substring(5).trim() + " has a Birthday Today";
        else
            temp = "There are " + arr.size() + " Events Today";

        // intent to start up app
        Intent i = new Intent(this,MainActivity.class);
        PendingIntent pIntent;
        pIntent = PendingIntent.getActivity(getApplicationContext(),0,i , PendingIntent.FLAG_UPDATE_CURRENT);


        ///////////////////////////////////////////////////////////////////
        Notification notify = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(temp)
                .setContentText("Click to open Birthday Reminder")
                .setContentIntent(pIntent)
                //.setOngoing(true)         // set non removable
                .build();

        mNotificationManager.notify(0, notify);

    }

    public ArrayList<String> birthdaysToday()
    {
        // null means none today

        ContactList temp = new ContactList(this);

        boolean show = getSharedPreferences("file", Context.MODE_PRIVATE).getBoolean("ShowAnn", false);
        temp.findBirthdays(1, show);
        if ("No Birthdays found in the next 1 days".equals(temp.getContacts().get(0)))
            return null;
        else
            return temp.getContacts();
    }
}