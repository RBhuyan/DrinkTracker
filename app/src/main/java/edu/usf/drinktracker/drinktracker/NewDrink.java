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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class NewDrink extends AppCompatActivity {
    EditText volume;
    Spinner drinkSelecter, quantitySelecter;
    Button button;
    Drink drink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drink);

        //Creates an 'up arrow' in the toolbar to go back to the 'home' activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drinkSelecter = (Spinner) findViewById(R.id.drink_options);
        quantitySelecter = (Spinner) findViewById(R.id.quantity_options);
        volume = (EditText) findViewById(R.id.volume);
        button  = (Button) findViewById(R.id.add_drink);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the volume the user input in is actually a double
                try {double volumeCheck = Double.parseDouble(volume.getText().toString()); }
                catch(Exception e) {
                    Toast.makeText(NewDrink.this, "Please enter volume as a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date currentDate = new Date();
                Drink drink = new Drink(drinkSelecter.getSelectedItem().toString(), Double.parseDouble(volume.getText().toString()),
                        Integer.parseInt(quantitySelecter.getSelectedItem().toString()), currentDate );
                //Now we need to pass this new drink to the drink fragment and update the display, so we use an intent
                //Unfortunately we can't declare an intent inside of a listener so we make a separate function called submit


                submit(drink);
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

    private void submit(Drink drink){
        //DrinkSessionFragment frag = new DrinkSessionFragment();
        //frag.setCustomObject(drink);
        Home.drinkList.add(drink);
        Intent intent = new Intent(this, Home.class);
        //intent.putExtra("selectedDrink", drink);
        startActivity(intent);
    }



}
