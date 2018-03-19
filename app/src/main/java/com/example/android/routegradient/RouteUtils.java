package com.example.android.routegradient;

import android.net.Uri;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.android.routegradient.BuildConfig;

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

    public static String parseRouteJSON(String routeJSON){
        JsonParser jsonParser = new JsonParser();
        JsonObject route = jsonParser.parse(routeJSON)
                .getAsJsonObject().getAsJsonArray("routes").get(0)
                .getAsJsonObject().getAsJsonArray("legs").get(0)
                .getAsJsonObject().getAsJsonArray("steps").get(0)
                .getAsJsonObject().getAsJsonObject("polyline");
        return route.get("points").getAsString();
    }

}
