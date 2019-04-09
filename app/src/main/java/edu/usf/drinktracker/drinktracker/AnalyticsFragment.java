/*
One of the fragments you can access with the bottom navigation bar
Will contain whatever analytic function we invoke to show your drinking history
 */
package edu.usf.drinktracker.drinktracker;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Date;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class AnalyticsFragment extends Fragment {
    TextView testTxt;
    Button testBttn;
    Button refresh;
    FirebaseAuth auth;
    String userID;
    int sessionNumber;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    int totalQuantity = 0;
    Double totalVolume = 0.0;
    Double highestVolume;

    TextView avg_drinks_val, avg_vol_val, highest_vol_val;

    public interface AnalyticsFragmentListener
    {
        void onInputASent(CharSequence input);
    }

    public static AnalyticsFragment newInstance() {
        AnalyticsFragment fragment = new AnalyticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_analytics, container, false);


        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        avg_drinks_val = (TextView) getActivity().findViewById(R.id.avg_drinks_val);
        avg_vol_val = (TextView) getActivity().findViewById(R.id.avg_vol_val);
        highest_vol_val = (TextView) getActivity().findViewById(R.id.highest_vol_val);
        //refresh = (Button) getActivity().findViewById(R.id.refresh);
        GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[]{ // these points are a test will add more points after each session.
               // new DataPoint(1, .08),
               // new DataPoint(2, .11),
              // new DataPoint(3, .05)
        });
        graph.setTitle("Average BAC Per Session");
        graph.setTitleTextSize(75);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Sessions");
        graph.getGridLabelRenderer().setPadding(60);

        graph.setBackgroundColor(Color.DKGRAY);
        graph.getGridLabelRenderer().setHumanRounding(false, false);
        graph.getGridLabelRenderer().setNumHorizontalLabels((int) series.getHighestValueX() + 1);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);

        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(series.getHighestValueX());
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(0.15);
        graph.getViewport().setMinY(0.0);

/*        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avg_vol_val.setText("90");
            }
        });*/
        //add if more than 1 session make graph visible

        //on data change for sessions, add new point for new session.
        /*refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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


                                final DatabaseReference drinks = FirebaseDatabase.getInstance().getReference().child("drinks");
                                drinks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        drinkList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String drinkType = ds.child("DrinkType").getValue(String.class);
                                            Double volume = ds.child("Volume").getValue(Double.class);

                                            totalVolume = totalVolume + volume;

                                            Date drinkDate = ds.child("DateTime").getValue(Date.class);

                                            int quantity = ds.child("Quantity").getValue(int.class);
                                            totalQuantity = totalQuantity + quantity;

                                            int drinkSessionNumber = ds.child("SessionNumber").getValue(int.class);
                                            String drinkUserID = ds.child("UserID").getValue(String.class);
                                            if (drinkUserID.equals(userID)) {
                                                drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                            }
                                        }
                                        //Initialize highest volume consumed to the first drink
                                        highestVolume = drinkList.get(0).Volume;

                                        for (int i = 0; i < drinkList.size(); i++) {
                                            Drink currentDrink = drinkList.get(i);


                                            //Calculate total amount of drinks had by the user in all sessions
                                            totalQuantity = totalQuantity + currentDrink.Quantity;

                                            //Calculate total volume of alcohol consumed by the user in all sessions
                                            totalVolume = totalVolume + (currentDrink.Quantity * currentDrink.Volume);

                                            //Get highest volume consumed
                                            if (currentDrink.Volume > highestVolume)
                                                highestVolume = currentDrink.Volume;

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
                //Calculate average amount of drinks had by the user in all sessions
                                /*int averageDrinks = totalQuantity/sessionNumber;
                                avg_drinks_val.setText(averageDrinks);
                                avg_drinks_val.setVisibility(View.VISIBLE);

                                //Average volume per session (Add all (volume*quantity) / # of sessions)
                                Double totalVolConsumed = (totalVolume*totalQuantity)/sessionNumber;
                                avg_vol_val.setText(totalVolConsumed.toString());
                                avg_vol_val.setVisibility(View.VISIBLE);

                                //Highest Volume Consumed (Find max in the array of volumes)
                                highest_vol_val.setText(highest_vol_val.toString());
                                highest_vol_val.setVisibility(View.VISIBLE);*/
            }};
        //});
  // }}

