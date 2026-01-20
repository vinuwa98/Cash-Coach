package com.algo.cashcoach;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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

public class ReportsFragment extends Fragment {

    private Button btnToday, btnWeek, btnMonth;
    private TextView tvIncome, tvExpense, tvBalance;
    private ListView listView;

    // Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private TransactionAdapter adapter;
    private ArrayList<Transaction> reportList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnToday = view.findViewById(R.id.btn_filter_today);
        btnWeek = view.findViewById(R.id.btn_filter_week);
        btnMonth = view.findViewById(R.id.btn_filter_month);

        tvIncome = view.findViewById(R.id.tv_report_income);
        tvExpense = view.findViewById(R.id.tv_report_expense);
        tvBalance = view.findViewById(R.id.tv_report_balance);
        listView = view.findViewById(R.id.list_report_transactions);

        // Default Load: Today
        loadReport(0);
        highlightButton(btnToday);

        // Button Listeners
        btnToday.setOnClickListener(v -> {
            highlightButton(btnToday);
            loadReport(0);
        });

        btnWeek.setOnClickListener(v -> {
            highlightButton(btnWeek);
            loadReport(7);
        });

        btnMonth.setOnClickListener(v -> {
            highlightButton(btnMonth);
            loadReport(30);
        });

        return view;
    }

    private void loadReport(int daysAgo) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String endDate = sdf.format(cal.getTime()); // Today

        // Calculate Start Date
        String startDate;
        if (daysAgo == 0) {
            startDate = endDate;
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo);
            startDate = sdf.format(cal.getTime());
        }

        firestore.collection("users").document(user.getUid()).collection("transactions")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reportList = new ArrayList<>();
                    double totalIncome = 0;
                    double totalExpense = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        Double amount = doc.getDouble("amount");
                        String type = doc.getString("type");
                        String category = doc.getString("category");
                        String note = doc.getString("note");
                        String date = doc.getString("date");

                        if (amount != null && type != null) {
                            reportList.add(new Transaction(id, amount, type, category, note, date));

                            // Calculate Totals
                            if (type.equalsIgnoreCase("Income")) {
                                totalIncome += amount;
                            } else if (type.equalsIgnoreCase("Expense")) {
                                totalExpense += amount;
                            }
                        }
                    }

                    tvIncome.setText(String.format(Locale.getDefault(), "%.2f", totalIncome));
                    tvExpense.setText(String.format(Locale.getDefault(), "%.2f", totalExpense));
                    tvBalance.setText(String.format(Locale.getDefault(), "%.2f", totalIncome - totalExpense));

                    adapter = new TransactionAdapter(getActivity(), reportList, null);
                    listView.setAdapter(adapter);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error loading report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void highlightButton(Button active) {
        btnToday.setBackgroundColor(Color.parseColor("#B0BEC5"));
        btnWeek.setBackgroundColor(Color.parseColor("#B0BEC5"));
        btnMonth.setBackgroundColor(Color.parseColor("#B0BEC5"));

        active.setBackgroundColor(Color.parseColor("#3F51B5"));
    }
}