package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Set the layout for this activity
        setContentView(R.layout.activity_reset_password);

        // Connect views from layout to variables
        EditText mailText = findViewById(R.id.forgot_password_email);
        Button sendEmail_btn = findViewById(R.id.btn_reset_password);

        // Set OnClickListener for sendEmail_btn to handle password reset process
        sendEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get email entered by user
                String email = mailText.getText().toString().trim();

                // Send password reset email using Firebase Authentication
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // On success, display toast message and navigate back to MainActivity
                        Toast.makeText(getApplicationContext(), "Please Check your mail for Password reset Instructions", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // On failure, display toast message with error and navigate back to MainActivity
                        Toast.makeText(getApplicationContext(), "Error! Reset Link is not sent!. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            }
        });
    }
}
