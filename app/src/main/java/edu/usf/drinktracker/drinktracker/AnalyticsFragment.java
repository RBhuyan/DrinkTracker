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
import java.util.HashMap;
import java.util.Map;


public class AnalyticsFragment extends Fragment {
    TextView testTxt;
    Button testBttn;
    FirebaseAuth auth;
    String userID, gender;
    DataPoint[] dp;
    int sessionNumber, weight;
    ArrayList<Drink> drinkList;
    ArrayList<Session> sessionList;
    Double avgVolume, avgQuant;
    Double maxV;
    HashMap<Integer, ArrayList<Drink>> sessionMap;
    HashMap<Integer, Double> sessionAverages;

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
        return inflater.inflate(R.layout.fragment_analytics, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Home home = (Home) getActivity();
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        avg_drinks_val = (TextView) getActivity().findViewById(R.id.avg_drinks_val);
        avg_vol_val = (TextView) getActivity().findViewById(R.id.avg_vol_val);
        highest_vol_val = (TextView) getActivity().findViewById(R.id.max_vol);
        GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);
        gender = home.getGender();
        weight = home.getWeight();

        drinkList = home.getDrinkList();
        sessionMap = new HashMap<Integer, ArrayList<Drink>>();
        sessionList = new ArrayList<Session>();


        //Iterate it through each drink and end up with a HashMap mapping session numbers to an ArrayList of the drinks in that session
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
            if(sessionMap.get(i).size() > 1) {
                sessionList.add(new Session(i, sessionMap.get(i)));
            }
        }

        if (sessionList.size() < 2) {
            avg_vol_val.setText("Needs more info");
            avg_drinks_val.setText("Needs more info");
            highest_vol_val.setText("Needs more info");
        }

        avg_vol_val.setText(String.valueOf(new DecimalFormat("#.##").format(returnAverageVolume(sessionList))));
        avg_drinks_val.setText(String.valueOf(new DecimalFormat("#.##").format(returnAverageQuantity(sessionList))));
        highest_vol_val.setText(String.valueOf(new DecimalFormat("#.##").format(returnMaxVolume(sessionList))));
        sessionAverages = returnSessionAverages(sessionList);

        dp = new DataPoint[sessionAverages.size()];
        for (int i : sessionAverages.keySet()) {
            System.out.println(i + " , " + sessionAverages.get(i));
            DataPoint dataP = new DataPoint(i, sessionAverages.get(i)/100);
            dp[i] = dataP;
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(dp);

        graph.setTitle("Average BAC Per Session");
        graph.setTitleTextSize(75);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Sessions");
        graph.getGridLabelRenderer().setPadding(60);

        graph.setBackgroundColor(Color.DKGRAY);
        graph.getGridLabelRenderer().setHumanRounding(false, false);
        graph.getGridLabelRenderer().setNumHorizontalLabels((int) series.getHighestValueX());
        graph.getGridLabelRenderer().setNumVerticalLabels(6);

        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(series.getHighestValueX());
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(0.15);
        graph.getViewport().setMinY(0.0);
    }

    public Double returnAverageVolume(ArrayList<Session> sList){
        double totalVol = 0.0;
        for (Session s : sList) {
            for (Drink d : s.DrinkList) {
                totalVol += d.Volume;
            }
        }
        return totalVol/ sList.size();
    }

    public double returnAverageQuantity(ArrayList<Session> sList){
        double totalQuant = 0.0;
        for (Session s : sList) {
            for (Drink d : s.DrinkList) {
                totalQuant += d.Quantity;
            }
        }
        return totalQuant/ sList.size();
    }

    public double returnMaxVolume(ArrayList<Session> sList) {
        double localMax = 0.0;
        double currentVol = 0.0;
        for (Session s : sList) {
            for (Drink d : s.DrinkList) {
                 currentVol = d.Volume;
            }
            if (currentVol > localMax) {
                localMax = currentVol;
            }
        }
        return localMax;
    }

    //Escape function if Session's DrinkList is null, empty, or only has 1 element
    public HashMap<Integer, Double> returnSessionAverages(ArrayList<Session> sList) {
        HashMap<Integer, Double> map = new HashMap<>();
        int count = 0;
        Double localTotal = 0.0;
        Date startDate;
        for (Session s : sList) {
            for (Drink d : s.DrinkList) {
                Map<Date, Double> sessionTracker = BACtracker.sessionBacTracker(s.DrinkList, gender, weight);
                for (Date date : sessionTracker.keySet()) {
                    localTotal += sessionTracker.get(date);
                }
            }
            map.put(count, (localTotal/s.DrinkList.size()));
            count++;
        }
        return map;
    }
}


