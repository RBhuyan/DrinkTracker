package edu.usf.drinktracker.drinktracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserSettings extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    Button changeAddressBttn, changeWeightBttn, deleteAccountBttn, submitBttn;
    EditText changeAddressTxt, changeWeightTxt;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth auth;
    String userID, name, address, email, weightTxt;
    int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Displays the back nav arrow

        changeAddressBttn = (Button) findViewById(R.id.change_address_bttn);
        changeWeightBttn = (Button) findViewById(R.id.change_weight_bttn);
        deleteAccountBttn = (Button) findViewById(R.id.delete_account_bttn);
        submitBttn = (Button) findViewById(R.id.submit_bttn);
        changeAddressTxt = (EditText) findViewById(R.id.new_address_txt);
        changeWeightTxt = (EditText) findViewById(R.id.new_weight_txt);

        //Sets up firebase
        auth = FirebaseAuth.getInstance();
       // mFirebaseInstance = FirebaseDatabase.getInstance();
        //mFirebaseDatabase = mFirebaseInstance.getReference("users"); //Gets reference to 'user' node in realtime database
        //mFirebaseInstance.getReference("app_title").setValue("Realtime Database");
        userID = auth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        name = (String) map.get("Name");
                        email = (String) map.get("Email");
                        address = (String) map.get("Address");
                        weight = ((Long) map.get("Weight")).intValue();
                        weightTxt = String.valueOf(weight);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {/*Do Nothing*/}
                });

        //Sets an OnClick Listener for the change address button
        changeAddressBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (changeAddressTxt.getVisibility() == View.GONE) { //So if the user has not started changing an address already
                    changeAddressTxt.setVisibility(View.VISIBLE);
                    changeAddressTxt.setText(address);
                }
                else {
                    //changeAddressTxt.setText(address);
                    changeAddressTxt.setVisibility(View.GONE);
                }
            }
        });

        //Sets an OnClick Listener for the change address button
        changeWeightBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (changeWeightTxt.getVisibility() == View.GONE) { //So if the user has not started changing an address already
                    changeWeightTxt.setVisibility(View.VISIBLE);
                    changeWeightTxt.setText(weightTxt);
                }
                else {
                    changeWeightTxt.setVisibility(View.GONE);
                    //changeWeightTxt.setText(email);
                }
            }
        });

        submitBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (changeWeightTxt.getVisibility() == View.GONE && changeAddressTxt.getVisibility()==View.GONE) { //So if the user has not started changing an address already
                    Toast.makeText(getApplication(), "No information to update!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (changeWeightTxt.getVisibility() == View.VISIBLE) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .child("Weight").setValue(Long.valueOf(changeWeightTxt.getText().toString()));
                    Toast.makeText(getApplication(), "Weight updated successfully", Toast.LENGTH_LONG).show();
                }
                if (changeAddressTxt.getVisibility() == View.VISIBLE) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .child("Address").setValue(changeAddressTxt.getText().toString());
                    Toast.makeText(getApplication(), "Address updated successfully", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.limited_menu_options, menu);
        return true;
    }
}
