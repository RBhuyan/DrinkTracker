package edu.usf.drinktracker.drinktracker;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SessionAdapter extends ArrayAdapter<Session> {
    DrinkAdapter adapter;
    public SessionAdapter(Context context, ArrayList<Session> sessions) {
        super(context, 0, sessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Session session = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.session_listview_layout, parent, false);
        }
        Context cont = parent.getContext();
        TextView title = (TextView) convertView.findViewById(R.id.drinkType);
        TextView drinkType = (TextView) convertView.findViewById(R.id.drinkType);
        TextView drinkVolume = (TextView) convertView.findViewById(R.id.drinkVolume);
        TextView drinkQuantity = (TextView) convertView.findViewById(R.id.drinkQuantity);
        GraphView graph = (GraphView) convertView.findViewById(R.id.graph);
        //ListView listView = (ListView) convertView.findViewById(R.id.drinks_list);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.drinks_list_layout);

        //adapter = new DrinkAdapter(cont, session.DrinkList);
        layout.removeAllViews();
        for (Drink d : session.DrinkList) {

        }

        //  todo: set up the graph!
        String cardTitle = "Session " + String.valueOf(session.SessionNumber);
        title.setText(cardTitle);

        HashMap<Integer, ArrayList<Drink>>sessionMap = new HashMap<Integer, ArrayList<Drink>>();

        ArrayList<Drink> testingDrinks = session.DrinkList;
        Collections.sort(testingDrinks, new Comparator<Drink>() { //sorts the drinkList by dateTime
            @Override
            public int compare(Drink r1, Drink r2) {
                return r1.DateTime.compareTo(r2.DateTime);
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-HH-m");
        int count = 0;
        int listSize = testingDrinks.size();
        DataPoint[] dp = new DataPoint[listSize];
        for (Drink d : testingDrinks) {
            DataPoint dpp = new DataPoint(d.DateTime, count);
            dp[count] = dpp;
            count++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dp);
        graph.addSeries(series);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(cont, format));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(testingDrinks.get(0).DateTime.getTime());
        graph.getViewport().setMaxX(testingDrinks.get(listSize - 1).DateTime.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);


        return convertView;
    }
}
