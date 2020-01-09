package com.yooogle.bigmoney.vault;

import com.yooogle.bigmoney.BigMoney;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

    private BigMoney plugin;
    private Economy provider;

    public VaultHook(BigMoney plugin) {
        this.plugin = plugin;
    }

    public void hook() {
        this.provider = this.plugin.getEconomyImplementor();
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);
        this.plugin.getLogger().info(ChatColor.GREEN + "Vault hooked.");
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        this.plugin.getLogger().info(ChatColor.RED + "Vault unhooked.");
    }

}
