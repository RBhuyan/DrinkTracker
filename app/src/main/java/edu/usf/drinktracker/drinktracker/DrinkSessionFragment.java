package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.Map;
import java.text.DecimalFormat;


public class DrinkSessionFragment extends Fragment {
    private Drink mDrink;
    ListView lv;
    ArrayList<Drink> drinkList = new ArrayList<Drink>();
    DrinkAdapter adapter;
    String strTest, userID, gender;
    Button startBttn, endBttn, refresh;
    FirebaseAuth auth;
    TextView startTxt;
    int sessionNumber, weight;
    FloatingActionButton fab;
    String inSession;
    DatabaseReference ref;
    ProgressBar progress;
    ImageView drinkImg;
    TextView bacTxt, bacVal;
    Double bac = 0.0;


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
        return inflater.inflate(R.layout.fragment_drink_session_new, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //  testing intents
        Home home = (Home) getActivity();

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();


        lv = (ListView) getActivity().findViewById(R.id.drink_list);
        endBttn = (Button) getActivity().findViewById(R.id.end_session_bttn);
        fab = getActivity().findViewById(R.id.fab);
        drinkImg = (ImageView) getActivity().findViewById(R.id.truiton_image);
        startBttn = (Button) getActivity().findViewById(R.id.start_new_session);
        refresh = (Button) getActivity().findViewById(R.id.refresh);
        startTxt = (TextView) getActivity().findViewById(R.id.new_session_txt);
        progress = (ProgressBar) getActivity().findViewById(R.id.progress_circular);
        bacTxt = (TextView) getActivity().findViewById(R.id.bac_text);
        bacVal = (TextView) getActivity().findViewById(R.id.bac_value);
        fab.clearAnimation();
        fab.hide();


        refresh.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                final String currGender = (String) map.get("Gender");
                                final int currWeight = ((Long) map.get("Weight")).intValue();
                                if (map.get("SessionNumber") == null)
                                    sessionNumber = 0;
                                else
                                    sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                                if (map.get("InSession").equals(null))
                                    inSession = "false";
                                else
                                    inSession = map.get("InSession").equals("True") ? "True" : "False";


                                final DatabaseReference drinks = FirebaseDatabase.getInstance().getReference().child("drinks");
                                drinks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        drinkList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String drinkType = ds.child("DrinkType").getValue(String.class);
                                            Double volume = ds.child("Volume").getValue(Double.class);
                                            Date drinkDate = ds.child("DateTime").getValue(Date.class);
                                            int quantity = ds.child("Quantity").getValue(int.class);
                                            int drinkSessionNumber = ds.child("SessionNumber").getValue(int.class);
                                            String drinkUserID = ds.child("UserID").getValue(String.class);
                                            if (drinkSessionNumber == sessionNumber && drinkUserID.equals(userID)) {
                                                drinkList.add(new Drink(drinkType, volume, quantity, drinkDate, drinkSessionNumber, userID));
                                            }
                                        }

                                        //Calculate BAC
                                        //% BAC = (A x 5.14 / W x r) – .015 x H
                                        //A = liquid ounces of alcohol consumed
                                        //W = a person’s weight in pounds
                                        //r = a gender constant of alcohol distribution (.73 for men and .66 for women)*
                                        //H = hours elapsed since drinking commenced

                                        Double A =0.0;
                                        Double ouncesDrank = 0.0;
                                        int hoursAtFirstDrink = drinkList.get(0).DateTime.getHours();

                                        for(int i = 0; i < drinkList.size(); i++)
                                        {
                                            //Get total ounces of alcohol consumed
                                            Drink currentDrink = drinkList.get(i);
                                            ouncesDrank = currentDrink.Volume * currentDrink.Quantity;

                                            //Get alcohol content
                                            if(currentDrink.DrinkType.contains("Beer"))
                                            {
                                                A = A + (ouncesDrank * 0.05);
                                            }
                                            if(currentDrink.DrinkType.contains("Wine"))
                                            {
                                                A = A + (ouncesDrank * 0.12);
                                            }
                                            if(currentDrink.DrinkType.contains("Hard Liquor"))
                                            {
                                                A = A + (ouncesDrank * 0.4);
                                            }
                                            if(currentDrink.DrinkType.contains("Spirits"))
                                            {
                                                A = A + (ouncesDrank * 0.15);
                                            }
                                            if(currentDrink.DateTime.getHours() < hoursAtFirstDrink)
                                            {
                                                hoursAtFirstDrink = currentDrink.DateTime.getHours();
                                            }


                                        }
                                        Double r;
                                        if(currGender == "Female")
                                            r = 0.66;
                                        else
                                            r = 0.73;


