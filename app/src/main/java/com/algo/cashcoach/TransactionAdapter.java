package com.algo.cashcoach;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private ArrayList<Transaction> transactionList;
    private DeleteListener deleteListener;

    public interface DeleteListener {
        void onDelete(String id);
    }

    public TransactionAdapter(Context context, ArrayList<Transaction> list, DeleteListener listener) {
        super(context, 0, list);
        this.context = context;
        this.transactionList = list;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        }

        Transaction t = transactionList.get(position);

        TextView tvCategory = convertView.findViewById(R.id.item_category);
        TextView tvDateNote = convertView.findViewById(R.id.item_date_note);
        TextView tvAmount = convertView.findViewById(R.id.item_amount);
        ImageView btnDelete = convertView.findViewById(R.id.btn_delete_item);

        tvCategory.setText(t.category);
        tvDateNote.setText(t.date + " | " + t.note);
        tvAmount.setText("LKR " + t.amount);

        if (t.type != null && t.type.equalsIgnoreCase("Income")) {
            tvAmount.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            tvAmount.setTextColor(Color.parseColor("#F44336"));
        }

        btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(t.id);
            }
        });

        return convertView;
    }
}