package com.example.mynfcapp.AccountCreation;

import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {

    //Variables
    ImageView backBtn;
    Button next, login;
    TextView titleText;


    //User Data Variables
    TextInputLayout reg_fullName, reg_email, reg_password;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Hooks
        backBtn = findViewById(R.id.signup_back_btn);
        next = findViewById(R.id.signup_next_btn);
        login = findViewById(R.id.signup_login_btn);
        titleText = findViewById(R.id.signup_title_text);

        //Hooks for getting User Data
        reg_fullName = findViewById(R.id.signup_fullname);
        reg_email = findViewById(R.id.signup_email);
        reg_password = findViewById(R.id.signup_password);


    }

    //Next Screen
    public void callNextSignupScreen(View view) {

        //Validation Check
        if (!validateFullName() | !validateEmail() | validatePassword()) {
            return;
        }
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        //Store & Pass User Details
        String fullName = reg_fullName.getEditText().getText().toString();
        String email = reg_email.getEditText().getText().toString();
        String password = reg_password.getEditText().getText().toString();

        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);

        //Add Transition
        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_button");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    public void callPrevSignupScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), StartUpScreenActivity.class);

        //Add Transition
        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_button");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    /*
    VALIDATION FUNCTIONS
     */

    private boolean validateFullName() {
        String val = reg_fullName.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            reg_fullName.setError("Field cannot be empty!");
            return false;
        } else {
            reg_fullName.setError(null);
            reg_fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = reg_email.getEditText().getText().toString().trim();
        String checkEmailSpaces = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            reg_email.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkEmailSpaces)){
            reg_email.setError("Invalid Email");
            return false;
        } else {
            reg_email.setError(null);
            reg_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = reg_password.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            reg_password.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkPassword)){
            reg_password.setError("Password Should Contain 4 Characters!");
            return false;
        } else {
            reg_password.setError(null);
            reg_password.setErrorEnabled(false);
            return true;
        }
    }


}


/**
 * {
 * "rules": {
 * ".read": "now < 1613779200000",  // 2021-2-20
 * ".write": "now < 1613779200000",  // 2021-2-20
 * }
 * }
 **/