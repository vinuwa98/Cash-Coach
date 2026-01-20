package com.algo.cashcoach;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionFragment extends Fragment {

    private EditText etAmount, etNote;
    private Spinner spinnerCategory;
    private RadioButton rbExpense;
    private Button btnSave;
    private TextView tvDatePicker;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        etAmount = view.findViewById(R.id.et_amount);
        etNote = view.findViewById(R.id.et_note);
        rbExpense = view.findViewById(R.id.rb_expense);
        btnSave = view.findViewById(R.id.btn_save_transaction);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        tvDatePicker = view.findViewById(R.id.tv_date_picker);

        // 1. SETUP SPINNER
        String[] categories = {"Food", "Transport", "Rent", "Bills", "Health", "Entertainment", "Salary", "Business", "Gift", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // 2. SETUP DATE PICKER
        Calendar calendar = Calendar.getInstance();
        updateDateLabel(calendar);

        tvDatePicker.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel(calendar);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 3. SAVE BUTTON
        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            String note = etNote.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            if (amountStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String type = rbExpense.isChecked() ? "Expense" : "Income";

            // Save to Cloud
            saveToFirestore(amount, type, category, note, selectedDate, view);
        });

        return view;
    }

    private void saveToFirestore(double amount, String type, String category, String note, String date, View view) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            btnSave.setEnabled(false);

            // Prepare Data Map
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("amount", amount);
            transaction.put("type", type);
            transaction.put("category", category);
            transaction.put("note", note);
            transaction.put("date", date);
            transaction.put("timestamp", FieldValue.serverTimestamp());

            firestore.collection("users").document(user.getUid()).collection("transactions")
                    .add(transaction)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getActivity(), "Saved to Cloud!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigateUp();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateLabel(Calendar calendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        selectedDate = sdf.format(calendar.getTime());
        tvDatePicker.setText(selectedDate);
    }
}