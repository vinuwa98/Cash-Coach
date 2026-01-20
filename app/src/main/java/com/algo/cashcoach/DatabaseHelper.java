package com.algo.cashcoach;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CashCoach.db";
    private static final int DATABASE_VERSION = 2;

    // Tables
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_LOANS = "loans";

    // Common Columns
    private static final String COL_ID = "id";

    // User Columns
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";

    // Transaction Columns
    private static final String COL_AMOUNT = "amount";
    private static final String COL_TYPE = "type";
    private static final String COL_CATEGORY = "category";
    private static final String COL_NOTE = "note";
    private static final String COL_DATE = "date";

    // Loan Columns
    private static final String COL_DESC = "description";
    private static final String COL_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // 2. Create Transactions Table
        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " REAL, " +
                COL_TYPE + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_NOTE + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createTransactionsTable);

        // 3. Create Loans Table
        String createLoansTable = "CREATE TABLE " + TABLE_LOANS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESC + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_STATUS + " TEXT)";
        db.execSQL(createLoansTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        onCreate(db);
    }

    // USER METHODS
    public boolean addUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // TRANSACTION METHODS
    public boolean addTransaction(double amount, String type, String category, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_TYPE, type);
        values.put(COL_CATEGORY, category);
        values.put(COL_NOTE, note);
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_TRANSACTIONS, null, values);
        return result != -1;
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COL_ID + " DESC", null);
    }

    public boolean deleteTransactionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TRANSACTIONS, COL_ID + "=" + id, null) > 0;
    }

    // LOAN METHODS
    public boolean addLoan(String desc, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESC, desc);
        values.put(COL_AMOUNT, amount);
        values.put(COL_STATUS, "Active");

        long result = db.insert(TABLE_LOANS, null, values);
        return result != -1;
    }

    public Cursor getAllLoans() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOANS + " ORDER BY " + COL_ID + " DESC", null);
    }

    public boolean deleteLoanById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LOANS, COL_ID + "=" + id, null) > 0;
    }

    // 9. Mark Loan as Paid and Add to Expense
    public boolean repayLoan(int id, String desc, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues transactionValues = new ContentValues();
        transactionValues.put(COL_AMOUNT, amount);
        transactionValues.put(COL_TYPE, "Expense");
        transactionValues.put(COL_CATEGORY, "Loan Repay");
        transactionValues.put(COL_NOTE, "Repaid: " + desc);
        transactionValues.put(COL_DATE, date);
        long result = db.insert(TABLE_TRANSACTIONS, null, transactionValues);

        if (result == -1) return false;

        ContentValues loanValues = new ContentValues();
        loanValues.put(COL_STATUS, "Paid");

        return db.update(TABLE_LOANS, loanValues, COL_ID + "=" + id, null) > 0;
    }

    // DASHBOARD METHODS
    public double getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalIncome = 0;
        double totalExpense = 0;

        // Sum Income
        Cursor cursorIncome = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TYPE + "='Income'", null);
        if (cursorIncome.moveToFirst()) {
            totalIncome = cursorIncome.getDouble(0);
        }
        cursorIncome.close();

        // Sum Expense
        Cursor cursorExpense = db.rawQuery("SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_TYPE + "='Expense'", null);
        if (cursorExpense.moveToFirst()) {
            totalExpense = cursorExpense.getDouble(0);
        }
        cursorExpense.close();

        return totalIncome - totalExpense;
    }

    // 10. Get Transactions by Date Range
    public Cursor getTransactionsByDate(String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_DATE + " BETWEEN ? AND ? ORDER BY " + COL_DATE + " DESC",
                new String[]{startDate, endDate});
    }
}