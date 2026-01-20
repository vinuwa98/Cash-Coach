package com.algo.cashcoach;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private TextView tvBalance, tvTitle;
    private View btnAdd, btnHistory, btnLoans, btnReports;
    private Button btnLogout;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // UI elements
        tvBalance = view.findViewById(R.id.tv_total_balance);
        tvTitle = view.findViewById(R.id.tv_dashboard_title);

        btnAdd = view.findViewById(R.id.btn_add_transaction_layout);
        btnHistory = view.findViewById(R.id.btn_history_layout);
        btnLoans = view.findViewById(R.id.btn_loans_layout);
        btnReports = view.findViewById(R.id.btn_reports_layout);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Username
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            if (username != null && !username.isEmpty()) {
                tvTitle.setText("Hello, " + username + "!");
            }
        }

        // Navigation
        btnAdd.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_dashboard_to_addTransaction));
        btnHistory.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_dashboard_to_history));
        btnLoans.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_dashboard_to_loans));
        btnReports.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_dashboard_to_reports));

        // Logout Logic
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        // Sign out from Firebase
                        mAuth.signOut();
                        // Go to Login Screen
                        Navigation.findNavController(view).navigate(R.id.action_dashboard_to_login);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh balance
        calculateBalance();
    }

    private void calculateBalance() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Get all transactions
        firestore.collection("users").document(user.getUid()).collection("transactions")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalBalance = 0.0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Double amount = doc.getDouble("amount");
                        String type = doc.getString("type");

                        if (amount != null && type != null) {
                            if (type.equalsIgnoreCase("Income")) {
                                totalBalance += amount;
                            } else if (type.equalsIgnoreCase("Expense")) {
                                totalBalance -= amount;
                            }
                        }
                    }

                    // Update UI
                    tvBalance.setText(String.format(Locale.getDefault(), "LKR %.2f", totalBalance));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error calculating balance", Toast.LENGTH_SHORT).show();
                });
    }
}