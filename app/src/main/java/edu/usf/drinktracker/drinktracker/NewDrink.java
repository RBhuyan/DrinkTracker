//Window for a user to input a new drink
package edu.usf.drinktracker.drinktracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.lang.String;
import java.lang.Object;

public class NewDrink extends AppCompatActivity {
    EditText volume;
    Spinner drinkSelecter, quantitySelecter;
    android.support.v7.widget.Toolbar toolbar;
    Button button;
    Drink drink;
    int sessionNumber, weight;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth auth;
    String userID, gender;
    int hour, min;

    private TimePicker timePicker1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drink);

        Intent intent = getIntent();
        sessionNumber = intent.getIntExtra("sessionNumber", 0);
        userID = intent.getStringExtra("userID");
        gender = intent.getStringExtra("gender");
        weight = intent.getIntExtra("weight", 0);
        //TODO: Implement the new Drink object with the user's gender and weight

        //Creates an 'up arrow' in the toolbar to go back to the 'home' activity
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drinkSelecter = (Spinner) findViewById(R.id.drink_options);
        quantitySelecter = (Spinner) findViewById(R.id.quantity_options);
        volume = (EditText) findViewById(R.id.volume);
        button  = (Button) findViewById(R.id.add_drink);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        hour = timePicker1.getCurrentHour();
        min = timePicker1.getCurrentMinute();

        /********/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the volume the user input in is actually a double
                try {double volumeCheck = Double.parseDouble(volume.getText().toString()); }
                catch(Exception e) {
                    Toast.makeText(NewDrink.this, "Please enter volume as a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY,hour);
                cal.set(Calendar.MINUTE,min);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);

                Date currentDate = cal.getTime();
                //Date currentDate = new Date();
                //Change current time to user's time
                //currentDate.setHours(hour);
                //currentDate.setMinutes(min);

                //TODO: Make sure program does not crash if sessionNumber is invalid
                Drink drink = new Drink(drinkSelecter.getSelectedItem().toString(), Double.parseDouble(volume.getText().toString()),
                        Integer.parseInt(quantitySelecter.getSelectedItem().toString()), currentDate, sessionNumber, userID );

                //Writes the drink to real-time database
                mFirebaseInstance = FirebaseDatabase.getInstance();
                mFirebaseDatabase = mFirebaseInstance.getReference("drinks");
                mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

                String drinkKey = mFirebaseDatabase.push().getKey();
                mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
                mFirebaseDatabase.child("drinks").child(drinkKey).setValue(drink);

                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
