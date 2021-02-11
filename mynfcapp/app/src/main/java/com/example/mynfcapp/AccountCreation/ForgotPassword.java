package com.example.mynfcapp.AccountCreation;
import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

public class ForgotPassword extends Activity {

    //Variables
    private CountryCodePicker countryCodePicker;
    private TextInputLayout phoneNumberTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        //Hooks
        countryCodePicker = findViewById(R.id.forgotPassword_country_code_picker);
        phoneNumberTextField = findViewById(R.id.forgotPassword_phoneNo);
    }


    /*
    Call the OTP Screen for number verification
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void verifyPhoneNumber(View view) {

        //Check User Internet Connection
        if (!isConnected(ForgotPassword.this)) {
            showCustomDialog();
        }

        //Validate Phone Number
        if (!validateFields()) {
            return;
        }

        //Data from fields
        String _getUserEnteredPhoneNumber = phoneNumberTextField.getEditText().getText().toString().trim(); //Gets User Phone Number
        String _phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnteredPhoneNumber;

        if (_phoneNo.charAt(0) == '0') {
            _phoneNo = _phoneNo.substring(1);
        }
        final String _completePhoneNumber = "+" + countryCodePicker.getFullNumber() + _phoneNo;

        //Check if User exists in Database
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If Number exists -> Call OTP to verify
                if (snapshot.exists()) {
                    phoneNumberTextField.setError(null);
                    phoneNumberTextField.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                    intent.putExtra("phoneNo", _completePhoneNumber);
                    intent.putExtra("whatToDO", "updateData");
                    startActivity(intent);
                    finish();;
                } else {
                    phoneNumberTextField.setError("No such user exists!");
                    phoneNumberTextField.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
     Validation Methods
     */

    private boolean validateFields() {
        String val = phoneNumberTextField.getEditText().getText().toString().trim();
        //String checkspaces = "Aw{1,20}z";
        String checkspaces = "^[^\\s].+[^\\s]$";
        if (val.isEmpty()) {
            phoneNumberTextField.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNumberTextField.setError("No White spaces are allowed! " + val);
            return false;
        } else {
            phoneNumberTextField.setError(null);
            phoneNumberTextField.setErrorEnabled(false);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected(ForgotPassword forgotPassword) {
        ConnectivityManager connectivityManager = (ConnectivityManager) forgotPassword.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else return false;
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
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