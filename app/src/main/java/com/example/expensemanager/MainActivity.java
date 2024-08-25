package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private EditText mEmail;              // EditText for entering email
    private EditText mPassword;           // EditText for entering password
    private Button loginButton;           // Button for login
    private TextView mForgotPassword;     // TextView for forgot password
    private TextView mSignup;             // TextView for signup

    // Progress dialog for showing login process
    private ProgressDialog mDialog;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set the layout for the activity

        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Authentication instance

        // Check if user is already logged in, redirect to HomeActivity if true
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();  // Finish current activity
        }

        mDialog = new ProgressDialog(this);  // Initialize progress dialog
        login();  // Call method to set up login functionality
    }

    // Method to set up login functionality
    private void login(){
        // Initialize UI elements from layout
        mEmail = findViewById(R.id.email_login);
        mPassword = findViewById(R.id.password_login);
        loginButton = findViewById(R.id.btn_login);
        mForgotPassword = findViewById(R.id.forgot_password);
        mSignup = findViewById(R.id.signup);

        // Set onClickListener for loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // Validate email input
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty. Please enter a valid Email id");
                    return;
                }

                // Validate password input
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty.");
                    return;
                }

                mDialog.setMessage("Processing");  // Set message for progress dialog
                mDialog.show();  // Show progress dialog

                // Firebase Authentication to sign in user with email and password
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mDialog.dismiss();  // Dismiss progress dialog
                            Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_SHORT).show();  // Show success message
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));  // Redirect to HomeActivity
                            finish();  // Finish current activity
                        } else {
                            mDialog.dismiss();  // Dismiss progress dialog
                            Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();  // Show failure message
                        }
                    }
                });
            }
        });

        // Set onClickListener for mSignup TextView
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));  // Redirect to RegistrationActivity
            }
        });

        // Set onClickListener for mForgotPassword TextView
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));  // Redirect to ResetPasswordActivity
            }


        });
    }
}
