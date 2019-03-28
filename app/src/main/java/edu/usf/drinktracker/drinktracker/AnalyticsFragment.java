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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;


public class AnalyticsFragment extends Fragment {
    TextView testTxt;
    Button testBttn;
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

            GraphView graph = (GraphView) getActivity().findViewById(R.id.graph);
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] { // these points are a test will add more points after each session.
                    new DataPoint(1, .08),
                    new DataPoint(2, .11),
                    new DataPoint(3, .05)
            });

            //add points here

            graph.setTitle("Average BAC Per Session");
            graph.setTitleTextSize(75);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Sessions");
            graph.getGridLabelRenderer().setPadding(60);
            //graph.getGridLabelRenderer().setVerticalAxisTitle("BAC %");
            graph.setBackgroundColor(Color.DKGRAY);
            graph.getGridLabelRenderer().setHumanRounding(false, false);
            graph.getGridLabelRenderer().setNumHorizontalLabels((int)series.getHighestValueX());
            graph.getGridLabelRenderer().setNumVerticalLabels(6);
            //graph.getSecondScale().setMinY(0.0);
            //graph.getSecondScale().setMaxY(0.15);
            graph.addSeries(series);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMaxX(series.getHighestValueX());
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMaxY(0.15);
            graph.getViewport().setMinY(0.0);

            //add if more than 1 session make graph visible

            //on data change for sessions, add new point for new session.


        }
    }


