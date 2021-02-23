package com.example.mynfcapp.Dashboard;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mynfcapp.AccountCreation.LoginActivity;
import com.example.mynfcapp.AccountCreation.SignUpActivity;
import com.example.mynfcapp.AccountCreation.StartUpScreenActivity;
import com.example.mynfcapp.R;
import com.example.mynfcapp.ReaderActivity;


public class DashboardFragmentSEC extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_s_e_c, container, false);
    }



}