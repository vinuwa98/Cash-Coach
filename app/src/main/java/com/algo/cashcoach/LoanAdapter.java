package com.algo.cashcoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LoanAdapter extends ArrayAdapter<Loan> {

    private Context context;
    private ArrayList<Loan> loanList;
    private LoanActionListener listener;

    public interface LoanActionListener {
        void onDelete(String id);
        void onRepay(Loan loan);
    }

    public LoanAdapter(Context context, ArrayList<Loan> list, LoanActionListener listener) {
        super(context, 0, list);
        this.context = context;
        this.loanList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_loan, parent, false);
        }

        Loan l = loanList.get(position);

        TextView tvDesc = convertView.findViewById(R.id.item_loan_desc);
        TextView tvStatus = convertView.findViewById(R.id.item_loan_status);
        TextView tvAmount = convertView.findViewById(R.id.item_loan_amount);
        ImageView btnDelete = convertView.findViewById(R.id.btn_delete_loan);
        ImageView btnRepay = convertView.findViewById(R.id.btn_repay_loan);

        tvDesc.setText(l.description);
        tvAmount.setText("LKR " + l.amount);

        // Check Loan Status
        if ("Paid".equalsIgnoreCase(l.status)) {
            tvStatus.setText("Paid");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green

            btnRepay.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.VISIBLE);

        } else {
            tvStatus.setText("Pending");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange
            btnRepay.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        // Delete
        btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(l.id);
        });

        // Repay
        btnRepay.setOnClickListener(v -> {
            if (listener != null) listener.onRepay(l);
        });

        return convertView;
    }
}