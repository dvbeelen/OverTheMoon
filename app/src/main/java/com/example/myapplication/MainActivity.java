package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    //Set variables
    public Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set button onClickListeners
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.settings:
                Intent settingsScreen = new Intent(this, SettingsActivity.class);
                startActivity(settingsScreen);
                break;
        }
    }

}
