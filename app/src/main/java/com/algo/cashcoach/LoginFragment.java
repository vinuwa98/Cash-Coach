package com.algo.cashcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseNetworkException;

public class LoginFragment extends Fragment {

    private EditText etEmailInput, etPassword;
    private Button btnLogin, btnGoToSignup;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etEmailInput = view.findViewById(R.id.login_username);
        etPassword = view.findViewById(R.id.login_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnGoToSignup = view.findViewById(R.id.btn_go_to_signup);

        btnLogin.setOnClickListener(v -> {
            String email = etEmailInput.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter Email and Password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password, view);
        });

        btnGoToSignup.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_login_to_signup);
        });

        return view;
    }

    private void loginUser(String email, String password, View view) {
        btnLogin.setEnabled(false);
        Toast.makeText(getActivity(), "Verifying...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        fetchUserProfile(uid, view);
                    } else {
                        btnLogin.setEnabled(true);
                        // USE THE NEW HELPER HERE
                        String friendlyError = getFriendlyErrorMessage(task.getException());
                        Toast.makeText(getActivity(), friendlyError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getFriendlyErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "Account not found. Please Sign Up first.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Incorrect Email or Password.";
        } else if (e instanceof FirebaseNetworkException) {
            return "No Internet Connection.";
        } else {
            return "Login Failed. Please try again.";
        }
    }

    private void fetchUserProfile(String uid, View view) {
        firestore.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String username = "User";

                        if (document.exists() && document.contains("username")) {
                            username = document.getString("username");
                        }

                        Toast.makeText(getActivity(), "Welcome back!", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        Navigation.findNavController(view).navigate(R.id.action_login_to_dashboard, bundle);
                    } else {
                        Navigation.findNavController(view).navigate(R.id.action_login_to_dashboard);
                    }
                });
    }
}