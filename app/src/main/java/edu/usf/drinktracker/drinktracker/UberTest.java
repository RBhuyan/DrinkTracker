package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;

public class UberTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubertest);
        Intent intent = getIntent();

        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("dKGoCOX5friZA3OIgcOFqh3714Er29oY")
                // required for enhanced button features
                .setServerToken("_tKgfeqrktEJXFYJPvnD4BQeof9eiN1wGsnhKxA3")
                // required for implicit grant authentication
                .setRedirectUri("DrinkTracker://oauth/callback")
                // optional: set sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);

        RideRequestButton requestButton = new RideRequestButton(this);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_activity);
        layout.addView(requestButton);

        RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(
                        37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                .build();
// set parameters for the RideRequestButton instance
        requestButton.setRideParameters(rideParams);

        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setSession(session);
        requestButton.loadRideInformation();

    }
}
