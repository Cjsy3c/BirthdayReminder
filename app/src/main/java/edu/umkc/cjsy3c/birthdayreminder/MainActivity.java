package edu.umkc.cjsy3c.birthdayreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private ArrayAdapter<String> la;
    private int timeFrame = 0;
    private ContactList contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("file", Context.MODE_PRIVATE);
        // pull limit from preferences
        timeFrame = pref.getInt("disp", 0);

        if (timeFrame > 1)
            setTitle("Birthdays - " + timeFrame + " days");
        else if (timeFrame == 1)
            setTitle("Birthdays Today");
        else
            setTitle(R.string.app_name);

        // display the list and start the notification alarm
        loadList();
        if (pref.getBoolean("Notify", true))
            scheduleAlarm();
        else
            cancelAlarm();

    }

    public void loadList(){
        contacts = new ContactList(getApplicationContext());
        boolean show = getSharedPreferences("file", Context.MODE_PRIVATE).getBoolean("ShowAnn", false);
        contacts.findBirthdays(timeFrame, show);
        lv = (ListView) findViewById(R.id.listView);


        la = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, contacts.getContacts());
        lv.setAdapter(la);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // menu item selected
        if (item.getItemId() == R.id.about) {
            // open about activity
            startActivity(new Intent(this, About_Activity.class));
        }

        else if (item.getItemId() == R.id.settings) {
            // open settings activity
            startActivity(new Intent(this, Settings.class));
        }

        return super.onOptionsItemSelected(item);
    }

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
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);

        // trigger alarm
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pIntent);

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

}
