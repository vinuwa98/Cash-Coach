package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MapActivity extends AppCompatActivity {

    EditText start;
    EditText end;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize UI components
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        searchBtn = findViewById(R.id.Searchbtn);

        // Set click listener for the search button
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startingPoint = start.getText().toString();
                String endPoint = end.getText().toString();

                // Check if both starting point and end point are provided
                if (startingPoint.isEmpty() || endPoint.isEmpty()) {
                    Toast.makeText(MapActivity.this, "Please enter starting point and destination", Toast.LENGTH_SHORT).show();
                } else {
                    // Call method to open Google Maps with the specified path
                    getPath(startingPoint, endPoint);
                }
            }
        });
    }

    private void getPath(String startingPoint, String endPoint) {
        try {
            // Encode starting point and end point to handle special characters in URLs
            String encodedStartingPoint = Uri.encode(startingPoint);
            String encodedEndPoint = Uri.encode(endPoint);

            // Construct the URI for Google Maps directions using the encoded starting and end points
            Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + encodedStartingPoint + "&destination=" + encodedEndPoint);
            Log.d("URI", uri.toString());

            // Create an intent to open Google Maps with the specified URI
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps"); // Specify package to open Google Maps
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the task to start a new instance
            startActivity(intent); // Start the intent to open Google Maps
        } catch (ActivityNotFoundException e) {
            // If Google Maps app is not installed, open Google Play Store to install it
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the task to start a new instance
            startActivity(intent); // Start the intent to open Google Play Store
        }
    }
}
