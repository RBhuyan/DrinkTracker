package edu.usf.drinktracker.drinktracker;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Session implements Serializable {
    public int SessionNumber;
    public Date Date;
    public ArrayList<Drink> DrinkList;

    public Session() {
        DrinkList = new ArrayList<Drink>();
    }

    public Session(int sessionNumber, Date date, ArrayList<Drink> drinkList) {
        this.SessionNumber = sessionNumber;
        this.Date = date;
        this.DrinkList = drinkList;
    }


}
