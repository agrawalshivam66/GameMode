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
import android.util.Log;
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


        SavedSettings = this.getSharedPreferences("SavedSettings",Context.MODE_PRIVATE);
        final SharedPreferences.Editor SharedPreferencesEditor = SavedSettings.edit();

        //Initilizing the Saved Values

        AutoBrightnessSwitch.setChecked(SavedSettings.getBoolean(getString(R.string.AutoBrightness), false));
        AllNotificationSwitch.setChecked(SavedSettings.getBoolean(getString(R.string.AllNotification),true));
        CallOnlySwitch.setChecked(SavedSettings.getBoolean(getString(R.string.CallsOnly),false));
        ClearBackgroundSwitch.setChecked(SavedSettings.getBoolean("ClearBackground",true));

        //AutoBrightness Onclick listen save value
        AutoBrightnessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("AutoBrightness",isChecked);
                SharedPreferencesEditor.apply();

            }
        });

        //All Notification Onclick listen save value
        AllNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("AllNotification",isChecked);
                SharedPreferencesEditor.apply();
            }
        });

        //Calls only Onclick listen save value
        CallOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("CallsOnly",isChecked);
                SharedPreferencesEditor.apply();
            }
        });

        //ClearBackground only Onclick listen save value
        ClearBackgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesEditor.putBoolean("ClearBackground",isChecked);
                SharedPreferencesEditor.apply();
            }
        });

    }

    // Email feedback for my personal address
    public void feedback(View view) {
        String addresses[] = {"agrawalshivam66@gmail.com"};
        String subject = "Game Mode app feedback";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            Toast.makeText(this, "Please type your suggestions and attach screenshots if required", Toast.LENGTH_LONG).show();
        }
    }
}
