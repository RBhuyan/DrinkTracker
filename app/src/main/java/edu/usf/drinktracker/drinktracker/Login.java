package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    public static User currentUser;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    List<User> userList = new ArrayList<User>();

    private DatabaseReference mDatabase;
    DatabaseReference ref;

    Button signinButton,signupButton;
    EditText emailText,passwordText;
    List<User> users = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("users");

        emailText = (EditText)findViewById(R.id.emailField);
        passwordText = (EditText)findViewById(R.id.passwordField);
        signinButton = (Button)findViewById(R.id.button);
        signupButton = (Button)findViewById(R.id.button3);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TESTING", "Reached the onDataChange in ValueEventListener");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    User u = ds.getValue(User.class);
                    userList.add(u);
                    Log.d("TESTING", u.name + " / " + u.password  + " / " + u.email + " / " + u.uID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void Login(User user) {
        if(user != null) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }

    public void ValidateLogin(View v) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        for (User u : userList) {
            if (u.email.equals(email) && u.password.equals(password)) {
                currentUser = u;

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Successful login",
                        Toast.LENGTH_SHORT);

                toast.show();

                Login(u);
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Login failed",
                        Toast.LENGTH_SHORT);

                toast.show();
            }
        }
    }

    public void signUp(View v){
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }
}
