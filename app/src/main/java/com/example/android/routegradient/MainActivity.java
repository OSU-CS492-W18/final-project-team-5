package com.example.android.routegradient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private EditText mRoute1EditText;
    private EditText mRoute2EditText;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoute1EditText = (EditText)findViewById(R.id.et_route1_entry);
        mRoute2EditText = (EditText)findViewById(R.id.et_route2_entry);
        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        Button routeButton = (Button)findViewById(R.id.btn_find_route);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startLocation = mRoute1EditText.getText().toString();
                String endLocation = mRoute2EditText.getText().toString();
                if (!TextUtils.isEmpty(startLocation) && !TextUtils.isEmpty(endLocation)) {
                    doRouteSearch(startLocation, endLocation);
                }
            }
        });
    }

    private void doRouteSearch(String routeStart, String routeEnd) {
        String default_travel_mode = "bicycling";
        String routeSearchURL = RouteUtils.buildRouteURL(routeStart, routeEnd, default_travel_mode);
        Log.d(TAG, "Using Route Utils with URL: " + routeSearchURL);
        new RouteSearchTask().execute(routeSearchURL);
    }

    public class RouteSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String routeSearchURL = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(routeSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (s != null) {
                Log.d(TAG, "Route Utils API result: " + s);
                String routeResult = RouteUtils.parseRouteJSON(s);
                Log.d(TAG, "Route Utils parsed result: " + routeResult);
                mLoadingErrorMessage.setVisibility(View.INVISIBLE);
                doElevationSearch(routeResult);
            } else {
                mLoadingErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doElevationSearch(String routeResult) {
        String elevationSearchURL = ElevationUtils.buildElevationURL(routeResult);
        Log.d(TAG, "Using Elevation Utils with URL: " + elevationSearchURL);
        new ElevationSearchTask().execute(elevationSearchURL);
    }

    public class ElevationSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String routeSearchURL = urls[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(routeSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (s != null) {
                Log.d(TAG, "Elevation Utils API result: " + s);
                ArrayList<Double> elevationResult = ElevationUtils.parseElevationJSON(s);
                Log.d(TAG, "Elevation Utils parsed result: " + elevationResult);
                ArrayList<Double> distanceBetweenSamples =  ElevationUtils.parseDistanceBetweenSamplesJSON(s);
                Log.d(TAG, "Elevation Utils parsed result for distance between samples: " + distanceBetweenSamples);
                ArrayList<Double> gradients = GradientUtils.parseAllGradients(elevationResult, distanceBetweenSamples);
                Log.d(TAG, "Elevation Utils parsed result for gradients: " + gradients);
                mLoadingErrorMessage.setVisibility(View.INVISIBLE);
                Double totalGradientChange = GradientUtils.parseTotalGradientChange(elevationResult, distanceBetweenSamples);
                Double totalElevationChange = GradientUtils.parseTotalElevationChange(elevationResult);
                Log.d(TAG, "Total Gradient Change: " + totalGradientChange + " | Total Elevation Change: " + totalElevationChange);
            } else {
                mLoadingErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
