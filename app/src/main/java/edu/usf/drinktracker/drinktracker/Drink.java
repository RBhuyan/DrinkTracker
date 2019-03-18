//Drink class
//Parameters are drink type, volume, quantity, and the dateTime of which the user inputted the drink
package edu.usf.drinktracker.drinktracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
//hello UI change
public class Drink implements Serializable {
    public int SessionNumber;
    public double Volume;
    public Date DateTime;
    public String DrinkType;
    public int Quantity;
    public String UserID;

    public Drink() {
    }

    public Drink(String drinkType, double volume, int quantity, Date dateTime, int sessionNumber, String userID) {
        this.Volume = volume;
        this.DateTime = dateTime;
        this.DrinkType = drinkType;
        this.Quantity = quantity;
        this.SessionNumber = sessionNumber;
        this.UserID = userID;
    }

}
