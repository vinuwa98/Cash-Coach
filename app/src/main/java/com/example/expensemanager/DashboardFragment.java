package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseApp;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    //Floating Button
    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income;
    private FloatingActionButton fab_expense;

    //Floating Button TextView
    private TextView fab_income_text;
    private TextView fab_expense_text;

    private boolean isOpen = false;

    // Animation objects
    private Animation fadeOpen, fadeClose;

    // Dashboard income and expense result
    private TextView totalIncomResult;
    private TextView totalExpenseResult;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    // Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);

        // Connect Floating Button to layout
        fab_main = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income = myview.findViewById(R.id.income_ft_btn);
        fab_expense = myview.findViewById(R.id.expense_ft_btn);

        // Connect floating text
        fab_income_text = myview.findViewById(R.id.income_ft_text);
        fab_expense_text = myview.findViewById(R.id.expense_ft_text);

        // Total income and expense
        totalIncomResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        // Recycler views
        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        // Load animations
        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        // Set onClickListener for main floating button
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                floatingButtonAnimation();
            }
        });

        // Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    total += data.getAmount();
                    String stResult = String.valueOf(total);
                    totalIncomResult.setText(stResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardFragment", "Failed to retrieve income data: " + error.getMessage());
            }
        });

        // Calculate total expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    total += data.getAmount();
                    String stResult = String.valueOf(total);
                    totalExpenseResult.setText(stResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardFragment", "Failed to retrieve expense data: " + error.getMessage());
            }
        });

        // Setup RecyclerView for income
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        // Setup RecyclerView for expense
        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    // Method to handle floating button animation
    private void floatingButtonAnimation() {
        if (isOpen) {
            fab_income.startAnimation(fadeClose);
            fab_expense.startAnimation(fadeClose);
            fab_income.setClickable(false);
            fab_expense.setClickable(false);
            fab_income_text.startAnimation(fadeClose);
            fab_expense_text.startAnimation(fadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
        } else {
            fab_income.startAnimation(fadeOpen);
            fab_expense.startAnimation(fadeOpen);
            fab_income.setClickable(true);
            fab_expense.setClickable(true);
            fab_expense_text.startAnimation(fadeOpen);
            fab_income_text.startAnimation(fadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
        }
        isOpen = !isOpen;
    }

    // Method to handle adding income or expense data
    private void addData() {
        // Handle income button click
        fab_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIncomeData();
            }
        });

        // Handle expense button click
        fab_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertExpenseData();
            }
        });
    }

    // Method to insert income data into Firebase
    public void insertIncomeData() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount = myview.findViewById(R.id.amount);
        EditText edtType = myview.findViewById(R.id.type_edt);
        EditText edtNote = myview.findViewById(R.id.note_edt);

        Button saveBtn = myview.findViewById(R.id.btnSave);
        Button cancelBtn = myview.findViewById(R.id.btnCancel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edtType.getText().toString().trim();
                String amount = edtamount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Please Enter A Type");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Please Enter A Note");
                    return;
                }
                int amountInInt = Integer.parseInt(amount);

                // Create random ID inside database
                String id = mIncomeDatabase.push().getKey();

                // Get current date
                String mDate = DateFormat.getDateInstance().format(new Date());

                // Create Data object
                Data data = new Data(amountInInt, type, note, id, mDate);

                // Store data in Firebase database
                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingButtonAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // Method to insert expense data into Firebase
    public void insertExpenseData() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount = myview.findViewById(R.id.amount);
        EditText edttype = myview.findViewById(R.id.type_edt);
        EditText edtnote = myview.findViewById(R.id.note_edt);

        Button saveBtn = myview.findViewById(R.id.btnSave);
        Button cancelBtn = myview.findViewById(R.id.btnCancel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edtamount.getText().toString().trim();
                String type = edttype.getText().toString().trim();
                String note = edtnote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edttype.setError("Please Enter A Type");
                    return;
                }
                if (TextUtils.isEmpty(amount)) {
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    edtnote.setError("Please Enter A Note");
                    return;
                }
                int amountInInt = Integer.parseInt(amount);

                // Create random ID inside database
                String id = mExpenseDatabase.push().getKey();

                // Get current date
                String mDate = DateFormat.getDateInstance().format(new Date());

                // Create Data object
                Data data = new Data(amountInInt, type, note, id, mDate);

                // Store data in Firebase database
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                floatingButtonAnimation();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set up FirebaseRecyclerAdapter for income
        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                Data.class,
                R.layout.dashboard_income,
                IncomeViewHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int i) {
                incomeViewHolder.setIncomeType(data.getType());
                incomeViewHolder.setIncomeAmount(data.getAmount());
                incomeViewHolder.setIncomeDate(data.getDate());
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        // Set up FirebaseRecyclerAdapter for expense
        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                ExpenseViewHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {
                expenseViewHolder.setExpenseType(data.getType());
                expenseViewHolder.setExpenseAmount(data.getAmount());
                expenseViewHolder.setExpenseDate(data.getDate());
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
    }

    // ViewHolder for income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mtype = mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount) {
            TextView mAmount = mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }
    }

    // ViewHolder for expense data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        public void setExpenseType(String type) {
            TextView mtype = mExpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }

        public void setExpenseAmount(int amount) {
            TextView mAmount = mExpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }

        public void setExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }
    }
}
