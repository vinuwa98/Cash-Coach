package com.algo.cashcoach;

import android.app.AlertDialog;
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
import androidx.navigation.Navigation;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoansFragment extends Fragment {

    private ListView listView;
    private TextView tvEmpty;
    private Button btnAddLoan;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private ArrayList<Loan> loanList;
    private LoanAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loans, container, false);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        listView = view.findViewById(R.id.list_loans);
        tvEmpty = view.findViewById(R.id.tv_empty_loans);
        btnAddLoan = view.findViewById(R.id.btn_go_to_add_loan);

        loadLoans();

        btnAddLoan.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_loans_to_addLoan);
        });

        return view;
    }

    private void loadLoans() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        firestore.collection("users").document(user.getUid()).collection("loans")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    loanList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String desc = doc.getString("description");
                        Double amount = doc.getDouble("amount");
                        String status = doc.getString("status");

                        if (amount != null) {
                            loanList.add(new Loan(doc.getId(), desc, amount, status));
                        }
                    }

                    updateUI();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading loans", Toast.LENGTH_SHORT).show());
    }

    private void updateUI() {
        if (loanList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new LoanAdapter(getActivity(), loanList, new LoanAdapter.LoanActionListener() {
                @Override
                public void onDelete(String id) {
                    showDeleteDialog(id);
                }

                @Override
                public void onRepay(Loan loan) {
                    showRepayDialog(loan);
                }
            });
            listView.setAdapter(adapter);
        }
    }

    private void showDeleteDialog(String id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Record?")
                .setMessage("This will remove the loan permanently.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteLoan(id);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteLoan(String id) {
        String uid = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(uid).collection("loans").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Record Deleted", Toast.LENGTH_SHORT).show();
                    loadLoans();
                });
    }

    private void showRepayDialog(Loan loan) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Mark as Repaid?")
                .setMessage("This will add an EXPENSE of LKR " + loan.amount + " to your history and mark this loan as Paid.")
                .setPositiveButton("Repay Now", (dialog, which) -> {
                    performRepayment(loan);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performRepayment(Loan loan) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();

        // 1. Create Transaction Data as Expense
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", loan.amount);
        transaction.put("type", "Expense");
        transaction.put("category", "Loan Repay");
        transaction.put("note", "Repaid: " + loan.description);
        transaction.put("date", date);
        transaction.put("timestamp", FieldValue.serverTimestamp());

        // 2. Add to Transactions Collection
        firestore.collection("users").document(uid).collection("transactions")
                .add(transaction)
                .addOnSuccessListener(docRef -> {
                    // 3. IF successful, update Loan status to "Paid"
                    updateLoanStatus(uid, loan.id);
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Repayment Failed", Toast.LENGTH_SHORT).show());
    }

    private void updateLoanStatus(String uid, String loanId) {
        firestore.collection("users").document(uid).collection("loans").document(loanId)
                .update("status", "Paid")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Loan Repaid Successfully!", Toast.LENGTH_SHORT).show();
                    loadLoans();
                });
    }
}