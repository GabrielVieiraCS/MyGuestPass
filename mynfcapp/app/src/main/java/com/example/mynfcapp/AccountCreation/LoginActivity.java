package com.example.mynfcapp.AccountCreation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynfcapp.AccountCreation.Database.SessionManager;
import com.example.mynfcapp.Dashboard.Dashboard;
import com.example.mynfcapp.ReaderActivity;
import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class LoginActivity extends Activity {

    //Variables
    CountryCodePicker countryCodePicker;
    Button callSignUp, login_btn;
    ImageView image;
    TextView Welcome, sloganText;
    TextInputLayout phoneNumber, password;
    TextInputEditText phoneNumberEditText, passwordEditText;
    RelativeLayout progressbar;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Hooks
        callSignUp = findViewById(R.id.signup_screen_btn);
        countryCodePicker = findViewById(R.id.login_country_code_picker);
        phoneNumber = findViewById(R.id.login_phoneNo);
        password = findViewById(R.id.login_password);
        progressbar = findViewById(R.id.login_progress_bar);
        rememberMe = findViewById(R.id.remember_me);
        phoneNumberEditText = findViewById(R.id.login_phoneNo_editText);
        passwordEditText = findViewById(R.id.login_password_editText);
        //image = findViewById(R.id.logoImage);
        //Welcome = findViewById(R.id.logo_name);

        //Check whether phoneNo & Passwprd is saved in shared preferences
        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            phoneNumberEditText.setText(rememberMeDetails.get(sessionManager.KEY_SESSIONPHONENUMBER));
            passwordEditText.setText(rememberMeDetails.get(sessionManager.KEY_SESSIONPASSWORD));
        }


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

    /*
    LOGS THE USER INTO THE APPLICATION
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void userLogin(View view) {
        //Check if User is connected to the Internet
        if (!isConnected(this)) {
            showCustomDialog();
        }

        //Method to validate login credentials
        if (!validateFields()) {
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        //Get User Credentials
        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();

        //Use Case handle of user input 0
        if (_phoneNumber.charAt(0) == '0') {
            _phoneNumber = _phoneNumber.substring(1);
        }
        String _completePhoneNumber = "+" + countryCodePicker.getFullNumber() + _phoneNumber;
        //Check REMEMBERME TICK
        if (rememberMe.isChecked()) {
            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(_phoneNumber, _password);
        }


        //Database Validation
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);

                    //Get Password From User Snapshot
                    String systemPassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                    if (systemPassword.equals(_password)) {
                        password.setError(null);
                        phoneNumber.setErrorEnabled(false);

                        String _fullName = snapshot.child(_completePhoneNumber).child("fullName").getValue(String.class);
                        String _email = snapshot.child(_completePhoneNumber).child("email").getValue(String.class);
                        String _phoneNo = snapshot.child(_completePhoneNumber).child("phoneNo").getValue(String.class);
                        String _password = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                        String _date = snapshot.child(_completePhoneNumber).child("date").getValue(String.class);
                        String _gender = snapshot.child(_completePhoneNumber).child("gender").getValue(String.class);


                        //CREATE A SESSION
                        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(_fullName, _email, _phoneNo, _password, _date, _gender);


                        Toast.makeText(LoginActivity.this, _fullName + "\n" + _email + "\n" + _phoneNo + "\n" + _date, Toast.LENGTH_SHORT).show();
                        //progressbar.setVisibility(View.GONE);
                        // ADD NEXT ACTIVITY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    } else {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Wrong Data!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else return false;
    }

    /**
     Validation Methods
     **/

    private boolean validateFields() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        //String checkspaces = "Aw{1,20}z";
        String checkspaces = "^[^\\s].+[^\\s]$";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNumber.setError("No White spaces are allowed! " + val);
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    /**
     Check Internet Conenction Status
     **/

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please have an active Internet Connection to continue")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), StartUpScreenActivity.class));
                    }
                });

    }

    /*
    Call Next Activities
     */

    public void callRegisterScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
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