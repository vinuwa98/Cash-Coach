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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddLoanFragment extends Fragment {

    private EditText etDesc, etAmount;
    private Button btnSave;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_loan, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etDesc = view.findViewById(R.id.et_loan_desc);
        etAmount = view.findViewById(R.id.et_loan_amount);
        btnSave = view.findViewById(R.id.btn_save_loan);

        btnSave.setOnClickListener(v -> {
            String desc = etDesc.getText().toString();
            String amountStr = etAmount.getText().toString();

            if (desc.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            saveLoanToFirestore(desc, amount, view);
        });

        return view;
    }

    private void saveLoanToFirestore(String desc, double amount, View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        btnSave.setEnabled(false);

        Map<String, Object> loanMap = new HashMap<>();
        loanMap.put("description", desc);
        loanMap.put("amount", amount);
        loanMap.put("status", "Pending");

        firestore.collection("users").document(user.getUid()).collection("loans")
                .add(loanMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Loan Added!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigateUp();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}