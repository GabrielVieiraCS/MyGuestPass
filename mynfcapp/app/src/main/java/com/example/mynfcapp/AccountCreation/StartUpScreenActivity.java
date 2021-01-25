package com.example.mynfcapp.AccountCreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.mynfcapp.R;

public class StartUpScreenActivity extends Activity {

    Button callRegisterBtn, callLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_up_screen);

        //Hooks
        callRegisterBtn = findViewById(R.id.callRegisterBtn);
        callLoginButton = findViewById(R.id.callLoginBtn);

    }

    public void callLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void callRegisterScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}