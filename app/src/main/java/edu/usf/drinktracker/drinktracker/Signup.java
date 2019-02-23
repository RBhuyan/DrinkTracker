package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class Signup extends AppCompatActivity {
    private static final String TAG = "Signup";
    EditText nameData;
    EditText passwordData;
    EditText emailData;
    Button registerButton;
    private String userId;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Intent intent = getIntent();
        nameData = (EditText)findViewById(R.id.nameField);
        passwordData = (EditText)findViewById(R.id.passwordField);
        emailData = (EditText)findViewById(R.id.emailField);
        registerButton = (Button)findViewById(R.id.registerButton);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        // Save / update the user
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameData.getText().toString();
                String password = passwordData.getText().toString();
                String email = emailData.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Please fill in the required fields",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if existing user is present

                createUser(name, password, email);
            }
        });


    }

    private void returnToLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, String password, String email) {

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        User user = new User(name, password, email, userId);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }


    //Don't think we use this?
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email);

                // Display newly updated name and email
                //txtDetails.setText(user.name + ", " + user.email);

                // clear edit text
                emailData.setText("");
                nameData.setText("");
                passwordData.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}

