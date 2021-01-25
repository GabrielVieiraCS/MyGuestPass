package com.example.mynfcapp.AccountCreation;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends Activity {

    Button callSignUp, login_btn;
    ImageView image;
    TextView Welcome, sloganText;
    TextInputLayout emailadd, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // Anim Hooks
        callSignUp = findViewById(R.id.signup_screen_btn);
        //image = findViewById(R.id.logoImage);
        //Welcome = findViewById(R.id.logo_name);


        callSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

                //Pair[] pairs = new Pair[2];

                //pairs[0] = new Pair<View, String>(image, "logo_image");
               // pairs[1] = new Pair<View, String>(Welcome, "logo_text");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
                startActivity(intent, options.toBundle());

            }
        });
    }

    public void callForgotPasswordScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void callPrevScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), StartUpScreenActivity.class);


        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }
}