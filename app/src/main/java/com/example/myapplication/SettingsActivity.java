package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

    //I've used constants to refer to my setting-names.
    public static final String usernameSetting = "username";
    public static final String customLat = "custom_lat";
    public static final String customLon = "custom_lon";
    public static final String gps_permission = "gps_permission";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    //Get the user-set latitude coordinate, if none is given, the standard (0.0) will be used.
    public static String getDefLat(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(customLat, c.getResources().getString(R.string.standard_lat));
    }

    //Get the user-set longitude coordinate, if none is given, the standard (0.0) will be used.
    public static String getDefLon(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(customLon, c.getResources().getString(R.string.standard_lon));
    }

    //Get the username settings, if none is given, the standard (User) will be used.
    public static String getUsernameSetting(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString(usernameSetting, c.getResources().getString(R.string.standard_username));
    }

    //Get the Request GPS Permission boolean. The standard value is false.
    public static boolean requestGPSPermission (Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(gps_permission, false);
    }
}