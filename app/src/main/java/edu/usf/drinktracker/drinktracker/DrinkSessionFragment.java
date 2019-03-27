/*
One of the fragments you can access with the bottom navigation bar.
This fragment will allow users to open a new drinking session, and then add in drinks using the floating action button.
The floating action button will open the NewDrink() activity, letting the user input a new drink.
It should probably display a graph showing time of drinks for current session

TODO: Implement NoSQL Firebase Database
*/
package edu.usf.drinktracker.drinktracker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.concurrent.Executor;
//import edu.usf.drinktracker.drinktracker.Globals;
import static com.firebase.ui.auth.ui.email.EmailLinkFragment.TAG;


public class DrinkSessionFragment extends Fragment {
    private Drink mDrink;
    ListView lv;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    ArrayList<Drink> totalDrinkList = new ArrayList<Drink>();
    DrinkAdapter adapter;
    String strTest, userID, gender;
    Button startBttn, endBttn, refresh, ride;
    //String strTest, userID, gender, address;
    //Button startBttn, endBttn, refresh, ride;
    FirebaseAuth auth;
    TextView startTxt;
    int sessionNumber, weight;
    FloatingActionButton fab;
    String inSession;
    DatabaseReference ref;
    ProgressBar progress;
    ImageView drinkImg;
    TextView bacTxt, bacVal;
    private String m_Text = "";
    Date currDate, loggedDate;
    String drinkType, address;
    int volume, quantity;
    double homeLatitude, homeLongitude;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    public FusedLocationProviderClient mFusedLocationClient;
    Double totalQuantity = 0.0, totalVolume = 0.0;
    Double highestVolume;
    ArrayList<Double> BACArray = new ArrayList<Double>();
    Double avgBAC = 0.0;

    Double bac = 0.0;

    public void newDrinkMenu() {
        Intent intent = new Intent(getContext(), NewDrink.class);
        intent.putExtra("sessionNumber", sessionNumber);
        intent.putExtra("userID", userID);
        intent.putExtra("gender", gender);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }

    //TODO: update the toggles of the in session/out of session
    //Initializes fragment
    public static DrinkSessionFragment newInstance() {
        DrinkSessionFragment fragment = new DrinkSessionFragment();
        return fragment;
    }

    public void setCustomObject(Drink object) {
        this.mDrink = object;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drink_session_new, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  testing intents
        final Home home = (Home) getActivity();

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();


        lv = getActivity().findViewById(R.id.drink_list);
        endBttn = getActivity().findViewById(R.id.end_session_bttn);
        fab = getActivity().findViewById(R.id.fab);
        drinkImg = getActivity().findViewById(R.id.truiton_image);
        startBttn = getActivity().findViewById(R.id.start_new_session);
        refresh = getActivity().findViewById(R.id.refresh);
        startTxt = getActivity().findViewById(R.id.new_session_txt);
        progress = getActivity().findViewById(R.id.progress_circular);
        bacTxt = getActivity().findViewById(R.id.bac_text);
        bacVal = getActivity().findViewById(R.id.bac_value);
        ride = getActivity().findViewById(R.id.ride);
        fab.clearAnimation();
        fab.hide();


        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Globals var = getInstance();
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                final String currGender = (String) map.get("Gender");
                                final int currWeight = ((Long) map.get("Weight")).intValue();
                                if (map.get("SessionNumber") == null)
                                    sessionNumber = 0;
                                else
                                    sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                                if (map.get("InSession").equals(null))
                                    inSession = "false";
                                else
                                    inSession = map.get("InSession").equals("True") ? "True" : "False";


                                final DatabaseReference drinks = FirebaseDatabase.getInstance().getReference().child("drinks");
                                drinks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        drinkList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String drinkType = ds.child("DrinkType").getValue(String.class);
                                            Double volume = ds.child("Volume").getValue(Double.class);
                                            Date drinkDate = ds.child("DateTime").getValue(Date.class);
                                            int quantity = ds.child("Quantity").getValue(int.class);
                                            int drinkSessionNumber = ds.child("SessionNumber").getValue(int.class);
                                            String drinkUserID = ds.child("UserID").getValue(String.class);
                                            if (drinkSessionNumber == sessionNumber && drinkUserID.equals(userID)) {
                                                drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                            }
                                        }

                                        //Calculate BAC
                                        //% BAC = (A x 5.14 / W x r) – .015 x H
                                        //A = liquid ounces of alcohol consumed
                                        //W = a person’s weight in pounds
                                        //r = a gender constant of alcohol distribution (.73 for men and .66 for women)*
                                        //H = hours elapsed since drinking commenced

