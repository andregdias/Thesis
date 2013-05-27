package com.opt.mobipag.data;

public class History {
    private String date;
    private double amount;
    private String details;

    public History(String date, double amount, String details) {
        setDate(date);
        setAmount(amount);
        setDetails(details);
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    void setDetails(String details) {
        this.details = details;
    }
}