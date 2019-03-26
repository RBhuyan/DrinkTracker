/*
One of the fragments you can access with the bottom navigation bar
Will contain whatever analytic function we invoke to show your drinking history
 */
package edu.usf.drinktracker.drinktracker;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;


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
        final Home home = (Home) getActivity();

        testTxt = (TextView) getActivity().findViewById(R.id.temp_text);
        testBttn = (Button) getActivity().findViewById(R.id.test_button);

        testBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender = home.getGender();
                int weight = home.getWeight();
                ArrayList<Drink> tempList = home.getDrinkList();
                Date dd = new Date();
                Log.d("BAC", dd.toString());
                Log.d("BAC", tempList.get(0).DateTime.toString());
                double d = BACtracker.liveBacTracker(tempList, gender, weight, tempList.get(0).DateTime, dd);
                testTxt.setText(Double.toString(d));
            }
        });
    }

}
