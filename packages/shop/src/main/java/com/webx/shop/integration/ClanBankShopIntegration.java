package com.webx.shop.integration;

import com.webx.clans.api.ClanIntegrationApi;
import org.bukkit.entity.Player;

public class ClanBankShopIntegration {
    private final ClanIntegrationApi clanApi;

    public ClanBankShopIntegration(ClanIntegrationApi clanApi) {
        this.clanApi = clanApi;
    }

    public boolean purchaseWithClanBank(Player player, String clanId, double price) {
        // TODO: Check clan bank balance and withdraw for shop purchase
        return clanApi.withdrawFromClanBankToEconomy(clanId, price);
    }
}
