package edu.usf.drinktracker.drinktracker;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public String password;
    public String uID;

    public User() {
    }

    public User(String name, String password, String email, String uID) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.uID = uID;
    }


}


