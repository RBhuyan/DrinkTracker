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

import edu.usf.drinktracker.drinktracker.AnalyticsFragment;
import edu.usf.drinktracker.drinktracker.Drink;
import edu.usf.drinktracker.drinktracker.DrinkSessionFragment;
import edu.usf.drinktracker.drinktracker.LogHistoryFragment;
import edu.usf.drinktracker.drinktracker.Login;
import edu.usf.drinktracker.drinktracker.R;

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
                    selectedFragment = DrinkSessionFragment.newInstance();
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