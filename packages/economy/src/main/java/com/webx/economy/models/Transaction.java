package com.webx.economy.models;

import java.util.UUID;

public class Transaction {
    private final UUID from;
    private final UUID to;
    private final double amount;
    private final double fee;
    private final TransactionType type;
    private final long timestamp;

    public Transaction(UUID from, UUID to, double amount, double fee, TransactionType type) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() {
        return fee;
    }

    public TransactionType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public enum TransactionType {
        PAYMENT,
        BANK_DEPOSIT,
        BANK_WITHDRAW,
        ADMIN_GIVE,
        ADMIN_TAKE,
        INTEREST
    }
}
