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
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import static com.firebase.ui.auth.ui.email.EmailLinkFragment.TAG;


public class DrinkSessionFragment extends Fragment {
    private Drink mDrink;
    ListView lv;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    ArrayList<Drink> totalDrinkList = new ArrayList<Drink>();
    DrinkAdapter adapter;
    String strTest, userID, gender, address;
    Button startBttn, endBttn, refresh, ride;
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
    String drinkType;
    Location loc;
    int volume, quantity;
    double homeLatitude, homeLongitude;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private static final String KEY_LOCATION = "location";

    Double bac = 0.0;


    //TODO: update the toggles of the in session/out of session
    //Initializes fragment
    public static DrinkSessionFragment newInstance() {
        DrinkSessionFragment fragment = new DrinkSessionFragment();
        return fragment;
    }

    public void setCustomObject(Drink object){
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
        home.updateLocation();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        lv = (ListView) getActivity().findViewById(R.id.drink_list);
        endBttn = (Button) getActivity().findViewById(R.id.end_session_bttn);
        fab = getActivity().findViewById(R.id.fab);
        drinkImg = (ImageView) getActivity().findViewById(R.id.truiton_image);
        startBttn = (Button) getActivity().findViewById(R.id.start_new_session);
        refresh = (Button) getActivity().findViewById(R.id.refresh);
        startTxt = (TextView) getActivity().findViewById(R.id.new_session_txt);
        progress = (ProgressBar) getActivity().findViewById(R.id.progress_circular);
        bacTxt = (TextView) getActivity().findViewById(R.id.bac_text);
        bacVal = (TextView) getActivity().findViewById(R.id.bac_value);
        ride = (Button) getActivity().findViewById(R.id.ride);
        fab.clearAnimation();
        fab.hide();


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
                        if (map.get("Gender") == "male") {
                            gender = "male";
                        }
                        else {
                            gender = "female";
                        }
                        weight = ((Long) map.get("Weight")).intValue();
                        home.setGender(gender);
                        home.setWeight(weight);
                        if( map.get("SessionNumber") == null)
                            sessionNumber = 0;
                        else
                            sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                        if(map.get("InSession").equals(null))
                            inSession = "false";
                        else
                            inSession = map.get("InSession").equals("True")?"True":"False";
                        //sessionNumber = 1;
                        //inSession = "false";
                            address = map.get("Address").toString();
                        DatabaseReference  drinkRef = FirebaseDatabase.getInstance().getReference().child("drinks");
                        drinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                drinkList = new ArrayList<>();
                                totalDrinkList = new ArrayList<>();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String drinkType = ds.child("DrinkType").getValue(String.class);
                                    Double volume = ds.child("Volume").getValue(Double.class);
                                    Date drinkDate = ds.child("DateTime").getValue(Date.class);
                                    int quantity = ds.child("Quantity").getValue(int.class);
                                    int drinkSessionNumber =  ds.child("SessionNumber").getValue(int.class);
                                    String drinkUserID = ds.child("UserID").getValue(String.class);
                                    if (drinkUserID.equals(userID)) {
                                        totalDrinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                    }
                                    if (drinkSessionNumber == sessionNumber && drinkUserID.equals(userID)) {
                                        drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                    }
                                }
                                home.setDrinkList(totalDrinkList);
                                gender = home.getGender();
                                System.out.println(gender);
                                weight = home.getWeight();
                                ArrayList<Drink> dList = home.getDrinkList();
                                int currSize = drinkList.size();
                                Double currentBAC = 0.0;
                                if (currSize > 0) {
                                    currentBAC = BACtracker.liveBacTracker(drinkList, gender, weight, drinkList.get(currSize - 1).DateTime, new Date());
                                }
                                bacVal.setText(String.valueOf(currentBAC));

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
                                }
                                else {
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
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


        //Sets up listener on floating action button to open a new instance of NewDrink
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDrinkMenu();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Drink> dList = home.getDrinkList();
                int currSize = drinkList.size();
                Double currentBAC = BACtracker.liveBacTracker(drinkList, gender, weight, drinkList.get(currSize - 1).DateTime, new Date());
                bacVal.setText(String.valueOf(currentBAC));
            }
        });



        //for uber shenanigans
        ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Location stuff
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = new ArrayList<Address>();
                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "The address you entered is invalid! Until you enter a valid address in the User Options you cannot use the Uber feature",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(addresses == null || addresses.size() == 0) {
                    homeLatitude= 28.056999; //if not valid address, set coords to USF
                    homeLongitude = -82.425987;
                }
                else{
                    homeLatitude= addresses.get(0).getLatitude();
                    homeLongitude= addresses.get(0).getLongitude();
                }
                //Current location
                loc = home.getLocation();


                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("\n");

                SessionConfiguration config = new SessionConfiguration.Builder()
                        .setClientId("dKGoCOX5friZA3OIgcOFqh3714Er29oY")
                        .setServerToken("_tKgfeqrktEJXFYJPvnD4BQeof9eiN1wGsnhKxA3")
                        .setRedirectUri("DrinkTracker://oauth/callback")
                        .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                        .build();

                UberSdk.initialize(config);

                RideRequestButton requestButton = new RideRequestButton(view.getContext());
                //ConstraintLayout layout = (ConstraintLayout) getView();
                //layout.addView(requestButton);

                    RideParameters rideParams = new RideParameters.Builder()
                            .setDropoffLocation(
                                    homeLatitude, homeLongitude, "Home", address)
                            // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                            .setPickupLocation(loc.getLatitude(), loc.getLongitude(), "Current Location", "132 Valley Cir, Brandon, FL 33510")

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
                                FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("InSession").setValue("False");
                                home.setDrinkList(new ArrayList<Drink>());
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
        });

    }

    private void newDrinkMenu() {
        Intent intent = new Intent(getContext(), NewDrink.class);
        intent.putExtra("sessionNumber", sessionNumber);
        intent.putExtra("userID", userID);
        intent.putExtra("gender", gender);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }
}
