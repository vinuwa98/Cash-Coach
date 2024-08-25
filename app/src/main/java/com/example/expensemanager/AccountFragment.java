package com.example.expensemanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;  // Firebase Authentication instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_account2, container, false);

        // Initialize EditText fields from layout
        EditText emailUser = myView.findViewById(R.id.email_account);
        EditText dateofCreation = myView.findViewById(R.id.dateofCreation);
        EditText timeOfCreation = myView.findViewById(R.id.timeOfCreation);
        EditText signInAt = myView.findViewById(R.id.lastSignInAt);

        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Authentication instance
        FirebaseUser user = mAuth.getCurrentUser();  // Get current user from Firebase

        if (user != null) {
            // Set user email in the corresponding EditText
            emailUser.setText(user.getEmail());

            // Get user creation timestamp and format it to date
            Long timestampCreate = user.getMetadata().getCreationTimestamp();
            Date date1 = new Date(timestampCreate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String formattedDate = dateFormat.format(date1);
            dateofCreation.setText(formattedDate);

            // Format creation timestamp to time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss z");
            String formattedTime = timeFormat.format(date1);
            timeOfCreation.setText(formattedTime);

            // Get last sign-in timestamp and format it to date and time
            Long lastSignInTS = user.getMetadata().getLastSignInTimestamp();
            Date date2 = new Date(lastSignInTS);
            SimpleDateFormat signInFormat = new SimpleDateFormat("dd MMM yyyy    HH:mm:ss z");
            String formattedSignIn = signInFormat.format(date2);
            signInAt.setText(formattedSignIn);
        }

        return myView;  // Return the inflated view
    }
}
