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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_JSON = "com.example.android.routegradient.json";
    private final static String TAG = MainActivity.class.getSimpleName();
    private EditText mRoute1EditText;
    private EditText mRoute2EditText;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;
    private TextView mBadResultErrorMessage;
    private String startLocation;
    private String endLocation;
    public String latlngsJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRoute1EditText = (EditText)findViewById(R.id.et_route1_entry);
        mRoute2EditText = (EditText)findViewById(R.id.et_route2_entry);
        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);
        mBadResultErrorMessage = (TextView)findViewById(R.id.tv_bad_result_error);

        Button routeButton = (Button)findViewById(R.id.btn_find_route);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocation = mRoute1EditText.getText().toString();
                endLocation = mRoute2EditText.getText().toString();
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
            mLoadingErrorMessage.setVisibility(View.INVISIBLE);
            mBadResultErrorMessage.setVisibility(View.INVISIBLE);
            if (s != null) {
                if (RouteUtils.getStatusFromJSON(s).equals("OK")) {
                    latlngsJson = s;
                    ArrayList<Double> latlngs = ElevationUtils.parseLatLngFromJSONTest(s);
                    Log.d(TAG, "new lat/lngs parsed: " + latlngs);
                    String[] urls = ElevationUtils.buildElevationURLNew(latlngs);
                    new ElevationSearchTask().execute(urls);

                }
                else {
                    mBadResultErrorMessage.setVisibility(View.VISIBLE);
                }
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
            String searchResults = "{\"json\" : [";
            //System.out.println(urls.length + " urls");
            for(int i = 0; i<urls.length; i++){
                //String routeSearchURL = urls[0];
                try {
                    searchResults = searchResults + NetworkUtils.doHTTPGet(urls[i]);
                    if(i < urls.length-1){
                        searchResults = searchResults + ",";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            searchResults += "]}";
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            //System.out.println("Search results: " + s);
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (s != null) {
                mLoadingErrorMessage.setVisibility(View.INVISIBLE);
                viewResults(s, latlngsJson);
            } else {
                mLoadingErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    public void viewResults(String s, String latlngsParam){
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_JSON,s);
        System.out.println(s.length());
        System.out.println(latlngsParam.length());
        intent.putExtra("LAT_LNGS_JSON", latlngsParam);
        intent.putExtra("startingLocation",startLocation);
        intent.putExtra("endingLocation",endLocation);
        startActivity(intent);
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
