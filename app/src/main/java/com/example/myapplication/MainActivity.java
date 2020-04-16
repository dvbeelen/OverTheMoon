package com.example.myapplication;

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

    //Screen-related Variables
    public Button loadDataButton;
    private TextView result;
    private TextView moonrise;
    private TextView moonset;
    private TextView moonaltitude;
    private TextView moondistance;

    //GPS-related Variables
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean requestLocationUpdates = false;

    private boolean GPSFailed = true;
    private double Latitude;
    private double Longitude;
    private String defaultLatitude;
    private String defaultLongitude;
    private String uriLat;
    private String uriLon;

    //Log-tags (defined as constant variables)
    private final static String API_TAG = "API";
    private final static String LOG_TAG = "Log";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get layout-items.
        loadDataButton = findViewById(R.id.data_button);
        loadDataButton.setOnClickListener(this);

        //Get data-fields
        result = findViewById(R.id.result_text);
        moonrise = findViewById(R.id.data1);
        moonset = findViewById(R.id.data2);
        moonaltitude = findViewById(R.id.data3);
        moondistance = findViewById(R.id.data4);

        //Get user-defined standard coordinates
        defaultLatitude = SettingsActivity.getDefLat(this);
        defaultLongitude = SettingsActivity.getDefLon(this);
        GPSFailed = true;

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
                    //If location is updated, also fetch the API again.
                    for (Location location : locationResult.getLocations()) {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                        fetchApi();
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
                        GPSFailed = false;
                        Log.d(LOG_TAG, "Location found");
                        Log.d(API_TAG, Boolean.toString(GPSFailed));
                        //If location is found, but it's null, still start the request for location updates
                    } else {
                        Log.d(LOG_TAG, "Last known location found, but it's null");
                        startLocationUpdates();
                        Log.d(API_TAG, Boolean.toString(GPSFailed));
                        requestLocationUpdates = true;
                    }
                    //The location was not found. Still the application will try to get location updates.
                }).addOnFailureListener(this, (e) -> {
                requestLocationUpdates = true;
                Log.d(LOG_TAG, "Location not found");
                Log.d(API_TAG, Boolean.toString(GPSFailed));
                startLocationUpdates();
        });
    }

    //Formulate a request for the users location
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(25000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //When App resumes after pause or shutdown, get current location.
    @Override
    protected void onResume() {
        super.onResume();
        if(requestLocationUpdates) {
            startLocationUpdates();
            Log.d(API_TAG, Boolean.toString(GPSFailed));
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
            Toast.makeText(getApplicationContext(),"GPS coordinates found.", Toast.LENGTH_SHORT).show();
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    Looper.getMainLooper());
        } else {
            //If permission is not given, use the user-specified default Lat. and Lon. coordinates.
            Log.d(LOG_TAG, "Permission Denied.");
            Toast.makeText(getApplicationContext(),"No GPS, switching to given default coordinates", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, defaultLatitude + defaultLongitude);
            if (SettingsActivity.requestGPSPermission(this)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
    }

    //Function that handles button clicks on the main-page.
    public void onClick(View view)
    {
        switch (view.getId()) {
            //Check if the data_button is pressed.
            case R.id.data_button:
                requestLastKnownLocation();
                Log.d(API_TAG, "Button clicked, request send");
                startLocationUpdates();
                fetchApi();
                break;
        }
    }

    //Make a call to the api.ipgeolocation.io, sending the Latitude and Longitude values with it to get location-relevant data.
    public void fetchApi () {
        RequestQueue queue = Volley.newRequestQueue(this);

        //If the app failed to get GPS-data, the custom Latitude and Longitude values from the Settings-screen will be used to get data.
        if (GPSFailed){
             uriLat = defaultLatitude;
             uriLon = defaultLongitude;
             Log.d(API_TAG, "Used custom coordinats");

             //If app managed to get GPS-data, use that data to make the API-reguest.
        } if (!GPSFailed) {
            uriLat = Double.toString(Latitude);
            uriLon = Double.toString(Longitude);
            Log.d(API_TAG, "Used GPS coordinats");
        }

        final String uri = "https://api.ipgeolocation.io/astronomy?apiKey=" + getString(R.string.secret_key) +  "&lat=" +  uriLat + "&long=" + uriLon;
        Log.d(API_TAG, uri);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //If API returns data succesfully, set it on the page.
                        Log.d(API_TAG, "Succesfull API Fetch.");
                        try {
                            result.setText(R.string.data_found);
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
                });

        queue.add(jsonObjectRequest);
    }
}
