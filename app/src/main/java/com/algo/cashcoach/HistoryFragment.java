package com.algo.cashcoach;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private ListView listView;
    private TextView tvEmpty;

    // UI elements for filtering
    private TextView tvFromDate, tvToDate, tvClear;
    private Spinner spType;
    private ImageButton btnSearch;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    // Data Lists
    private ArrayList<Transaction> transactionList;
    private TransactionAdapter adapter;

    // Filter States
    private String currentType = "All";
    private String fromDateString = "";
    private String toDateString = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Lists
        listView = view.findViewById(R.id.list_transactions);
        tvEmpty = view.findViewById(R.id.tv_empty_history);

        // Initialize Filter Controls
        spType = view.findViewById(R.id.spinner_filter_type);
        tvFromDate = view.findViewById(R.id.tv_date_from);
        tvToDate = view.findViewById(R.id.tv_date_to);
        btnSearch = view.findViewById(R.id.btn_filter_search);
        tvClear = view.findViewById(R.id.tv_clear_filters);

        // 1. Setup Spinner
        String[] types = {"All Records", "Incomes Only", "Expenses Only"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(spinAdapter);

        // 2. Spinner Selection
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) currentType = "All";
                else if (position == 1) currentType = "Income";
                else currentType = "Expense";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 3. Date Pickers
        tvFromDate.setOnClickListener(v -> showDatePicker(tvFromDate, true));
        tvToDate.setOnClickListener(v -> showDatePicker(tvToDate, false));

        // 4. Search button
        btnSearch.setOnClickListener(v -> loadHistory());

        // 5. Clear filters
        tvClear.setOnClickListener(v -> {
            fromDateString = "";
            toDateString = "";
            tvFromDate.setText("Select Date");
            tvToDate.setText("Select Date");
            spType.setSelection(0);
            currentType = "All";
            loadHistory();
        });

        loadHistory();

        return view;
    }

    private void showDatePicker(TextView textView, boolean isFrom) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
            cal.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(cal.getTime());

            textView.setText(date);

            if (isFrom) fromDateString = date;
            else toDateString = date;

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadHistory() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Query query = firestore.collection("users")
                .document(user.getUid())
                .collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING);

        // Apply Date Filters
        if (!fromDateString.isEmpty() && !toDateString.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("date", fromDateString)
                    .whereLessThanOrEqualTo("date", toDateString);
        }

        // Execute Query
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                transactionList = new ArrayList<>();

                for (DocumentSnapshot doc : task.getResult()) {
                    Double amount = doc.getDouble("amount");
                    String type = doc.getString("type");
                    String category = doc.getString("category");
                    String note = doc.getString("note");
                    String date = doc.getString("date");

                    if (!currentType.equals("All") && type != null && !type.equalsIgnoreCase(currentType)) {
                        continue;
                    }

                    if (amount != null) {
                        transactionList.add(new Transaction(doc.getId(), amount, type, category, note, date));
                    }
                }
                updateUI();
            } else {
                Toast.makeText(getActivity(), "Error loading: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (transactionList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            if(!fromDateString.isEmpty() || !currentType.equals("All")) {
                tvEmpty.setText("No records found for this filter.");
            } else {
                tvEmpty.setText("No transactions found.");
            }
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new TransactionAdapter(getActivity(), transactionList, this::showDeleteDialog);
            listView.setAdapter(adapter);
        }
    }

    private void showDeleteDialog(String id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteTransactionFromFirestore(id);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteTransactionFromFirestore(String id) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        firestore.collection("users")
                .document(user.getUid())
                .collection("transactions")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                    loadHistory();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error deleting: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}