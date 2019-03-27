package edu.usf.drinktracker.drinktracker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SessionAdapter extends ArrayAdapter<Session> {
    DrinkAdapter adapter;
    String gender;
    int weight, realSize;
    DataPoint[] dp;
    public SessionAdapter(Context context, ArrayList<Session> sessions) {
        super(context, 0, sessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Session session = getItem(position);

        gender = LogHistoryFragment.gender;
        weight = LogHistoryFragment.weight;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_listview_layout, parent, false);
        }
        if (gender == null) {
            //System.out.println("Gender is null");
        }
       // System.out.println("Gender is " + gender);
        //System.out.println("Weight is " + weight);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        GraphView graph = (GraphView) convertView.findViewById(R.id.graph);

        String cardTitle = "Session " + String.valueOf(session.SessionNumber);
        title.setText(cardTitle);

        HashMap<Integer, ArrayList<Drink>>sessionMap = new HashMap<Integer, ArrayList<Drink>>();

        Collections.sort(session.DrinkList, new Comparator<Drink>() { //sorts the drinkList by dateTime
            @Override
            public int compare(Drink r1, Drink r2) {
                return r1.DateTime.compareTo(r2.DateTime);
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("dd-HH-m");
        int count = 0;
        int listSize = session.DrinkList.size();
        if(!(listSize > 0)) {
            try {
                DataPoint[] dp = new DataPoint[1];
                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
                graph.addSeries(series);
            }
            catch (Exception e) {

            }
        }
        else {
            Map<Date, Double> returnPoints = BACtracker.sessionBacTracker(session.DrinkList, gender, weight);
            Map<Date, Double> map = new TreeMap<Date, Double>(returnPoints);
            realSize = map.size();
            dp = new DataPoint[realSize];
            for (Date d : map.keySet()) {
                DataPoint dpp = new DataPoint(d, map.get(d));
                //System.out.println("*********  " + session.SessionNumber);
                System.out.println("Drink number: " + count);
                //System.out.println(d);
                //System.out.println(map.get(d));
                dp[count] = dpp;
                count++;
            }
        }
        try {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
            graph.addSeries(series);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(), format));
            //graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(session.DrinkList.get(0).DateTime.getTime());
            graph.getViewport().setMaxX(session.DrinkList.get(realSize - 1).DateTime.getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
        }
        catch (Exception e) {
            System.out.println(e);
        }


        return convertView;
    }
}
