package com.example.mynfcapp.AccountCreation;

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
import android.view.View;

import com.example.mynfcapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class SetNewPassword extends Activity {

    //Variables
    TextInputLayout new_password, confirm_new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        //Hooks
        new_password = findViewById(R.id.new_password);
        confirm_new_password = findViewById(R.id.confirm_new_password);
    }

    /*
    Validation Methods
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected(SetNewPassword setNewPassword) {
        ConnectivityManager connectivityManager = (ConnectivityManager) setNewPassword.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
            return true;
        } else return false;
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetNewPassword.this);
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

    private boolean validatePassword() {
        String val = new_password.getEditText().getText().toString().trim();
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
            new_password.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkPassword)){
            new_password.setError("Password Should Contain 4 Characters!");
            return false;
        } else {
            new_password.setError(null);
            new_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String val = confirm_new_password.getEditText().getText().toString().trim();
        String val2 = new_password.getEditText().getText().toString().trim();
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
            confirm_new_password.setError("Field cannot be empty!");
            return false;
        } else if (!val.matches(checkPassword)){
            confirm_new_password.setError("Password Should Contain 4 Characters!");
            return false;
        } else if (!val.equals(val2)) {
            confirm_new_password.setError("Passwords should match!");
            return false;
        } else {
            confirm_new_password.setError(null);
            confirm_new_password.setErrorEnabled(false);
            return true;
        }
    }

    /*
    Call Next Activities
     */


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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setNewPassword(View view) {
        //Check User Internet Connection
        if (!isConnected(SetNewPassword.this)) {
            showCustomDialog();
        }

        //Validate Phone Number
        if (!validatePassword() | !validateConfirmPassword()) {
            return;
        }

        //Get User Data from fields
        String _newPassword = new_password.getEditText().getText().toString().trim();
        String _phoneNumber = getIntent().getStringExtra("phoneNo");

        //Update Data in Firebase and in Sessions
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(_phoneNumber).child("password").setValue(_newPassword);

        startActivity(new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class));
        finish();
    }
}