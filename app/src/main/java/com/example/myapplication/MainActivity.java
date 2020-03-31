package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{

    private Button loadDataButton;
    private TextView result;
    final static String API_TAG = "API";
    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDataButton = findViewById(R.id.data_button);
        loadDataButton.setOnClickListener(this);

        result = findViewById(R.id.result_text);

        API_KEY = getString(R.string.api_key);

    }

    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.data_button:
                Log.d(API_TAG, "Button clicked, request send");
                RequestQueue queue = Volley.newRequestQueue(this);
                final String uri = "https://api.ipgeolocation.io/astronomy?apiKey="+ API_KEY + "&lat=-52.189278&long=4.443176";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(API_TAG, "Succesful API Fetch.");
                                try {
                                    result.setText("Moon will rise at: " + response.getString("moonrise"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(API_TAG, uri);
                            }
                        }, new Response.ErrorListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(API_TAG, "API request failed.");
                                Log.d(API_TAG, error.toString());
                                result.setText("That's no moon.");
                                Log.d(API_TAG, uri);
                            }
                        });

                queue.add(jsonObjectRequest);
                break;
        }
    }
}
