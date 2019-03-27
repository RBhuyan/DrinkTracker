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

        double userConstant;
        for (Drink drink : drinkList) {
            double alcPercent = 0;

            if (drink.DrinkType.contains("Beer (4-8)%")) {
                alcPercent = 4.5/100;
            }
            else if (drink.DrinkType.contains("Wine")) {
                alcPercent = 11.6/100;
            }
            else if (drink.DrinkType.contains("Hard Liquor")) {
                alcPercent = 40/100;
            }
            else if (drink.DrinkType.contains("Spirits")) {
                alcPercent = 22.5/100;
            }
            else if (alcPercent == 0) {
                return -100;
            }
            totalAlcoholConsumed += (drink.Volume * alcPercent * 0.789 * 28.3945); //Remember unit conversion to grams
        }
        //Sets the gender constant
        System.out.println("Reached gender");
        System.out.println(gender);
        if (gender.equals("male")){
            userConstant = 0.68;
        }
        else {
            userConstant = 0.55;
        }
        int listLength = drinkList.size();
        //System.out.println("Reached list lenth");
        long diffInMill = calcTime.getTime() - drinkList.get(0).DateTime.getTime(); //Difference between the user given current calc time and the first time inputted
        //1 hour = 3600000 ms, which is what diffInMill is calculated in

        return ( ((totalAlcoholConsumed * 100)/(weight * 454 * userConstant)) - ((diffInMill * 0.015 / 3600000)) ); //final formula!
    }

    public static HashMap<Date, Double> sessionBacTracker(ArrayList<Drink> drinkList, String gender, int weight) {
        int listLength = drinkList.size();
        long diffInMill = Math.abs(drinkList.get(listLength - 1).DateTime.getTime() - drinkList.get(0).DateTime.getTime()); //Difference between the user given current calc time and the first time inputted
        //1 hour = 3600000 ms, which is what diffInMill is calculated in
        HashMap<Date, Double> graphPoints = new HashMap<Date, Double>();
        Date startDay = drinkList.get(0).DateTime;
        boolean first = true;
        for (Drink d : drinkList) {
            if (first) {
                graphPoints.put(startDay, 0.0);
                first = false;
            }
            else {
                Double dd = liveBacTracker(drinkList, gender, weight, startDay, d.DateTime);
                graphPoints.put(d.DateTime, dd);
            }
        }
        return graphPoints;
    }

}
