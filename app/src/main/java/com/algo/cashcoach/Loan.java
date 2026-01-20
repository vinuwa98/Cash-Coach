package com.algo.cashcoach;

public class Loan {
    String id;
    String description;
    double amount;
    String status;

    public Loan(String id, String description, double amount, String status) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.status = status;
    }
}