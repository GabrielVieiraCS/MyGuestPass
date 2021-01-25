package com.example.mynfcapp.AccountCreation;
import com.example.mynfcapp.R;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

public class ForgotPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }


    public void callNextScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), MakeSelection.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(ForgotPassword.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void callPrevScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);



        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(ForgotPassword.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }
}