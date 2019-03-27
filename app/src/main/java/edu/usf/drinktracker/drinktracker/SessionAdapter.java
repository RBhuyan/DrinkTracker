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
    int weight;
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
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-HH-m");
        int count = 0;
        int listSize = session.DrinkList.size();
        DataPoint[] dp = new DataPoint[listSize];
        //for (Drink d : session.DrinkList) {
        //    DataPoint dpp = new DataPoint(d.DateTime, count);
        //    dp[count] = dpp;
        //    count++;
        //}

        HashMap<Date, Double> returnPoints = BACtracker.sessionBacTracker(session.DrinkList, gender, weight);
        Map<Date, Double> map = new TreeMap<Date, Double>(returnPoints);
        for (Date d : map.keySet()) {
            DataPoint dpp = new DataPoint(d, map.get(d));
            dp[count] = dpp;
            count++;
        }
        try {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
            graph.addSeries(series);

            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(), format));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            graph.getViewport().setMinX(session.DrinkList.get(0).DateTime.getTime());
            graph.getViewport().setMaxX(session.DrinkList.get(listSize - 1).DateTime.getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
        }
        catch (Exception e) {

        }


        return convertView;
    }
}
