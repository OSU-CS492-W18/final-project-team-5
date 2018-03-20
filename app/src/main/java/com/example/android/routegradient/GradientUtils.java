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

        double deltaElevationAbs = Math.abs(elevation1-elevation2);
        return deltaElevationAbs;
    }


    /*
    Figures out the total elevation change as a gradient (-100.0 to 100.0)
     */
    public static double parseTotalGradientChange(ArrayList<Double> elevations, ArrayList<Double> distanceBetweenSamples){
        double elevation1 = elevations.get(0);
        double elevation2 = elevations.get(elevations.size()-1);
        double deltaElevation = elevation1-elevation2;

        double totalDistanceFlat = 0;
        for(int i = 0; i<distanceBetweenSamples.size()-1; i++){
            totalDistanceFlat += distanceBetweenSamples.get(i);
        }

        return rad2deg(Math.atan(deltaElevation/totalDistanceFlat)) * 100;
    }

    /*
    Figures out the percent gradient (-100.0 to 100.0) between every point in the parsed elevations and latitude/longitudes
    From this ArrayList you could visualize how steep every portion of the route is
    IE: if the first step has gradient 12, it would be very steep upward gradient. If the first step has a gradient of -2, it would be a
    fairly shallow downhill gradient.
     */
    public static ArrayList<Double> parseAllGradients(ArrayList<Double> elevations, ArrayList<Double> distanceBetweenSamples){
        ArrayList<Double> gradient = new ArrayList<Double>();

        double elevation1;
        double elevation2;

        for(int i = 0; i<elevations.size()-1; i++){
            elevation1 = elevations.get(i);
            elevation2 = elevations.get(i+1);

            double distanceFlat = distanceBetweenSamples.get(i);
            double deltaElevation = elevation1-elevation2;
            gradient.add(rad2deg(Math.atan(deltaElevation/distanceFlat)) * 100);
        }
        return gradient;
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
