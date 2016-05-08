package edu.umkc.cjsy3c.birthdayreminder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class About_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_);

    }

    public void sendFeedback(View v) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("mailto:Cody10101@gmail.com"));    // set recipient
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Birthday Reminder App Feedback"); // set subject

        startActivity(sendIntent);  // send intent to open email

    }

}
