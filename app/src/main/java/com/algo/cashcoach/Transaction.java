package com.algo.cashcoach;

public class Transaction {
    String id;
    double amount;
    String type;
    String category;
    String note;
    String date;

    public Transaction(String id, double amount, String type, String category, String note, String date) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.date = date;
    }
}