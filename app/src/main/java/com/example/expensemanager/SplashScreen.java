package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for the splash screen activity
        setContentView(R.layout.activity_splash_screen);

        // Uncomment the line below to hide the action bar (if needed)
        // getSupportActionBar().hide();

        // Use Handler to delay the start of MainActivity by 3 seconds
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Create an Intent to start MainActivity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                // Finish the current activity (SplashScreen)
                finish();
            }
        }, 3000); // Delay time in milliseconds (3 seconds in this case)
    }
}
