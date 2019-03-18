/*
Our Home class that is shown after the user logs in

The BottomNavigationView is the bottom bar in the home screen.

Basically the Home screen is just that bottom navigation bar and the rest of the app screen is a big content view

The content view is one of the three fragments I've defined in the project (DrinkSession, LogHistory, Analytics)

Content View by default is set to DrinkSession. This means if we tap the LogHistory icon on the bottom navigation bar,
the current content view will be replaced by the LogHistory fragment view

This is a little annoying because to access data in the DrinkSessionFragment for example, we are no longer accessing an activity
(as the base activity is Home), but instead we must access the DrinkSession Fragment of Home.

You can think of that as not accessing an object but instead accessing an attribute of an object that can only be
reached through an accessor method, in this case the accessor method being a fragment call.
*/
package edu.usf.drinktracker.drinktracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Home extends AppCompatActivity {
    public static ArrayList<Drink> drinkList;
    private TextView mTextMessage;
    android.support.v7.widget.Toolbar toolbar;
    Drink drink;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    FirebaseUser user;
    String userID;
    public static String homeInSession;
    int sessionNumber;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment selectedFragment = null;
        //Keystore password is DTpassword
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(userID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if(map.get("SessionNumber") == null)
                                        sessionNumber = 0;
                                    else
                                        sessionNumber = (((Long) map.get("SessionNumber")).intValue());
                                    if(map.get("InSession") == null)
                                        homeInSession = "false";
                                    else
                                        homeInSession = map.get("InSession").equals("True") ? "True" : "False";

                                    selectedFragment = DrinkSessionFragment.newInstance();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                    //selectedFragment = DrinkSessionFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.second_nav_option);
                    //return true;
                    selectedFragment = AnalyticsFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.third_nav_option);
                    //return true;
                    selectedFragment = LogHistoryFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra("drink")) {
            drink = (Drink) intent.getSerializableExtra("drink");
        }

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Home.this, Login.class));
                    finish();
                }
            }
        };

        auth = FirebaseAuth.getInstance();
        userID = auth.getUid();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, DrinkSessionFragment.newInstance());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_options, menu);
        return true;
    }

    //Overrides app action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.sign_out:
                if (auth.getCurrentUser() != null) {
                    auth.signOut();
                }
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Intent intent = new Intent(Home.this, UserSettings.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public Drink getDrink(){
        return drink;
    }
}
