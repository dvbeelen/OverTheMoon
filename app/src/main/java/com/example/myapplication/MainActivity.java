package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    public Button loadDataButton;
    private TextView result;
    private TextView coordinates;
    private TextView moonrise;
    private TextView moonset;
    private TextView moonaltitude;
    private TextView moondistance;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean requestLocationUpdates = false;
    public int REQ_PERM_LOC_UPDATES = 1;
    final static String API_TAG = "API";
    final static String LOG_TAG = "Log";
    private double Latitude;
    private double Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get layout-items.
        loadDataButton = findViewById(R.id.data_button);
        loadDataButton.setOnClickListener(this);

        //Get data-fields
        result = findViewById(R.id.result_text);
        coordinates = findViewById(R.id.current_coordinates);
        moonrise = findViewById(R.id.data1);
        moonset = findViewById(R.id.data2);
        moonaltitude = findViewById(R.id.data3);
        moondistance = findViewById(R.id.data4);

        //Get fusedLocationClient to make GPS possible.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Update location once Last Known Location is checked.
        locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Toast.makeText(getApplicationContext(),"Fetched current location. it's null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        Toast.makeText(getApplicationContext(),"Fetched current location.",Toast.LENGTH_SHORT).show();
                    }
                }
        };
    }

    //Get the last known location from device.
    private void requestLastKnownLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location.
                    if (location != null) {
                        requestLocationUpdates = true;
                        startLocationUpdates();
                        Latitude = location.getLatitude();
                        Longitude = location.getLatitude();
                        Log.d(LOG_TAG, "Location found");
                        //If location is found, but it's null, still start the request for location updates
                    } else {
                        Log.d(LOG_TAG, "Last known location found, but it's null");
                        startLocationUpdates();
                        requestLocationUpdates = true;
                    }
                    //The location was not found. Still the application will try to get location updates.
                }).addOnFailureListener(this, (e) -> {
                requestLocationUpdates = true;
                Log.d(LOG_TAG, "Location not found");
                startLocationUpdates();
        });
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //When App resumes after pause or shutdown, get current location.
    @Override
    protected void onResume() {
        super.onResume();
        if(requestLocationUpdates) {
            startLocationUpdates();
        }
    }

    //Start looking for location updates
    private void startLocationUpdates(){
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
        //Check if permission is granted to access device GPS location. If it is, start the locationCallback.
        if (permissionAccessCoarseLocationApproved) {
            Log.d(LOG_TAG, "Permission Granted.");
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper());
        } else {
            //If permission is not given
            Log.d(LOG_TAG, "Permission Denied. Ask User for Permission.");
            requestLocationUpdates = false;
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERM_LOC_UPDATES);
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "Permission Requested.");
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Log.d(LOG_TAG, "Permission not granted. Try again.");
            }
        }
        Log.d(LOG_TAG, "Ask location again");
        requestLocationUpdates = true;
        startLocationUpdates();
    }

    //Function that handles button clicks on the main-page.
    public void onClick(View view)
    {
        switch (view.getId()) {
            //Check if the data_button is pressed.
            case R.id.data_button:
                requestLastKnownLocation();
                Log.d(API_TAG, "Button clicked, request send");
                RequestQueue queue = Volley.newRequestQueue(this);
                //Make a call to the api.ipgeolocation.io, sending the Latitude and Longitude values with it to get location-relevant data.
                final String uri = "https://api.ipgeolocation.io/astronomy?apiKey=" + getString(R.string.secret_key) +  "&lat=" +  Latitude + "&long=" + Longitude;
                Log.d(API_TAG, uri);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(API_TAG, "Succesful API Fetch.");
                                try {
                                    result.setText( "Todays Date " + response.getString("date"));
                                    coordinates.setText(getString(R.string.current_location));
                                    moonrise.setText(response.getString("moonrise"));
                                    moonset.setText(response.getString("moonset"));
                                    moonaltitude.setText(response.getString("moon_altitude"));
                                    moondistance.setText(response.getString("moon_distance"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(API_TAG, uri);
                            }
                        }, error -> {
                            Log.d(API_TAG, "API request failed.");
                            Log.d(API_TAG, error.toString());
                            result.setText(getString(R.string.error_text));
                            Log.d(API_TAG, uri);
                        });

                queue.add(jsonObjectRequest);
                break;
        }
    }
}
