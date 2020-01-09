package com.yooogle.bigmoney.events;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.response.AccountResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private BigMoney plugin;

    public PlayerListener(BigMoney plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoined) {
        Player player = playerJoined.getPlayer();

        AccountResponse loadPlayer = this.plugin.getAccountManager().load(player.getUniqueId());

        if(!loadPlayer.isSuccess()) {
            AccountResponse createAccount = this.plugin.getAccountManager().create(player.getUniqueId());
            if(!createAccount.isSuccess()) {
                this.plugin.getLogger().severe("Player account failed to create. " + createAccount.getReason());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuit) {
        // Unloads the player and saves their data when they leave

        Player player = playerQuit.getPlayer();

        AccountResponse saveAccount = this.plugin.getAccountManager().save(player.getUniqueId());
        if(!saveAccount.isSuccess()) {
            this.plugin.getLogger().severe("Failed to save player account. " + saveAccount.getReason());
        }

        AccountResponse unloadAccount = this.plugin.getAccountManager().unload(player.getUniqueId());
        if(!unloadAccount.isSuccess()) {
            this.plugin.getLogger().severe("Failed to unload player account. " + unloadAccount.getReason());
        }
    }

}
