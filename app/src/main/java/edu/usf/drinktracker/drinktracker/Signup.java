//Allows user to sign up
//In the firebase cloud manager there are 2 different categories, authentication and our actual database
//Authentication handles user login, signout, etc. The database contains our actual user information
//Because these are 2 different things we must create a user authentication AND a user account in our database on a register
package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class Signup extends AppCompatActivity {
    private static final String TAG = "Signup";
    EditText nameData, passwordData, emailData;
    Button registerButton;
    private String userId;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String address = extras.getString("address");
        String weight = extras.getString("weight");
        String isMaleString = extras.getString("isMaleChecked");
        String gender = isMaleString.equals("True") ? "Male" : "Female";


        nameData = (EditText)findViewById(R.id.nameField);
        passwordData = (EditText)findViewById(R.id.passwordField);
        emailData = (EditText)findViewById(R.id.emailField);
        registerButton = (Button)findViewById(R.id.registerButton);

        auth = FirebaseAuth.getInstance();

        //This segment of code sets up the database insertion
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameData.getText().toString().trim();
                String email = emailData.getText().toString().trim();
                String password = passwordData.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Segment below is responsible for USER DATABASE CREATION

                if (TextUtils.isEmpty(userId)) {
                    userId = mFirebaseDatabase.push().getKey();
                }
                User user = new User(name, password, email, userId);
                mFirebaseDatabase.child(userId).setValue(user);
                //addUserChangeListener();

                //Segment below is responsible for USER AUTHENTICATION CREATION
                //creates the user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Signup.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Signup.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(Signup.this, Home.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Adds a value event listener to the firebase realtime database
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            //Triggers when data changes in the database
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

