/*
One of the fragments you can access with the bottom navigation bar.
This fragment will allow users to open a new drinking session, and then add in drinks using the floating action button.
The floating action button will open the NewDrink() activity, letting the user input a new drink.
It should probably display a graph showing time of drinks for current session

TODO: Implement NoSQL Firebase Database
*/
package edu.usf.drinktracker.drinktracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DrinkSessionFragment extends Fragment {
    private Drink mDrink;
    ListView lv;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    DrinkAdapter adapter;
    String strTest;

    //Initializes fragment
    public static DrinkSessionFragment newInstance() {
        DrinkSessionFragment fragment = new DrinkSessionFragment();

        return fragment;
    }

    public void setCustomObject(Drink object){
        this.mDrink = object;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drink_session, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  testing intents
        Home home = (Home) getActivity();
        Drink testingIntentDrink = home.getDrink();
        if (testingIntentDrink != null) {
            Toast.makeText(getActivity(), testingIntentDrink.DrinkType, Toast.LENGTH_SHORT).show();
            drinkList.add(testingIntentDrink);
        }


        lv = (ListView) getActivity().findViewById(R.id.drink_list);

        //Sets up listener on floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDrinkMenu();
            }
        });

        adapter = new DrinkAdapter(getActivity(), drinkList);
        lv.setAdapter(adapter);

    }

    private void newDrinkMenu() {
        Intent intent = new Intent(getContext(), NewDrink.class);
        startActivity(intent);
    }

    public static void addNewDrink(Drink drink){
        //adapter.add(drink);
        //adapter.notifyDataSetChanged();
    }

}
