package com.example.android.routegradient;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public static ArrayList<Integer> parseElevationJSON(String elevationJSON){
        ArrayList<Integer> elevations = new ArrayList<>(512);

        JsonParser jsonParser = new JsonParser();
        JsonArray elevationData = jsonParser.parse(elevationJSON)
                .getAsJsonObject().getAsJsonArray("results");

        for(JsonElement res : elevationData){
            Integer elevation = res.getAsJsonObject().get("elevation").getAsInt();

            elevations.add(elevation);
        }

        return elevations;
    }

}
