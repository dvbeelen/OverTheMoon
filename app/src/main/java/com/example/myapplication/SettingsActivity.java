package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static boolean getCountbackSetting (Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("check_box_count_backwards", false);
    }
}