                                        Double A = 0.0;
                                        Double ouncesDrank = 0.0;
                                        int hoursAtFirstDrink = drinkList.get(0).DateTime.getHours();
                                        highestVolume = drinkList.get(0).Volume;
                                        for (int i = 0; i < drinkList.size(); i++) {
                                            //Get total ounces of alcohol consumed
                                            Drink currentDrink = drinkList.get(i);
                                            ouncesDrank = currentDrink.Volume * currentDrink.Quantity;

                                            //Calculate total amount of drinks had by the user in all sessions
                                            totalQuantity = totalQuantity + currentDrink.Quantity;

                                            //Calculate total volume of alcohol consumed by the user in all sessions
                                            totalVolume = totalVolume + (currentDrink.Quantity * currentDrink.Volume);

                                            //Get highest volume consumed
                                            if (currentDrink.Volume > highestVolume)
                                                highestVolume = currentDrink.Volume;

                                            //Get alcohol content
                                            if (currentDrink.DrinkType.contains("Beer")) {
                                                A = A + (ouncesDrank * 0.05);
                                            }
                                            if (currentDrink.DrinkType.contains("Wine")) {
                                                A = A + (ouncesDrank * 0.12);
                                            }
                                            if (currentDrink.DrinkType.contains("Hard Liquor")) {
                                                A = A + (ouncesDrank * 0.4);
                                            }
                                            if (currentDrink.DrinkType.contains("Spirits")) {
                                                A = A + (ouncesDrank * 0.15);
                                            }
                                            if (currentDrink.DateTime.getHours() < hoursAtFirstDrink) {
                                                hoursAtFirstDrink = currentDrink.DateTime.getHours();
                                            }


                                        }
                                        Globals globals = Globals.getInstance();


                                        //Calculate average amount of drinks had by the user in all sessions
                                        Double averageDrinks = totalQuantity/sessionNumber;
                                        globals.setAvg_drinks_val(averageDrinks);
                                        //avg_drinks_val.setVisibility(View.VISIBLE);

                                        //Average volume per session (Add all (volume*quantity) / # of sessions)
                                        Double totalVolConsumed = (totalVolume*totalQuantity)/sessionNumber;
                                        globals.setAvg_vol_val(totalVolConsumed);
                                        //avg_vol_val.setText(totalVolConsumed.toString());
                                        //avg_vol_val.setVisibility(View.VISIBLE);

                                        //Highest Volume Consumed (Find max in the array of volumes)
                                        globals.setHighest_vol_val(highestVolume);
                                        //highest_vol_val.setText(highest_vol_val.toString());
                                        //highest_vol_val.setVisibility(View.VISIBLE);
                                        Double r;
                                        if (currGender == "Female")
                                            r = 0.66;
                                        else
                                            r = 0.73;


                                        Date currDate = new Date();
                                        int currHour = currDate.getHours();

                                        int H = currHour - hoursAtFirstDrink;

                                        bac = (A * 5.14) / (currWeight * r) - (0.015 * H);
                                        BACArray.add(bac);
                                        DecimalFormat df2 = new DecimalFormat(".##");
                                        String formattedBAC = df2.format(bac);


