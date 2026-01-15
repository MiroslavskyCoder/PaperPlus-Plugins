package com.webx.tournaments.integration;

import com.webx.clans.api.ClanIntegrationApi;

public class ClanTournamentIntegration {
    private final ClanIntegrationApi clanApi;

    public ClanTournamentIntegration(ClanIntegrationApi clanApi) {
        this.clanApi = clanApi;
    }

    public boolean payEntryFee(String clanId, double amount) {
        // TODO: Withdraw entry fee from clan bank
        return clanApi.transferClanBankToTournament(clanId, amount);
    }

    public boolean rewardClan(String clanId, double amount) {
        // TODO: Deposit tournament reward to clan bank
        return clanApi.rewardClanFromTournament(clanId, amount);
    }
}
