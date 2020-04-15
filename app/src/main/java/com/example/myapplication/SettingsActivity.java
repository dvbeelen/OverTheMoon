package com.example.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Locale;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public static boolean getLanguageChoice(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("change_language", true);
    }


    private void setLocale(String lang, Context c) {
       getSharedPreferences("change_language", Context.MODE_PRIVATE);
    }
}