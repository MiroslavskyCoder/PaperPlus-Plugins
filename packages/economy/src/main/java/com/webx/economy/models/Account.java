package com.webx.economy.models;

import java.util.UUID;

public class Account {
    private final UUID owner;
    private double balance;
    private double bankBalance;
    private long lastInterest;

    public Account(UUID owner) {
        this.owner = owner;
        this.balance = 0.0;
        this.bankBalance = 0.0;
        this.lastInterest = System.currentTimeMillis();
    }

    public Account(UUID owner, double balance, double bankBalance, long lastInterest) {
        this.owner = owner;
        this.balance = balance;
        this.bankBalance = bankBalance;
        this.lastInterest = lastInterest;
    }

    public UUID getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = Math.max(0, balance);
    }

    public boolean hasBalance(double amount) {
        return balance >= amount;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (!hasBalance(amount)) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(double bankBalance) {
        this.bankBalance = Math.max(0, bankBalance);
    }

    public boolean hasBankBalance(double amount) {
        return bankBalance >= amount;
    }

    public void depositBank(double amount) {
        this.bankBalance += amount;
    }

    public boolean withdrawBank(double amount) {
        if (!hasBankBalance(amount)) {
            return false;
        }
        this.bankBalance -= amount;
        return true;
    }

    public long getLastInterest() {
        return lastInterest;
    }

    public void setLastInterest(long lastInterest) {
        this.lastInterest = lastInterest;
    }

    public double getTotalBalance() {
        return balance + bankBalance;
    }
}
