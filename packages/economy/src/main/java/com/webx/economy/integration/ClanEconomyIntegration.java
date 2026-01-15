package com.webx.economy.integration;

import com.webx.clans.api.ClanIntegrationApi;

public class ClanEconomyIntegration {
    private final ClanIntegrationApi clanApi;

    public ClanEconomyIntegration(ClanIntegrationApi clanApi) {
        this.clanApi = clanApi;
    }

    public boolean depositToClanBank(String clanId, double amount) {
        // TODO: Deposit from player economy to clan bank
        return clanApi.depositToClanBankFromEconomy(clanId, amount);
    }

    public boolean withdrawFromClanBank(String clanId, double amount) {
        // TODO: Withdraw from clan bank to player economy
        return clanApi.withdrawFromClanBankToEconomy(clanId, amount);
    }
}
