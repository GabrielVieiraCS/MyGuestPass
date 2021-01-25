package com.example.mynfcapp.AccountCreation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mynfcapp.R;

public class SetNewPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
    }

    public void callPrevScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(SetNewPassword.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }
}