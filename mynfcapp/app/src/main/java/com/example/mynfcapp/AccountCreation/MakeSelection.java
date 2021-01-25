package com.example.mynfcapp.AccountCreation;
import com.example.mynfcapp.R;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MakeSelection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_selection);
    }

    public void callPrevScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);



        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.makeSceneTransitionAnimation(MakeSelection.this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }
}