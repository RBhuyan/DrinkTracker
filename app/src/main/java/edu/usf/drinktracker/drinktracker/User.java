//user class
package edu.usf.drinktracker.drinktracker;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    public String Name;
    public String Email;
    public String Password;
    public int Weight;
    public String Address;
    String Gender;

    public User() {
    }

    public User(String name, String password, String email, int weight, String address, String gender) {
        this.Name = name;
        this.Password = password;
        this.Email = email;
        this.Weight = weight;
        this.Address = address;
        this.Gender = gender;
    }


}


