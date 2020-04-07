package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        loadDataButton = findViewById(R.id.data_button);
        loadDataButton.setOnClickListener(this);

        result = findViewById(R.id.result_text);
        coordinates = findViewById(R.id.current_coordinates);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(LOG_TAG, "Callback started");
                if (locationResult == null) {
                    Log.d(LOG_TAG, "No Location found.");
                }
                assert locationResult != null;
                for (Location location : locationResult.getLocations()) {
                    Log.d(LOG_TAG, "Location update received.");
                    Latitude = location.getLatitude();
                    Longitude = location.getLongitude();
                }
            }
        };
    }

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
                        //Location is found, but it's null
                    } else {
                        Log.d(LOG_TAG, "Last known location found, but it's null");
                        startLocationUpdates();
                        requestLocationUpdates = true;
                    }
                    //The location was not found.
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

    @Override
    protected void onResume() {
        super.onResume();
        if(requestLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates(){
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
        if (permissionAccessCoarseLocationApproved) {
            Log.d(LOG_TAG, "Permission Granted.");
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper());
        } else {
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

    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.data_button:
                requestLastKnownLocation();
                Log.d(API_TAG, "Button clicked, request send");
                RequestQueue queue = Volley.newRequestQueue(this);
                final String uri = "https://api.ipgeolocation.io/astronomy?apiKey=23db69866d064920877a97972d57bbb3&lat=" +  Latitude + "&long=" + Longitude;
                Log.d(API_TAG, uri);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(API_TAG, "Succesful API Fetch.");
                                try {
                                    result.setText("Moon will rise at: " + response.getString("moonrise"));
                                    coordinates.setText("You're current coordinates" + Longitude + Latitude );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(API_TAG, uri);
                            }
                        }, error -> {
                            Log.d(API_TAG, "API request failed.");
                            Log.d(API_TAG, error.toString());
                            result.setText("That's no moon. (Invalid location)");
                            Log.d(API_TAG, uri);
                        });

                queue.add(jsonObjectRequest);
                break;
        }
    }
}
