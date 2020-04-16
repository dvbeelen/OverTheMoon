package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TitleActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Get buttons on screen
    public Button startButton;
    public Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        //Set button onClickListeners
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);
        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this);

        //Greet the user with the Username given in Settings.
        Toast.makeText(getApplicationContext(), "Welcome " + SettingsActivity.getUsernameSetting(this), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        //If the GPS Permission setting is set to true, this function will check if the permission is already given.
        if (SettingsActivity.requestGPSPermission(this)){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission is already given.", Toast.LENGTH_SHORT).show();
                //If the permission is not already given, the app will ask permission again.
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onClick(View view) {
        //Handle the button-clicks. Change activity.
        switch (view.getId()) {
            //Go to the Main-screen
            case R.id.start:
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                break;
            //Go to the Settings-screen
            case R.id.settings:
                Intent settingsScreen = new Intent(this, SettingsActivity.class);
                startActivity(settingsScreen);
                break;
        }
    }
}
