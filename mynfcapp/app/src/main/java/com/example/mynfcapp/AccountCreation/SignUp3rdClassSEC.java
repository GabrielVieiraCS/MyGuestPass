package com.example.mynfcapp.AccountCreation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class SignUp3rdClassSEC extends Activity {

    //Variables
    ImageView backBtn;
    Button next, login;
    TextView titleText;
    TextInputLayout reg_phoneNo;
    ScrollView scrollView;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up3rd_class);

        //Hooks
        backBtn = findViewById(R.id.signup_back_btn);
        next = findViewById(R.id.signup_next_btn);
        login = findViewById(R.id.signup_login_btn);
        titleText = findViewById(R.id.signup_title_text);

        scrollView = findViewById(R.id.signup_3rd_screen_scroll_view);
        reg_phoneNo = findViewById(R.id.phoneNo);
        countryCodePicker = findViewById(R.id.country_code_picker);
    }

    public void callVerifyOTP(View view) {
        if (!validatePhoneNumber()) return;

        String __fullName = getIntent().getStringExtra("fullName");
        String __email = getIntent().getStringExtra("email");
        String __password = getIntent().getStringExtra("password");
        String __gender = getIntent().getStringExtra("gender");
        String __date = getIntent().getStringExtra("date");

        String _getUserEnteredPhoneNumber = reg_phoneNo.getEditText().getText().toString().trim(); //Gets User Phone Number
        String __phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnteredPhoneNumber;

        Intent intent = new Intent(getApplicationContext(), VerifyOTPSEC.class);

        intent.putExtra("fullName", __fullName);
        intent.putExtra("email", __email);
        intent.putExtra("password", __password);
        intent.putExtra("gender", __gender);
        intent.putExtra("date", __date);
        intent.putExtra("phoneNo", __phoneNo);


        //Add Transition
        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_button");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClassSEC.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    public void callPrevSignupScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClassSEC.class);

        //Add Transition
        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_button");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rdClassSEC.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }

    /**
     * Validation Method
     */
    private boolean validatePhoneNumber() {
        String val = reg_phoneNo.getEditText().getText().toString().trim();
        //String checkspaces = "Aw{1,20}z";
        String checkspaces = "^[^\\s].+[^\\s]$";
        if (val.isEmpty()) {
            reg_phoneNo.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            reg_phoneNo.setError("No White spaces are allowed! " + val );
            return false;
        } else {
            reg_phoneNo.setError(null);
            reg_phoneNo.setErrorEnabled(false);
            return true;
        }
    }

}