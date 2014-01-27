
package com.xifan.myaccount.data;

public class Account {
    
    public static int currentAccountId = 1;
    public static int currentAccountType = 1;
    
    private int accountType;
    private float balance;
    private float expend;
    private float revenue;
    private int id;

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public float getExpend() {
        return expend;
    }

    public void setExpend(float expend) {
        this.expend = expend;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
