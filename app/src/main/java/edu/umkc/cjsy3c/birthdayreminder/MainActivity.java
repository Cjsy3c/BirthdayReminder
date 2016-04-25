package edu.umkc.cjsy3c.birthdayreminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayAdapter<String> la;
    private int timeFrame = 0;
    private boolean notify;
    private ContactList contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("file", Context.MODE_PRIVATE);
        // pull limit from preferences
        timeFrame = pref.getInt("disp", 0);
        notify = pref.getBoolean("Notify", true);
        pref = null;    // close preferences

        if (timeFrame > 1)
            setTitle("Birthdays - " + timeFrame + " days");
        else if (timeFrame == 1)
            setTitle("Birthdays Today");
        else
            setTitle(R.string.app_name);

        // display the list and start the notification alarm
        loadList();


        if (notify)
            scheduleAlarm();
        else
            cancelAlarm();

    }

    /**
     * Load the list to the list view on the screen
     */
    public void loadList() {
        contacts = new ContactList(getApplicationContext());

        boolean show = getSharedPreferences("file", Context.MODE_PRIVATE).getBoolean("ShowAnn", false);
        contacts.findBirthdays(timeFrame, show);


        lv = (ListView) findViewById(R.id.listView);

        // create arrayadapter for list view
        la = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts.getContacts());
        contacts = null;
        lv.setAdapter(la);
        lv.setLongClickable(true);

        // set on click
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                AlertDialog.Builder message = new AlertDialog.Builder(MainActivity.this);
                message.setMessage("Would you like to send a text message?");

                // undo button should do nothing
                message.setNegativeButton("Cancel", null);

                message.setPositiveButton("Send Message",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // button click

                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                sendIntent.setData(Uri.parse("sms:"));

                                sendIntent.putExtra("sms_body", "Happy Birthday");
                                startActivity(sendIntent);
                            }
                        });

                message.setCancelable(true);
                message.create().show();
                return true;

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // menu item selected
        if (item.getItemId() == R.id.about) {
            // open about activity
            startActivity(new Intent(this, About_Activity.class));
        } else if (item.getItemId() == R.id.settings) {
            // open settings activity
            startActivity(new Intent(this, Settings.class));
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the alarm intent to trigger at the start of each day
     */
    public void scheduleAlarm() {

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // pending intent is what the alarm triggers
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // create an alarm
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // set tomorrow at midnight
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);

        // trigger alarm
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);

    }

    /**
     * cancel the alarm set
     */
    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

}
