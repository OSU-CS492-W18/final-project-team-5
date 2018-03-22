package com.example.android.routegradient;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import com.example.android.routegradient.BuildConfig;

/**
 * Created by cbenson on 3/17/18.
 */

public class ElevationUtils {

    private final static String ELEVATION_BASE_URL = "https://maps.googleapis.com/maps/api/elevation";
    private final static String ELEVATION_FORMAT = "json";
    private final static String ELEVATION_SAMPLES = "512";

    public static String buildElevationURL(String routePath){
        return Uri.parse(ELEVATION_BASE_URL).buildUpon()
                .appendPath(ELEVATION_FORMAT)
                .appendQueryParameter("path", "enc:" + routePath)
                .appendQueryParameter("samples", ELEVATION_SAMPLES)
                .appendQueryParameter("key", BuildConfig.ELEVATION_KEY)
                .build()
                .toString();
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
    //List<int[]> myList = new ArrayList<int[]>();
    public static ArrayList<Double> parseDistanceBetweenSamplesJSON(String elevationJSON){
        ArrayList<Double> distanceBetweenSamples = new ArrayList<Double>();

        JsonParser jsonParser = new JsonParser();
        JsonArray elevationData = jsonParser.parse(elevationJSON)
                .getAsJsonObject().getAsJsonArray("results");


        for(JsonElement res : elevationData){
            Double distance = res.getAsJsonObject().get("resolution").getAsDouble();
            distanceBetweenSamples.add(distance);
        }

        return distanceBetweenSamples;
    }

    public static ArrayList<Double> parseLatLngFromJSON(String json){

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

}
