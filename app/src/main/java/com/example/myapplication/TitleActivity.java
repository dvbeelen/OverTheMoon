package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class TitleActivity extends AppCompatActivity
        implements View.OnClickListener {

    public Button startButton;
    public Button settings;
    public boolean language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        //Set button onClickListeners
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);
        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        //Handle the button-clicks. Change activity.
        switch (view.getId()) {
            case R.id.start:
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                break;
            case R.id.settings:
                Intent settingsScreen = new Intent(this, SettingsActivity.class);
                startActivity(settingsScreen);
                break;
        }
    }

}
