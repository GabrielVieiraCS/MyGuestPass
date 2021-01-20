package com.example.mynfcapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {

    //Variables
    Button accountAlreadyBtn, registerBtn;
    TextInputLayout registerName, registerEmail, registerPhoneNo, registerPassword;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Hooks to all xml elements in activity_sign_up.xml
        accountAlreadyBtn = findViewById(R.id.account_already_btn);
        registerBtn = findViewById(R.id.register_button);
        // Registration elements
        registerName = findViewById(R.id.regName);
        registerEmail = findViewById(R.id.regEmail);
        registerPhoneNo = findViewById(R.id.regPhoneNo);
        registerPassword = findViewById(R.id.regPassword);

        // Button to bring user back to LoginActivity
        accountAlreadyBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this);
                startActivity(intent, options.toBundle());

            }
        });

        //Save data to FireBase on button click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
               rootNode = FirebaseDatabase.getInstance();
               reference = rootNode.getReference("users");

               //Get all the values from the textField
                String name = registerName.getEditText().getText().toString();
                String email= registerEmail.getEditText().getText().toString();
                String phoneNo = registerPhoneNo.getEditText().getText().toString();
                String password = registerPassword.getEditText().getText().toString();

               UserHelperClass helperClass = new UserHelperClass(name, email, phoneNo, password);

               reference.setValue(helperClass);

            }
        }); //Register Button method end
    }
}