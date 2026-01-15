package com.webx.clans.bank;

import java.time.Instant;

public class ClanBankTransaction {
    private String type; // deposit, withdraw
    private double amount;
    private String actor;
    private Instant timestamp;
    // TODO: Add transaction id, reason, etc.

    public ClanBankTransaction(String type, double amount, String actor, Instant timestamp) {
        this.type = type;
        this.amount = amount;
        this.actor = actor;
        this.timestamp = timestamp;
    }
    // Getters and setters...
}
