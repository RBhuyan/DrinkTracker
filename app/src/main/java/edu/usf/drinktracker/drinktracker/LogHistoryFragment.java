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
        user = FirebaseAuth.getInstance().getCurrentUser();
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
            if(sessionMap.get(i).size() > 1) {
                sessionList.add(new Session(i, sessionMap.get(i)));
            }
        }

        adapter = new SessionAdapter(getActivity(), sessionList);
        lv.setAdapter(adapter);
    }
    }


