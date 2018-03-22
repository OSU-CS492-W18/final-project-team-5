package com.example.android.routegradient;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Collections;

public class ResultActivity extends AppCompatActivity{

    LineGraphSeries<DataPoint> series;
    private TextView mMaxGradientTV;
    private TextView mTotalGradientChangeTV;
    //private TextView mTotalElevationChangeTV;
    private TextView mResultsTitleTV;
    private String startLocation;
    private String endLocation;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String json = intent.getStringExtra(MainActivity.EXTRA_JSON);
        String latlngsJson = intent.getStringExtra("LAT_LNGS_JSON");
        startLocation = intent.getStringExtra("startingLocation");
        endLocation = intent.getStringExtra("endingLocation");

        mMaxGradientTV = (TextView)findViewById(R.id.tv_results_max_gradient);
        mTotalGradientChangeTV = (TextView)findViewById(R.id.tv_total_gradient_change);
        //mTotalElevationChangeTV = (TextView)findViewById(R.id.tv_total_elevation_change);
        mResultsTitleTV = (TextView)findViewById(R.id.tv_results_title);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String prefUnits = mSharedPreferences.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_default));
        String prefData = mSharedPreferences.getString(getString(R.string.pref_data_key),getString(R.string.pref_data_default));

        ArrayList<Double> elevationResult = ElevationUtils.parseElevationJSONNew(json);
        ArrayList<Double> latLngFromJson = ElevationUtils.parseLatLngFromJSONTest(latlngsJson);

        //ArrayList<Double> elevationResult = ElevationUtils.parseElevationJSON(json);
        //ArrayList<Double> latLngFromJson =  ElevationUtils.parseLatLngFromJSON(json);
        ArrayList<Double> parseResolution = GradientUtils.parceDistanceBetweenLocations(latLngFromJson);
        ArrayList<Double> gradients = GradientUtils.parseAllGradients(elevationResult, latLngFromJson);
        Double totalGradientChange = GradientUtils.parseTotalGradientChange(elevationResult, latLngFromJson);
        Double totalElevationChange = GradientUtils.parseTotalElevationChange(elevationResult);

        double units = 1;
        if(prefUnits.equals("feet")){
            units = 3.28084;
        }
        plotGraph(elevationResult, parseResolution, units);
        //plotGraph(elevationResult, units);

        System.out.println("gradients= " + gradients);
        double max = 0;
        int maxIndex = 0;
        for(int i = 0; i<gradients.size()-1; i++){
            if(Math.abs(gradients.get(i)) > max){
                max = Math.abs(gradients.get(i));
                maxIndex = i;
            }
        }

        System.out.println("Pref data:" + prefData + " PrefUnits: " + prefUnits);
        if(prefData.equals("total")){
            mResultsTitleTV.setText(getString(R.string.total_elevation_change));
            if(prefUnits.equals("feet")){
                totalElevationChange *= 3.28084;
                mTotalGradientChangeTV.setText(String.format("%s Feet", round(totalElevationChange,4)));
            }
            else{
                mTotalGradientChangeTV.setText(String.format("%s Meters", round(totalElevationChange,4)));
            }
        }
        else {
            mResultsTitleTV.setText(getString(R.string.total_gradient_change));
            mTotalGradientChangeTV.setText(String.format("%s%%", round(totalGradientChange, 4)));
        }
        mMaxGradientTV.setText(String.format("%s%%", round(gradients.get(maxIndex), 4)));
    }

    private void plotGraph(ArrayList<Double> elevationResult, ArrayList<Double> distanceBetweenSamples, double units){
        double x,y;
        x = 0;
        GraphView graph = (GraphView)findViewById(R.id.graph);
        //StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //staticLabelsFormatter.setHorizontalLabels(new String[] {"Distance", "Distance"});
        //staticLabelsFormatter.setVerticalLabels(new String[] {"Elevation", "Elevation"});
        //graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        series = new LineGraphSeries<DataPoint>();
        System.out.println(elevationResult.size());
        System.out.println(distanceBetweenSamples.size());
        for(int i = 0; i<elevationResult.size()-1; i++){
            x = x+distanceBetweenSamples.get(i)*units;
            //x = x+1;
            y = elevationResult.get(i)*units;
            series.appendData(new DataPoint(x,y), true, elevationResult.size());
        }
        graph.addSeries(series);
    }

    private static double round(double value, int decimalPlaces){
        return (double)Math.round(value * Math.pow(10,decimalPlaces)) / Math.pow(10,decimalPlaces);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result,
                menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                showRouteMap();
                return true;
            case R.id.action_share:
                shareRoute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showRouteMap() {
        String mapString = "google.navigation:q="+endLocation+"&mode=b";
        Uri gmmIntentUri = Uri.parse(mapString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void shareRoute() {
        if (startLocation != null && endLocation != null) {
            String shareString = "";
            if(mSharedPreferences.getString(getString(R.string.pref_data_key),getString(R.string.pref_data_default)).equals("total")) {
                shareString = " total elevation change";
            }
            else {
                shareString = " total gradient";
            }
            String shareText = "Route information for " + startLocation + " to " + endLocation +
                    ": " + mMaxGradientTV.getText().toString() + " max gradient, " +
                    mTotalGradientChangeTV.getText().toString() + shareString;
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(shareText)
                    .setChooserTitle(R.string.share_chooser)
                    .startChooser();
        }
    }
}
