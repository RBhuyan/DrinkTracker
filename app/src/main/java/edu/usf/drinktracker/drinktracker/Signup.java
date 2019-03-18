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
import android.widget.RadioButton;
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
    Button registerButton, back_to_login;
    EditText addressData, weightData;
    RadioButton male, female;
    private String userId;
    FirebaseDatabase mFirebaseInstance;
    DatabaseReference mFirebaseDatabase;
    FirebaseAuth auth;
    String genderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

       /* Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        final String address = extras.getString("address");
        String weightTxt = extras.getString("weight");
        final String isMaleString = extras.getString("isMaleChecked");
        final String gender = isMaleString.equals("True") ? "Male" : "Female";
        final int weight = Integer.parseInt(weightTxt);*/
       // all intent data from previous activity not needed because it is all in this one activity now

        addressData = ((EditText) findViewById(R.id.address_text));
        weightData =((EditText) findViewById(R.id.weight_text));

        male = (RadioButton) findViewById(R.id.male_text);
        female = (RadioButton) findViewById(R.id.female_text);
        male.setChecked(true);
        nameData = (EditText)findViewById(R.id.nameField);
        passwordData = (EditText)findViewById(R.id.passwordField);
        emailData = (EditText)findViewById(R.id.emailField);
        registerButton = (Button)findViewById(R.id.registerButton);
        back_to_login =(Button)findViewById(R.id.back_to_login);


        male.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                female.setChecked(false);
            }
        });

        female.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                male.setChecked(false);
            }
        });


        //final int weight = Integer.parseInt(weightTxt);
        if ( male.isChecked() == true )
            genderData = "male";
        else
            genderData = "female";

        auth = FirebaseAuth.getInstance();

        //This segment of code sets up the database insertion
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameData.getText().toString().trim();
                final String email = emailData.getText().toString().trim();
                final String password = passwordData.getText().toString().trim();
                final String gender = genderData;
                final String address = addressData.getText().toString().trim();
                final String weightTxt = weightData.getText().toString();
                final int weight = Integer.parseInt(weightTxt);

                //this is not working fun fact :)
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
                if(TextUtils.isEmpty(weightTxt)){
                    Toast.makeText(getApplicationContext(), "Enter weight!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(getApplicationContext(), "Enter address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Segment below is responsible for USER DATABASE CREATION

                //if (TextUtils.isEmpty(userId)) {
                    //userId = mFirebaseDatabase.push().getKey();
                //}
                final User user = new User(name, password, email, weight, address, gender, 0, "False");

                // mFirebaseDatabase.child(userId).setValue(user);
                //mFirebaseDatabase.child(email).setValue(user);

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
                                    String authID = auth.getCurrentUser().getUid();
                                    mFirebaseDatabase.child(authID).setValue(user);
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

