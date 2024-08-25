package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateActivity extends AppCompatActivity {

    // UI elements
    private EditText mAmount, mType, mNote;
    private Button mUpdateBtn, mDeleteBtn;

    // Firebase Database reference
    private DatabaseReference mDatabase;

    // Key to identify the specific data entry
    private String postKey;

    // Tag for logging
    private static final String TAG = "UpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data_item);

        // Initialize UI elements
        mAmount = findViewById(R.id.amount);
        mType = findViewById(R.id.type_edt);
        mNote = findViewById(R.id.note_edt);
        mUpdateBtn = findViewById(R.id.btnUpdUpdate);
        mDeleteBtn = findViewById(R.id.btnUpdDelete);

        // Retrieve data passed from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postKey = bundle.getString("postKey");
            double amount = bundle.getDouble("amount");
            String type = bundle.getString("type");
            String note = bundle.getString("note");

            // Populate EditText fields with existing data
            mAmount.setText(String.valueOf(amount));
            mType.setText(type);
            mNote.setText(note);
        }

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseManager").child(postKey);

        // Set onClickListener for Update button
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Update button clicked");
                updateData(); // Call method to update data
            }
        });

        // Set onClickListener for Delete button
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Delete button clicked");
                deleteData(); // Call method to delete data
            }
        });
    }

    // Method to update data in Firebase Database
    private void updateData() {
        try {
            // Retrieve values from EditText fields
            String amountStr = mAmount.getText().toString().trim();
            String typeStr = mType.getText().toString().trim();
            String noteStr = mNote.getText().toString().trim();

            // Log the values being updated
            Log.d(TAG, "Updating data with postKey: " + postKey);
            Log.d(TAG, "Amount: " + amountStr + ", Type: " + typeStr + ", Note: " + noteStr);

            // Validate if Amount field is empty
            if (TextUtils.isEmpty(amountStr)) {
                mAmount.setError("Amount cannot be empty");
                Log.e(TAG, "Amount is empty");
                return;
            }

            // Parse amount from String to double
            double amountDouble = Double.parseDouble(amountStr);

            // Convert double amount to integer if necessary (using Math.round to handle decimal values)
            int amount = (int) Math.round(amountDouble);

            // Create a Data object with updated values
            Data data = new Data(amount, typeStr, noteStr, postKey, null);

            // Update data in Firebase Database
            mDatabase.setValue(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Data updated successfully");
                    Toast.makeText(UpdateActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update data");
                    Toast.makeText(UpdateActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            // Handle NumberFormatException if parsing fails
            Log.e(TAG, "Error parsing amount: " + e.getMessage(), e);
            Toast.makeText(this, "Invalid amount format. Please enter a valid number.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle other exceptions
            Log.e(TAG, "Error updating data: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Placeholder method for deleting data (if needed)
    private void deleteData() {
        // Implement delete operation if required
    }
}
