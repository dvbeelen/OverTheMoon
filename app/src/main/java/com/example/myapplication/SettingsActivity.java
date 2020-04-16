package com.example.myapplication;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class SettingsActivity extends PreferenceActivity {

    //I've used constants to refer to my setting-names.
    public static final String usernameSetting = "username";
    public static final String defLocationSetting = "def_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }

    //Get the username settings, if none is given, the standard (User) will be used.
    public static String getDefLocationSetting(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(defLocationSetting, Integer.toString(R.string.standard_username));
    }

    //Get the default location setting, if none is given, the standard (Amsterdam) will be used.
    public static String getUsernameSetting(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(usernameSetting, Integer.toString(R.string.standard_location));
    }


}