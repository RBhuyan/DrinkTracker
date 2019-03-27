/*
One of the fragments you can access with the bottom navigation bar
*/
package edu.usf.drinktracker.drinktracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;


public class LogHistoryFragment extends Fragment {
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    FirebaseUser user;
    String userID;
    HashMap<Integer, ArrayList<Drink>> sessionMap;
    ArrayList<Session> sessionList;
    GraphView graph;
    final int MAX_DATA_POINTS = 100;
    SimpleDateFormat format;
    private static final String TAG = "DRINKTAG";
    ListView lv;
    SessionAdapter adapter;
    public static int weight;
    public static String gender;

    public static LogHistoryFragment newInstance() {
        LogHistoryFragment fragment = new LogHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Home home = (Home) getActivity();
        //graph = (GraphView) getActivity().findViewById(R.id.graph);
        lv = (ListView) getActivity().findViewById(R.id.session_listview);
        user = user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        gender = home.getGender();
        weight = home.getWeight();

        sessionMap = new HashMap<Integer, ArrayList<Drink>>();
        drinkList = home.getDrinkList();
        sessionList = new ArrayList<Session>();

        for (Drink d : drinkList) {
            if (sessionMap.containsKey(d.SessionNumber)) {
                sessionMap.get(d.SessionNumber).add(d);
            } else {
                ArrayList<Drink> tempList = new ArrayList<Drink>();
                tempList.add(d);
                sessionMap.put(d.SessionNumber, tempList);
            }
        }
        for (Integer i : sessionMap.keySet()) {
            sessionList.add(new Session(i, sessionMap.get(i)));
        }

        adapter = new SessionAdapter(getActivity(), sessionList);
        lv.setAdapter(adapter);
    }



        /*
        ArrayList<Drink> testingDrinks = sessionList.get(0).DrinkList;
        Collections.sort(testingDrinks, new Comparator<Drink>() { //sorts the drinkList by dateTime
            @Override
            public int compare(Drink r1, Drink r2) {
                return r1.DateTime.compareTo(r2.DateTime);
            }
        });
        format = new SimpleDateFormat("MM-dd-HH-m");
        int count = 0;
        int listSize = testingDrinks.size();
        DataPoint[] dp = new DataPoint[listSize];
        Log.d("List size is " + Integer.toString(listSize), TAG);
        for (Drink d : testingDrinks) {
            Log.d(d.DateTime.toString(), TAG);
            DataPoint dpp = new DataPoint(d.DateTime, count);
            dp[count] = dpp;
            count++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), format));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(testingDrinks.get(0).DateTime.getTime());
        graph.getViewport().setMaxX(testingDrinks.get(listSize - 1).DateTime.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        */

        //We put an accessor function in Home so that we don't have to read from Firebase again! Following code has been replaced.
        /*
        DatabaseReference drinkRef = FirebaseDatabase.getInstance().getReference().child("drinks");
        drinkRef.addValueEventListener(new ValueEventListener() { //HERE
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drinkList = new ArrayList<>();
                sessionList = new ArrayList<Session>();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Drink drink = ds.getValue(Drink.class);

                    //First we have to map the drinks with the session number
                    //After we have finished iterating we will have a HashMap where the key is session number, and value is a drinkList containing the
                    //drinks consumed in that drinking session
                    if (drink.UserID.equals(userID)) {
                        if (sessionMap.containsKey(drink.SessionNumber)) {
                            sessionMap.get(drink.SessionNumber).add(drink);
                        }
                        else {
                            ArrayList<Drink> tempList = new ArrayList<Drink>();
                            tempList.add(drink);
                            sessionMap.put(drink.SessionNumber, tempList);
                        }
                    }
                }
                for (Integer i : sessionMap.keySet()) {
                    sessionList.add(new Session(i, sessionMap.get(i)));
                }
                //Now we have a List of Sessions, each Session has an integer of session number and a list of drinks
                ArrayList<Drink> testingDrinks = sessionList.get(0).DrinkList;
                Collections.sort(testingDrinks, new Comparator<Drink>() { //sorts the drinkList by dateTime
                    @Override
                    public int compare(Drink r1, Drink r2) {
                        return r1.DateTime.compareTo(r2.DateTime);
                    }
                });
                format = new SimpleDateFormat("MM-dd-HH-m");
                int count = 0;
                int listSize = testingDrinks.size();
                DataPoint[] dp = new DataPoint[listSize];
                Log.d("List size is " + Integer.toString(listSize), TAG);
                for (Drink d : testingDrinks) {
                    Log.d(d.DateTime.toString(), TAG);
                    DataPoint dpp = new DataPoint(d.DateTime, count);
                    dp[count] = dpp;
                    count++;
                }
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                graph.addSeries(series);

                // set date label formatter
                graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), format));
                graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

                // set manual x bounds to have nice steps
                graph.getViewport().setMinX(testingDrinks.get(0).DateTime.getTime());
                graph.getViewport().setMaxX(testingDrinks.get(listSize - 1).DateTime.getTime());
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getGridLabelRenderer().setHumanRounding(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        */
    }


