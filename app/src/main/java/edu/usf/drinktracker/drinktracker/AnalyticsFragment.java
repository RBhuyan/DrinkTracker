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
import android.provider.ContactsContract;
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
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


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
    Map<String, Double> avgBACMap = new HashMap<String, Double>();
    ArrayList<DataPoint> data = new ArrayList<DataPoint>();
    DataPoint[] dataToPlot = {};
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
        //GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        Globals globals = Globals.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();
        avg_drinks_val = (TextView) getActivity().findViewById(R.id.avg_drinks_val);
        avg_vol_val = (TextView) getActivity().findViewById(R.id.avg_vol_val);
        highest_vol_val = (TextView) getActivity().findViewById(R.id.highest_vol_val);
        refresh = (Button) getActivity().findViewById(R.id.refresh);
        GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);

        DecimalFormat df2 = new DecimalFormat(".##");

        avg_vol_val.setText(df2.format(globals.getAvg_vol_val()));
        avg_drinks_val.setText(df2.format(globals.getAvg_drinks_val()));
        highest_vol_val.setText(df2.format(globals.getHighest_vol_val()));

        //add if more than 1 session make graph visible
        /*refresh.setOnClickListener(new View.OnClickListener() {
            GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);
            public void onClick(View view)
            {
                Globals globals = Globals.getInstance();
                avgBACMap = globals.getAvgBACMap();
                //DataPoint[] data;

                for (Map.Entry<String, Double> entry : avgBACMap.entrySet())
                {
                    // these points are a test will add more points after each session.
                    //new DataPoint(Integer.parseInt(entry.getKey()), entry.getValue());
                    if(Integer.parseInt(entry.getKey()) % 10 == 0) {
                        DataPoint curr = new DataPoint(Integer.parseInt(entry.getKey()), entry.getValue());
                        data.add(curr);
                    }
                }
                //DataPoint[] dataToPlot = data.toArray();
                PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(data.toArray(new DataPoint[0]));

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
        });*/

        avgBACMap = globals.getAvgBACMap();
        //DataPoint[] data;

        for (Map.Entry<String, Double> entry : avgBACMap.entrySet())
        {

            DataPoint curr  = new DataPoint(Integer.parseInt(entry.getKey()), entry.getValue());
            data.add(curr);
        }
        //DataPoint[] dataToPlot = data.toArray();
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(data.toArray(new DataPoint[0]));

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



    }}