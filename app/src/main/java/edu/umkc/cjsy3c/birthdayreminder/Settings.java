package edu.umkc.cjsy3c.birthdayreminder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {

    private Spinner spin;
    private String[] spinList = {"Show all","Today", "7 days", "30 days", "90 days"};
    ArrayAdapter<String> spinAdapter;
    private static Activity mAct;
    private static int i = 0;
    CheckBox cb, anniversary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set the checked box
        cb = (CheckBox) findViewById(R.id.checkBox);
        boolean checked = getSharedPreferences("file", Context.MODE_PRIVATE).getBoolean("Notify", true);
        cb.setChecked(checked);

        anniversary = (CheckBox) findViewById(R.id.anniversaryCheckBox);
        checked = getSharedPreferences("file", Context.MODE_PRIVATE).getBoolean("ShowAnn", false);
        anniversary.setChecked(checked);

        mAct = this;
        // create spinner
        spin = (Spinner)findViewById(R.id.spinner);
        spinAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,spinList);

        // set what happens on dropdown
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(spinAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // set how many days to display
                
                SharedPreferences pref = getSharedPreferences("file",Context.MODE_PRIVATE);
                //int i = 0;
                switch (position) {
                    case 0:     // show all
                        i = 0;
                        break;
                    case 1:     // 1 day
                        i = 1;
                        break;
                    case 2:     // 7 days
                        i = 7;
                        break;
                    case 3:     // 30 days
                        i = 30;
                        break;
                    case 4:     // 90 days
                        i = 90;
                        break;
                    default:    // none selected, show all
                        i = 0;
                        break;
                }
                pref.edit().putInt("disp", i).commit();


                //System.out.println("Selected item i = "+i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //System.out.println("onNothingSelected called");
            }

        });


    }

    @Override
    protected void onPause() {
        // save notification settings
        SharedPreferences pref = getSharedPreferences("file", Context.MODE_PRIVATE);
        pref.edit().putBoolean("Notify", cb.isChecked());
        pref.edit().putBoolean("ShowAnn", anniversary.isChecked()).commit();

        super.onPause();
    }


}
