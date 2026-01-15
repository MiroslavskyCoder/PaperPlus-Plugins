package com.webx.clans.api;

import org.bukkit.entity.Player;

public class ClanBankApi {
    // TODO: Expose clan bank API for other plugins (e.g., tournaments, web)
    public double getClanBalance(String clanId) { return 0.0; }
    public boolean deposit(String clanId, double amount, Player actor) { return false; }
    public boolean withdraw(String clanId, double amount, Player actor) { return false; }
}
