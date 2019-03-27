package edu.usf.drinktracker.drinktracker;

import java.util.HashMap;
import java.util.Map;

public class Globals {
    private static Globals instance = null;

    //variables
    private static Double avg_drinks_val, avg_vol_val, highest_vol_val;
    private static Map<String, Double> averageBACmap = new HashMap<String, Double>();

    private Globals(){}

    //Average drinks val
    public void setAvg_drinks_val (Double input)
    {
        Globals.avg_drinks_val = input;
    }
    public Double getAvg_drinks_val()
    {
        return Globals.avg_drinks_val;
    }

    //Average volume val
    public void setAvg_vol_val (Double input)
    {
        Globals.avg_vol_val = input;
    }
    public Double getAvg_vol_val()
    {
        return Globals.avg_vol_val;
    }

    //Highest Volume Val
    public void setHighest_vol_val (Double input)
    {
        Globals.highest_vol_val = input;
    }
    public Double getHighest_vol_val()
    {
        return Globals.highest_vol_val;
    }


    //Average BAC Map
    public void put(String session, Double bac) {
        averageBACmap.put(session, bac);
    }

    public Map getAvgBACMap()
    {
        return Globals.averageBACmap;
    }


    public static Globals getInstance()
    {
        if(instance == null)
        {
            instance = new Globals();
        }
        return instance;
    }


}
