package edu.usf.drinktracker.drinktracker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;


//Will return the x y coordinates to DrinkSessionFragment
public class Locater {
    private double longitude, latitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    public double[] returnLocation() {
        double[] coordinates = new double[2];



        coordinates[0] = longitude;
        coordinates[1] = latitude;
        return coordinates;
    }

}
