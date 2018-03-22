package com.example.android.routegradient;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.android.routegradient.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by cbenson on 3/17/18.
 */

public class RouteUtils {

    private final static String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions";
    private final static String DIRECTIONS_FORMAT = "json";

    public static String buildRouteURL(String routeStart, String routeEnd, String mode){
        return Uri.parse(DIRECTIONS_BASE_URL).buildUpon()
                .appendPath(DIRECTIONS_FORMAT)
                .appendQueryParameter("origin", routeStart)
                .appendQueryParameter("destination", routeEnd)
                .appendQueryParameter("mode", mode)
                .appendQueryParameter("key", BuildConfig.DIRECTIONS_KEY)
                .build()
                .toString();
    }

    public static String getStatusFromJSON(String routeJSON) {
        String status = "";
        try {
            status = new JSONObject(routeJSON).getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return status;
    }

    public static ArrayList<String> parseRouteJSON(String routeJSON){
        ArrayList<String> polyLines = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray routeSteps = jsonParser.parse(routeJSON)
                .getAsJsonObject().getAsJsonArray("routes").get(0)
                .getAsJsonObject().getAsJsonArray("legs").get(0)
                .getAsJsonObject().getAsJsonArray("steps");

        for(JsonElement poly : routeSteps){
            polyLines.add(poly.getAsJsonObject().getAsJsonObject("polyline").get("points").getAsString());
        }

        return polyLines;
    }

}
