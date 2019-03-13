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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class DrinkSessionFragment extends Fragment {
    private Drink mDrink;
    ListView lv;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    DrinkAdapter adapter;
    String strTest, userID;
    Button startBttn, endBttn;
    TextView startTxt;
    FirebaseAuth auth;
    int sessionNumber;
    FloatingActionButton fab;
    String inSession;
    DatabaseReference ref;
    //TODO: update the toggles of the in session/out of session
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
        View view = inflater.inflate(R.layout.fragment_drink_session, container, false);

        return view;
        //return inflater.inflate(R.layout.fragment_drink_session, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //  testing intents
        Home home = (Home) getActivity();

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();


        lv = (ListView) getActivity().findViewById(R.id.drink_list);
        startBttn = (Button) getActivity().findViewById(R.id.start_new_session);
        endBttn = (Button) getActivity().findViewById(R.id.end_session_bttn);
        startTxt = (TextView) getActivity().findViewById(R.id.new_session_txt);
        fab = getActivity().findViewById(R.id.fab);

        //OnClick Listener for the start view button
        startBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("SessionNumber") == null)
                                    sessionNumber = 1;
                                else
                                    sessionNumber = (((Long) map.get("SessionNumber")).intValue()) + 1;
                                //Sets the user's session number to +1 it's current value and sets In Current Session to be true
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("SessionNumber").setValue(Long.valueOf(sessionNumber));
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("InSession").setValue("True");

                                startBttn.setVisibility(View.GONE);
                                startTxt.setVisibility(View.GONE);
                                fab.show();
                                lv.setVisibility(View.VISIBLE);
                                endBttn.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {/*Do Nothing, mandatory to put here*/}
                        });
            }
        });

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if( map.get("SessionNumber") == null)
                            sessionNumber = 0;
                        else {
                            sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                        }
                        if(map.get("InSession") == null)
                        {
                            inSession = "false";
                        }
                        else
                            inSession = map.get("InSession").equals("True")?"True":"False";
                        //sessionNumber = 1;
                        //inSession = "false";

                        DatabaseReference  drinkRef = FirebaseDatabase.getInstance().getReference().child("drinks");
                        drinkRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                drinkList = new ArrayList<>();
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String drinkType = ds.child("DrinkType").getValue(String.class);
                                    Double volume = ds.child("Volume").getValue(Double.class);
                                    Date drinkDate = ds.child("DateTime").getValue(Date.class);
                                    int quantity = ds.child("Quantity").getValue(int.class);
                                    int drinkSessionNumber =  ds.child("SessionNumber").getValue(int.class);
                                    String drinkUserID = ds.child("UserID").getValue(String.class);
                                    if (drinkSessionNumber == sessionNumber && drinkUserID.equals(userID)) {
                                        drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                    }
                                }

                                adapter = new DrinkAdapter(getActivity(), drinkList);
                                lv.setAdapter(adapter);

                                //TODO: Handle logic if the user is in a session or not
                                if (inSession.equals("True")) {
                                    startTxt.setVisibility(View.INVISIBLE);
                                    startBttn.setVisibility(View.INVISIBLE);
                                    fab.show();
                                    lv.setVisibility(View.VISIBLE);
                                    endBttn.setVisibility(View.VISIBLE);
                                }
                                else {
                                    startTxt.setVisibility(View.VISIBLE);
                                    startBttn.setVisibility(View.VISIBLE);
                                    fab.hide();
                                    lv.setVisibility(View.INVISIBLE);
                                    endBttn.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });


        //Sets up listener on floating action button to open a new instance of NewDrink
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDrinkMenu();
            }
        });
    }

    private void newDrinkMenu() {
        Intent intent = new Intent(getContext(), NewDrink.class);
        intent.putExtra("sessionNumber", sessionNumber);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }
}