                                        Date currDate = new Date();
                                        int currHour = currDate.getHours();

                                        int H = currHour - hoursAtFirstDrink;

                                        bac = (A * 5.14)/(currWeight * r) - (0.015 * H);
                                        DecimalFormat df2 = new DecimalFormat(".##");
                                        //df2.format(bac);
                                        //String bacText = bac.toString();
                                        bacVal.setText(df2.format(bac));
                                        bacVal.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });
        //OH NO
        startBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                sessionNumber = (((Long) map.get("SessionNumber")).intValue()) + 1;
                                gender = (String) map.get("Gender");
                                weight = ((Long) map.get("Weight")).intValue();

                                //Sets the user's session number to +1 it's current value and sets In Current Session to be true
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("SessionNumber").setValue(Long.valueOf(sessionNumber));
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("InSession").setValue("True");

                                //VISIBILITIES
                                progress.setVisibility(View.GONE);
                                startBttn.setVisibility(View.GONE);
                                startTxt.setVisibility(View.GONE);
                                drinkImg.setVisibility(View.GONE);
                                fab.show();
                                lv.setVisibility(View.VISIBLE);
                                //bacVal.setVisibility(View.VISIBLE);
                                bacTxt.setVisibility(View.VISIBLE);
                                endBttn.setVisibility(View.VISIBLE);
                                refresh.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {/*Do Nothing, mandatory to put here*/}
                        });
            }
        });



        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if( map.get("SessionNumber") == null)
                            sessionNumber = 0;
                        else
                            sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                        if(map.get("InSession").equals(null))
                            inSession = "false";
                        else
                            inSession = map.get("InSession").equals("True")?"True":"False";
                        //sessionNumber = 1;
                        //inSession = "false";

                        DatabaseReference  drinkRef = FirebaseDatabase.getInstance().getReference().child("drinks");
                        drinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                progress.setVisibility(View.GONE);
                                if (inSession.equals("True")) {
                                    startTxt.setVisibility(View.GONE);
                                    drinkImg.setVisibility((View.GONE));
                                    startBttn.setVisibility(View.GONE);
                                    fab.show();
                                    lv.setVisibility(View.VISIBLE);
                                    //bacVal.setVisibility(View.VISIBLE);
                                    bacTxt.setVisibility(View.VISIBLE);
                                    endBttn.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                }
                                else {
                                    startTxt.setVisibility(View.VISIBLE);
                                    drinkImg.setVisibility(View.VISIBLE);
                                    startBttn.setVisibility(View.VISIBLE);
                                    fab.hide();
                                    lv.setVisibility(View.GONE);
                                    bacTxt.setVisibility(View.GONE);
                                    //bacVal.setVisibility(View.GONE);
                                    endBttn.setVisibility(View.GONE);
                                    refresh.setVisibility(View.GONE);
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

        //When the user ends a session
        endBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userID)
                                        .child("InSession").setValue("False");
                                //We increment the session number when a session is started so all we have to do is tell the database
                                //the user is no longer in a session

                                startBttn.setVisibility(View.VISIBLE);
                                startTxt.setVisibility(View.VISIBLE);
                                drinkImg.setVisibility(View.VISIBLE);
                                fab.hide();
                                lv.setVisibility(View.GONE);
                                lv.setAdapter(null);
                                bacVal.setVisibility(View.GONE);
                                bacTxt.setVisibility(View.GONE);
                                endBttn.setVisibility(View.GONE);
                                refresh.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {/*Do Nothing, mandatory to put here*/}
                        });
            }
        });
    }

    private void newDrinkMenu() {
        Intent intent = new Intent(getContext(), NewDrink.class);
        intent.putExtra("sessionNumber", sessionNumber);
        intent.putExtra("userID", userID);
        intent.putExtra("gender", gender);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }


}