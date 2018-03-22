package com.example.android.routegradient;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by alex on 3/20/2018.
 */

public class GradientUtils {


    /*
    Figures out the total elevation change as a distance
     */
    public static double parseTotalElevationChange(ArrayList<Double> elevations){
        double elevation1 = elevations.get(0);
        double elevation2 = elevations.get(elevations.size()-1);



        double deltaElevationAbs = elevation2-elevation1;
        return deltaElevationAbs;
    }


    /*
    Figures out the total elevation change as a gradient (-100.0 to 100.0)
     */
    public static double parseTotalGradientChange(ArrayList<Double> elevations, ArrayList<Double> LatLngFromJSON){
        double elevation1 = elevations.get(0);
        double elevation2 = elevations.get(elevations.size()-1);
        double deltaElevation = elevation2-elevation1;

        double totalDistanceFlat = 0;
        double lat1, lng1, lat2, lng2;
        for(int i = 0; i<LatLngFromJSON.size()-4; i+=2) {
            lat1 = LatLngFromJSON.get(i);
            lng1 = LatLngFromJSON.get(i + 1);
            lat2 = LatLngFromJSON.get(i + 2);
            lng2 = LatLngFromJSON.get(i + 3);
            totalDistanceFlat += distance(lat1, lng1, lat2, lng2);
        }

        return (Math.toDegrees(Math.atan(deltaElevation/totalDistanceFlat)));
    }

    /*
    Figures out the percent gradient (-100.0 to 100.0) between every point in the parsed elevations and latitude/longitudes
    From this ArrayList you could visualize how steep every portion of the route is
    IE: if the first step has gradient 12, it would be very steep upward gradient. If the first step has a gradient of -2, it would be a
    fairly shallow downhill gradient.
     */
//    public static ArrayList<Double> parseAllGradients(ArrayList<Double> elevations, ArrayList<Double> distanceBetweenSamples){
//        ArrayList<Double> gradient = new ArrayList<Double>();
//
//        double elevation1;
//        double elevation2;
//
//        for(int i = 0; i<elevations.size()-1; i++){
//            elevation1 = elevations.get(i);
//            elevation2 = elevations.get(i+1);
//
//            double distanceFlat = distanceBetweenSamples.get(i);
//            double deltaElevation = elevation1-elevation2;
//            gradient.add(rad2deg(Math.atan(deltaElevation/distanceFlat)) * 100);
//        }
//        return gradient;
//    }

    public static ArrayList<Double> parceDistanceBetweenLocations(ArrayList<Double> LatLngFromJSON){
        ArrayList<Double> distances = new ArrayList<Double>();

        double lat1, lng1, lat2, lng2;

        for(int i = 0; i<LatLngFromJSON.size()-3; i+=2){
            lat1 = LatLngFromJSON.get(i);
            lng1 = LatLngFromJSON.get(i+1);
            lat2 = LatLngFromJSON.get(i+2);
            lng2 = LatLngFromJSON.get(i+3);

            double distanceFlat =  distance(lat1, lng1, lat2, lng2);
            distances.add(distanceFlat);
        }
        return distances;
    }

    public static ArrayList<Double> parseAllGradients(ArrayList<Double> elevations, ArrayList<Double> LatLngFromJSON){
        ArrayList<Double> gradient = new ArrayList<Double>();

        double elevation1;
        double elevation2;

        double lat1, lng1, lat2, lng2;

        int j = 0;
        for(int i = 0; i<elevations.size()-2 && j<LatLngFromJSON.size()-4; i++){
            elevation1 = elevations.get(i);
            elevation2 = elevations.get(i+1);
            lat1 = LatLngFromJSON.get(j);
            lng1 = LatLngFromJSON.get(j+1);
            lat2 = LatLngFromJSON.get(j+2);
            lng2 = LatLngFromJSON.get(j+3);
            j+=2;


            double distanceFlat =  distance(lat1, lng1, lat2, lng2);
            double deltaElevation = elevation1-elevation2;
            gradient.add(Math.toDegrees(Math.atan(deltaElevation/distanceFlat)));
        }
        return gradient;
    }


    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        //System.out.println(distance);
        return (distance);
    }

    /*  This function converts decimal degrees to radians           */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*  This function converts radians to decimal degrees             */
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
