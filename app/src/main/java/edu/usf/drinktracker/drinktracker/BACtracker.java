package edu.usf.drinktracker.drinktracker;

import android.util.Log;

import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BACtracker {

    public static double liveBacTracker(ArrayList<Drink> drinkList, String gender, int weight, Date startTime, Date calcTime) {
        final double widmarkConstant = 0.017;
        final double maleConstant = 3.75;
        final double femaleConstant = 4.7;

        double totalAlcoholConsumed = 0; //In grams

        double alcPercent;
        double userConstant;
        for (Drink drink : drinkList) {
            switch(drink.DrinkType) {  //Parses the user's drink type option into the average alcohol level for that kind of drink
                case "Beer (4-8%)":
                    alcPercent = 4.5/100; //We divide by 100 to get a percentage
                    break;
                case "Wine":
                    alcPercent = 11.6/100;
                    break;
                case "Spirit":
                    alcPercent = 40/100;
                    break;
                case "Liqueur":
                    alcPercent = 22.5/100;
                    break;
                default:
                    return -1; //Should NEVER reach here!
            }
            totalAlcoholConsumed += (drink.Volume * alcPercent * 0.789);
        }
        //Sets the gender constant
        if (gender.equals("Male")){
            userConstant = 0.68;
        }
        else {
            userConstant = 0.55;
        }
        int listLength = drinkList.size();
        long diffInMill = calcTime.getTime() - drinkList.get(0).DateTime.getTime(); //Difference between the user given current calc time and the first time inputted
        //1 hour = 3600000 ms, which is what diffInMill is calculated in

        return ( ((totalAlcoholConsumed * 100)/(weight * 454 * userConstant)) - ((diffInMill * 0.015 / 3600000)) ); //final formula!
    }
    /*
    public static HashMap<DateTime, Double> sessionBacTracker(ArrayList<Drink> drinkList, String gender, int weight, Date startTime, Date calcTime) {
        final double widmarkConstant = 0.017;
        final double maleConstant = 3.75;
        final double femaleConstant = 4.7;

        double totalAlcoholConsumed = 0; //In grams

        double alcPercent;
        double userConstant;
        for (Drink drink : drinkList) {
            switch(drink.DrinkType) {  //Parses the user's drink type option into the average alcohol level for that kind of drink
                case "Beer":
                    alcPercent = 4.5/100; //We divide by 100 to get a percentage
                    break;
                case "Wine":
                    alcPercent = 11.6/100;
                    break;
                case "Spirit":
                    alcPercent = 40/100;
                    break;
                case "Liqueur":
                    alcPercent = 22.5/100;
                    break;
                default:
                    alcPercent = -1; //Should NEVER reach here!
            }
            totalAlcoholConsumed += (drink.Volume * alcPercent * 0.789);
        }
        //Sets the gender constant
        if (gender.equals("Male")){
            userConstant = maleConstant/weight;
        }
        else {
            userConstant = femaleConstant/weight;
        }
        int listLength = drinkList.size();
        long diffInMill = Math.abs(calcTime.getTime() - drinkList.get(0).DateTime.getTime()); //Difference between the user given current calc time and the first time inputted
        //1 hour = 3600000 ms, which is what diffInMill is calculated in

        return ((totalAlcoholConsumed * 100)/(weight * 454 * userConstant)) - (diffInMill * 0.015 / 3600000); //final formula!
    }
    */
}
