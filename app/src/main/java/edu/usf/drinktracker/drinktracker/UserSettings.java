package edu.usf.drinktracker.drinktracker;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Button changeAddressBttn, changeWeightBttn, submitBttn, changeNameBttn, changeEmailBttn;
    EditText changeAddressTxt, changeWeightTxt;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth auth;
    FirebaseUser userFB;
    String userID, name, address, email, weightTxt;
    EditText changeNameTxt, changeEmailTxt;
    int weight;
    boolean sameData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Displays the back nav arrow

        submitBttn = (Button) findViewById(R.id.submit_bttn);

        changeAddressTxt = (EditText) findViewById(R.id.new_address_txt);
        changeWeightTxt = (EditText) findViewById(R.id.new_weight_txt);
        changeNameTxt = (EditText) findViewById((R.id.new_name_txt));
        changeEmailTxt = (EditText) findViewById(R.id.new_email_txt) ;

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

                        changeNameTxt.setText(name);
                        changeAddressTxt.setText(address);
                        changeWeightTxt.setText(weightTxt);
                        changeEmailTxt.setText(email);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {/*Do Nothing*/}
                });
 /*       //Sets onClick listener for change name button
        changeNameBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(!changeNameTxt.isClickable()){
                    makeClickable(changeNameTxt);
                }
                else
                    makeUnClickable(changeNameTxt);
            }
        });
        //Sets an OnClick Listener for the change address button
        changeAddressBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!changeAddressTxt.isClickable()) { //So if the user has not started changing an address already
                    //makes it clickable, have cursor, and focusable
                    makeClickable(changeAddressTxt);
                }
                else {
                    makeUnClickable(changeAddressTxt);
                }
            }
        });

        //Sets an OnClick Listener for the change address button
        changeWeightBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!changeWeightTxt.isClickable()) { //So if the user has not started changing an address already
                    makeClickable(changeWeightTxt);
                }
                else {
                    makeUnClickable(changeWeightTxt);
                }
            }
        });
        changeEmailBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(!changeEmailTxt.isClickable()){
                    makeClickable(changeEmailTxt);
                }
                else
                    makeUnClickable(changeEmailTxt);
            }
        });*/
        submitBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sameData = true; // if any needs to be updated, change to false to trigger no info update toast

                if(!changeNameTxt.getText().toString().equals(name)){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .child("Name").setValue(changeNameTxt.getText().toString());
                    Toast.makeText(getApplication(), "Name updated successfully", Toast.LENGTH_LONG).show();
                    sameData = false;
                }
                if (!changeWeightTxt.getText().toString().equals(weightTxt)) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .child("Weight").setValue(Long.valueOf(changeWeightTxt.getText().toString()));
                    Toast.makeText(getApplication(), "Weight updated successfully", Toast.LENGTH_LONG).show();
                    sameData = false;
                }
                if (!changeAddressTxt.getText().toString().equals(address)) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .child("Address").setValue(changeAddressTxt.getText().toString());
                    Toast.makeText(getApplication(), "Address updated successfully", Toast.LENGTH_LONG).show();
                    sameData = false;
                }
                if(!changeEmailTxt.getText().toString().equals(email)){
                     userFB = FirebaseAuth.getInstance().getCurrentUser();
                     userFB.updateEmail(email);
                    Toast.makeText(getApplication(), "Email updated successfully", Toast.LENGTH_LONG).show();
                    sameData = false;
                }
                if (sameData) {
                    Toast.makeText(getApplication(), "No information to update!", Toast.LENGTH_LONG).show();
                    return;
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
    private void makeClickable(EditText temp){
        temp.setClickable(true);
        temp.setFocusableInTouchMode(true);
        temp.setCursorVisible(true);
    }
    private void makeUnClickable(EditText temp){
        temp.setClickable(false);
        temp.setFocusableInTouchMode(false);
        temp.setCursorVisible(false);
    }
}