                                        bacVal.setText(df2.format(bac));
                                        bacVal.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });
        //OH NO
        startBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                sessionNumber = (((Long) map.get("SessionNumber")).intValue()) + 1;
                                gender = (String) map.get("Gender");
                                weight = ((Long) map.get("Weight")).intValue();

                                loggedDate = (Date) map.get("DateTime");
                                drinkType = (String) map.get("DrinkType");
                                //quantity = (int) map.get("Quantity");
                                //volume = (int) map.get("Volume");


                                home.setGender(gender);
                                home.setWeight(weight);


                                //Sets the user's session number to +1 it's current value and sets In Current Session to be true
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("SessionNumber").setValue(Long.valueOf(sessionNumber));
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("InSession").setValue("True");

                                //VISIBILITIES
                                progress.setVisibility(View.GONE);
                                startBttn.setVisibility(View.GONE);
                                startTxt.setVisibility(View.GONE);
                                drinkImg.setVisibility(View.GONE);
                                ride.setVisibility(View.VISIBLE);
                                fab.show();
                                lv.setVisibility(View.VISIBLE);
                                bacTxt.setVisibility(View.VISIBLE);
                                bacVal.setVisibility(View.VISIBLE);
                                endBttn.setVisibility(View.VISIBLE);
                                refresh.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {/*Do Nothing, mandatory to put here*/}
                        });
            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map.get("SessionNumber") == null)
                            sessionNumber = 0;
                        else
                            sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                        if (map.get("InSession").equals(null))
                            inSession = "false";
                        else
                            inSession = map.get("InSession").equals("True") ? "True" : "False";
                        //sessionNumber = 1;
                        //inSession = "false";
                        address = map.get("Address").toString();
                        DatabaseReference drinkRef = FirebaseDatabase.getInstance().getReference().child("drinks");
                        drinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                drinkList = new ArrayList<>();
                                totalDrinkList = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String drinkType = ds.child("DrinkType").getValue(String.class);
                                    Double volume = ds.child("Volume").getValue(Double.class);
                                    Date drinkDate = ds.child("DateTime").getValue(Date.class);
                                    int quantity = ds.child("Quantity").getValue(int.class);
                                    int drinkSessionNumber = ds.child("SessionNumber").getValue(int.class);
                                    String drinkUserID = ds.child("UserID").getValue(String.class);
                                    if (drinkUserID.equals(userID)) {
                                        totalDrinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                    }
                                    if (drinkSessionNumber == sessionNumber && drinkUserID.equals(userID)) {
                                        drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                    }
                                }
                                home.setDrinkList(totalDrinkList);

                                adapter = new DrinkAdapter(getActivity(), drinkList);
                                lv.setAdapter(adapter);

                                progress.setVisibility(View.GONE);
                                if (inSession.equals("True")) {
                                    startTxt.setVisibility(View.GONE);
                                    drinkImg.setVisibility((View.GONE));
                                    startBttn.setVisibility(View.GONE);
                                    fab.show();
                                    ride.setVisibility(View.VISIBLE);
                                    lv.setVisibility(View.VISIBLE);
                                    bacVal.setVisibility(View.VISIBLE);
                                    bacTxt.setVisibility(View.VISIBLE);
                                    endBttn.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                } else {
                                    startTxt.setVisibility(View.VISIBLE);
                                    drinkImg.setVisibility(View.VISIBLE);
                                    startBttn.setVisibility(View.VISIBLE);
                                    fab.hide();
                                    ride.setVisibility(View.VISIBLE);
                                    lv.setVisibility(View.GONE);
                                    bacTxt.setVisibility(View.GONE);
                                    bacVal.setVisibility(View.GONE);
                                    endBttn.setVisibility(View.GONE);
                                    refresh.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        //Sets up listener on floating action button to open a new instance of NewDrink
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDrinkMenu();
            }
        });

        //for uber shenanigans
        ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Location stuff
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    homeLatitude = addresses.get(0).getLatitude();
                    homeLongitude = addresses.get(0).getLongitude();
                } else {
                    homeLatitude = 28.056999; //if not valid address, set coords to USF
                    homeLongitude = -82.425987;
                }
                //Current location
                Location location = ((Home) getActivity()).updateLocation();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("\n");
                // Set up the input

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

                RideRequestButton requestButton = new RideRequestButton(view.getContext());
                //ConstraintLayout layout = (ConstraintLayout) getView();
                //layout.addView(requestButton);

                RideParameters rideParams = new RideParameters.Builder()
                        // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                        //.setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                        // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                        .setDropoffLocation(
                                homeLatitude, homeLongitude, "Home", address)
                        // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                        .setPickupLocation(location.getLatitude(), location.getLongitude(), "Current Location", "132 Valley Cir, Brandon, FL 33510")

                        .build();
                // set parameters for the RideRequestButton instance
                requestButton.setRideParameters(rideParams);

                ServerTokenSession session = new ServerTokenSession(config);
                requestButton.setSession(session);
                requestButton.loadRideInformation();

                builder.setView(requestButton);

                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        //When the user ends a session
        endBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < BACArray.size(); i++)
                {
                    avgBAC = avgBAC + BACArray.get(i);
                }
                avgBAC = avgBAC/sessionNumber;
                Globals globals = Globals.getInstance();
                if(sessionNumber < 10)
                    globals.put(Integer.toString(sessionNumber), avgBAC);

                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("InSession").setValue("False");
                                //We increment the session number when a session is started so all we have to do is tell the database
                                //the user is no longer in a session

                                FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("InSession").setValue("False");

                                startBttn.setVisibility(View.VISIBLE);
                                startTxt.setVisibility(View.VISIBLE);
                                drinkImg.setVisibility(View.VISIBLE);
                                ride.setVisibility(View.VISIBLE);
                                fab.hide();
                                lv.setVisibility(View.GONE);
                                lv.setAdapter(null);
                                bacVal.setVisibility(View.GONE);
                                bacTxt.setVisibility(View.GONE);
                                endBttn.setVisibility(View.GONE);
                                refresh.setVisibility(View.GONE);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });
    }
}
