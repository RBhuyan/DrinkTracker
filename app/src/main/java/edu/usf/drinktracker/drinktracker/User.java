package edu.usf.drinktracker.drinktracker;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
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


