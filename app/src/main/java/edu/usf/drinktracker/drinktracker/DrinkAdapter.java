//Adapter to dynamically change the listview in the DrinkSessionFragment
//Once completed the listview will show all drinks drunk in the current drinking session
package edu.usf.drinktracker.drinktracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class DrinkAdapter extends ArrayAdapter<Drink> {
    public DrinkAdapter(Context context, ArrayList<Drink> drinks) {
        super(context, 0, drinks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Drink drink = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drink_listview_layout, parent, false);
        }

        Log.d("ViewTesting", "ConvertView class is " + convertView.getClass().getName());
        Log.d("ViewTesting", "Convert view id of drink type is " + convertView.findViewById(R.id.drinkType).getClass().getName());
        Log.d("ViewTesting", "drink class name is " + drink.getClass().getName());
        Log.d("ViewTesting", "drink type is " + convertView.findViewById(R.id.drinkType).getClass().getName());

        TextView drinkType = (TextView) convertView.findViewById(R.id.drinkType);
        TextView drinkVolume = (TextView) convertView.findViewById(R.id.drinkVolume);
        TextView drinkQuantity = (TextView) convertView.findViewById(R.id.drinkQuantity);
        // Populate the data into the template view using the data object
        drinkType.setText(drink.DrinkType);
        drinkVolume.setText(Double.toString(drink.Volume));
        drinkQuantity.setText(Integer.toString(drink.Quantity));

        return convertView;
    }
}
