package edu.usf.drinktracker.drinktracker;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This class is not saved to the firebase database
//Just used as a struct for the RecyclerView in the SessionHistory section
@IgnoreExtraProperties
public class Session implements Serializable {
    public ArrayList<Drink> DrinkList;
    public int SessionNumber;
    public Session() {
        DrinkList = new ArrayList<Drink>();
    }

    public Session(int sessionNumber, ArrayList<Drink> drinkList) {
        this.SessionNumber = sessionNumber;
        this.DrinkList = drinkList;
    }


}
