package com.example.shivam_pc.gamemode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    Switch AllNotificationSwitch;
    Switch CallOnlySwitch;
    Switch AutoBrightnessSwitch;
    Switch ClearBackgroundSwitch;
    SharedPreferences SavedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        AllNotificationSwitch = (Switch) findViewById(R.id.AllNotificationSwitch);
        CallOnlySwitch = (Switch) findViewById(R.id.ShowCallsOnlySwitch);
        AutoBrightnessSwitch = (Switch) findViewById(R.id.AutoBrightnessSwitch);
        ClearBackgroundSwitch = (Switch) findViewById(R.id.ClearBackground);

        //Setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });


        SavedSettings = this.getSharedPreferences("SavedSettings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor SharedPreferencesEditor = SavedSettings.edit();

        //Initilizing the Saved Values

        AutoBrightnessSwitch.setChecked(SavedSettings.getBoolean(getString(R.string.AutoBrightness), false));
        AllNotificationSwitch.setChecked(SavedSettings.getBoolean(getString(R.string.AllNotification), true));
        CallOnlySwitch.setChecked(SavedSettings.getBoolean(getString(R.string.CallsOnly), false));
        ClearBackgroundSwitch.setChecked(SavedSettings.getBoolean("ClearBackground", true));

        //AutoBrightness Onclick listen save value
        AutoBrightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("AutoBrightness", isChecked);
                SharedPreferencesEditor.apply();

            }
        });

        //All Notification Onclick listen save value
        AllNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("AllNotification", isChecked);
                SharedPreferencesEditor.apply();
            }
        });

        //Calls only Onclick listen save value
        CallOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("CallsOnly", isChecked);
                SharedPreferencesEditor.apply();
            }
        });

        //ClearBackground only Onclick listen save value
        ClearBackgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("ClearBackground", isChecked);
                SharedPreferencesEditor.apply();
            }
        });

    }


    //creating Menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    //Option Menu selections in toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_about:
                Intent about = new Intent(this,about_activity.class);
                startActivity(about);
                return true;

            case R.id.action_privacy:
                Intent privacy = new Intent(this,privacy_activity.class);
                startActivity(privacy);
                return true;

            case R.id.action_feedback:
                // Email feedback for my personal address
                String addresses[] = {"agrawalshivam66@gmail.com"};
                String subject = "Game Mode app feedback";
                Intent MailIntent = new Intent(Intent.ACTION_SENDTO);
                MailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
                MailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
                MailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                if (MailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(MailIntent);
                    Toast.makeText(this, "Please type your suggestions and attach screenshots if required", Toast.LENGTH_LONG).show();
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}




