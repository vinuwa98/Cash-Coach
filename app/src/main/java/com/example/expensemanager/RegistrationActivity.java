package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button signupButton;
    private TextView mLogin;

    private ProgressDialog mDialog;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize ProgressDialog for showing loading indicator
        mDialog = new ProgressDialog(this);

        // Method to set up signup functionality
        signup();
    }

    // Method to set up signup functionality
    private void signup(){
        // Connect layout views to variables
        mEmail=findViewById(R.id.email_signup);
        mPassword=findViewById(R.id.password_signup);
        signupButton=findViewById(R.id.btn_signup);
        mLogin=findViewById(R.id.login);

        // Set OnClickListener for signupButton to handle signup process
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get email and password input
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                // Validate email and password fields
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty. Please enter a valid Email id");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty.");
                    return;
                }

                // Log email and password for debugging purposes
                Log.i("val", email);
                Log.i("val", password);

                // Show ProgressDialog while processing signup request
                mDialog.setMessage("Please wait while we process your data");
                mDialog.show();

                // Attempt to create a new user with email and password using Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Dismiss ProgressDialog when task completes
                        mDialog.dismiss();

                        // Check if signup was successful
                        if(task.isSuccessful()){
                            // Display toast message and navigate to HomeActivity on success
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else{
                            // Display toast message if signup failed
                            Toast.makeText(getApplicationContext(), "Registration Failed!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Set OnClickListener for mLogin TextView to navigate to login screen
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
