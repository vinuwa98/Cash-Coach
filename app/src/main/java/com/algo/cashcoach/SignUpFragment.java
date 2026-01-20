package com.algo.cashcoach;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseNetworkException;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etUsername = view.findViewById(R.id.signup_username);
        etEmail = view.findViewById(R.id.signup_email);
        etPassword = view.findViewById(R.id.signup_password);
        etConfirmPassword = view.findViewById(R.id.signup_confirm_password);
        btnRegister = view.findViewById(R.id.btn_signup_confirm);
        tvGoToLogin = view.findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if(user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(user, email, pass, view);
        });

        tvGoToLogin.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_signup_to_login);
        });

        return view;
    }

    private void registerUser(String username, String email, String password, View view) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();
                        saveUserToFirestore(uid, username, email, view);
                    } else {
                        // USE THE NEW HELPER HERE
                        String friendlyError = getFriendlyErrorMessage(task.getException());
                        Toast.makeText(getActivity(), friendlyError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getFriendlyErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            return "This email is already registered.";
        } else if (e instanceof FirebaseAuthWeakPasswordException) {
            return "Password is too weak. Please use at least 6 characters.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Invalid email format.";
        } else if (e instanceof FirebaseNetworkException) {
            return "No Internet Connection.";
        } else {
            return "Registration Failed. Please try again.";
        }
    }

    private void saveUserToFirestore(String uid, String username, String email, View view) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);

        firestore.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Account Created!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_signup_to_login);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}