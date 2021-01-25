package com.example.mynfcapp.AccountCreation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.mynfcapp.R;

public class ForgetPasswordSuccessMessage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password_success_message);
    }

    public void callPrevScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(ForgetPasswordSuccessMessage.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }
    public void callLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(ForgetPasswordSuccessMessage.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

}