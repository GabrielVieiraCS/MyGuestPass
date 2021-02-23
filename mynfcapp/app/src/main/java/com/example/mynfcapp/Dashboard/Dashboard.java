package com.example.mynfcapp.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mynfcapp.AccountCreation.Database.SessionManager;
import com.example.mynfcapp.AccountCreation.LoginActivity;
import com.example.mynfcapp.R;
import com.example.mynfcapp.ReaderActivity;
import com.example.mynfcapp.Test;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.HashMap;

public class Dashboard extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //User Session Details
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();

        String fullName = userDetails.get(SessionManager.KEY_FULLNAME);
        String email = userDetails.get(SessionManager.KEY_EMAIL);
        String phoneNo = userDetails.get(SessionManager.KEY_PHONENUMNER);
        String password = userDetails.get(SessionManager.KEY_PASSWORD);
        String date = userDetails.get(SessionManager.KEY_DATE);
        String gender = userDetails.get(SessionManager.KEY_GENDER);

        //Hooks
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        bottomMenu();


        
    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch(i) {
                    case R.id.bottom_nav_dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.bottom_nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

    }

    public void callReaderActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ReaderActivity.class);
        startActivity(intent);
    }
    public void logoutUser(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }



}