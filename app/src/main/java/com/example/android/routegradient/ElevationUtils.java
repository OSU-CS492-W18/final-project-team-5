package com.example.android.routegradient;

import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import com.example.android.routegradient.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cbenson on 3/17/18.
 */

public class ElevationUtils {

    private final static String ELEVATION_BASE_URL = "https://maps.googleapis.com/maps/api/elevation";
    private final static String ELEVATION_FORMAT = "json";
    private final static String ELEVATION_SAMPLES = "512";

    public static String buildElevationURL(String routePath){
        //System.out.println(routePath);
        return Uri.parse(ELEVATION_BASE_URL).buildUpon()
                .appendPath(ELEVATION_FORMAT)
                .appendQueryParameter("path", "enc:" + routePath)
                .appendQueryParameter("samples", ELEVATION_SAMPLES)
                .appendQueryParameter("key", BuildConfig.ELEVATION_KEY)
                .build()
                .toString();
    }

    public static String[] buildElevationURLNew(ArrayList<Double> latlnglist){
        String[] searchResults = new String[latlnglist.size()/2];
        int j = 0;
        for(int i = 0; i<latlnglist.size()-1; i+=2){
            String url = "https://maps.googleapis.com/maps/api/elevation/json?locations="
                    + latlnglist.get(i) + ","
                    + latlnglist.get(i+1) +
                    "&key=" + BuildConfig.ELEVATION_KEY;
            searchResults[j] = url;
            j++;
        }
        return searchResults;
    }

    public static ArrayList<Double> parseElevationJSON(String elevationJSON){
        ArrayList<Double> elevations = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonArray elevationData = jsonParser.parse(elevationJSON)
                .getAsJsonObject().getAsJsonArray("results");

        for(JsonElement res : elevationData){
            Double elevation = res.getAsJsonObject().get("elevation").getAsDouble();

            elevations.add(elevation);
        }

        return elevations;
    }

    public static ArrayList<Double> parseElevationJSONNew(String elevationJSON){
        ArrayList<Double> elevations = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        JsonArray elevationData = jsonParser.parse(elevationJSON)
                .getAsJsonObject().getAsJsonArray("json");

        for(JsonElement res : elevationData){
            JsonArray array = res.getAsJsonObject().getAsJsonArray("results");//.getAsJsonObject().get("elevation").getAsDouble();

            for(JsonElement res2 : array){
                Double elevation = res2.getAsJsonObject().get("elevation").getAsDouble();
                elevations.add(elevation);
            }
            //Double elevation = res.getAsJsonObject().getAsJsonArray("results");//.getAsJsonObject().get("elevation").getAsDouble();
            //elevations.add(elevation);
        }
        //System.out.println(elevations);
        return elevations;
    }

//    public static ArrayList<Double> parseDistanceBetweenSamplesJSON(String elevationJSON){
//        ArrayList<Double> distanceBetweenSamples = new ArrayList<Double>();
//
//        JsonParser jsonParser = new JsonParser();
//        JsonArray elevationData = jsonParser.parse(elevationJSON)
//                .getAsJsonObject().getAsJsonArray("results");
//
//
//        for(JsonElement res : elevationData){
//            Double distance = res.getAsJsonObject().get("resolution").getAsDouble();
//            distanceBetweenSamples.add(distance);
//        }
//
//        return distanceBetweenSamples;
//    }


    public static ArrayList<Double> parseLatLngFromJSON(String json) {

        ArrayList<Double> distanceBetweenSamples = new ArrayList<Double>();

        JsonParser jsonParser = new JsonParser();
        JsonArray elevationData = jsonParser.parse(json)
                .getAsJsonObject().getAsJsonArray("results");


        for(JsonElement res : elevationData){
            Double distance1  = res.getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
            distanceBetweenSamples.add(distance1);
            Double distance2 = res.getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
            distanceBetweenSamples.add(distance2);
        }

        return distanceBetweenSamples;
    }


    public static ArrayList<Double> parseLatLngFromJSONTest(String json){

        ArrayList<Double> distanceBetweenSamples = new ArrayList<Double>();

        JsonParser jsonParser = new JsonParser();
        JsonArray route = jsonParser.parse(json)
                .getAsJsonObject().getAsJsonArray("routes").get(0)
                .getAsJsonObject().getAsJsonArray("legs").get(0)
                .getAsJsonObject().getAsJsonArray("steps");

        for(JsonElement res : route){
            Double distance1  = res.getAsJsonObject().get("start_location").getAsJsonObject().get("lat").getAsDouble();
            distanceBetweenSamples.add(distance1);
            Double distance2 = res.getAsJsonObject().get("start_location").getAsJsonObject().get("lng").getAsDouble();
            distanceBetweenSamples.add(distance2);
        }
        return distanceBetweenSamples;


    }

}
