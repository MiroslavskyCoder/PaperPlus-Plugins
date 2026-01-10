package com.webx.economy.storage;

import com.webx.economy.models.Account;

import java.util.List;

public interface StorageProvider {
    void initialize();
    void close();
    
    List<Account> loadAccounts();
    void saveAccounts(List<Account> accounts);
    void saveAccount(Account account);
    void deleteAccount(java.util.UUID uuid);
}
