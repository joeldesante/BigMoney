package com.yooogle.bigmoney;

import com.yooogle.bigmoney.account.Account;
import com.yooogle.bigmoney.account.AccountStatus;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.utils.Util;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class EconomyImplementor implements Economy {

    private BigMoney plugin;

    public EconomyImplementor(BigMoney plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "BigEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return Util.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "dollar";
    }

    @Override
    public String currencyNameSingular() {
        return "dollars";
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return false;
        }

        return this.plugin.getAccountManager().hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.plugin.getAccountManager().hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        /*
            BigMoney does not support world specific balances
         */
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return false;
        }

        return this.plugin.getAccountManager().hasAccount(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        /*
            BigMoney does not support world specific balances
         */
        return this.plugin.getAccountManager().hasAccount(player.getUniqueId());
    }

    @Override
    public double getBalance(String playerName) {
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return 0D;
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return 0D;
        }

        return a.getAccount().getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return 0D;
        }

        return a.getAccount().getBalance();
    }

    @Override
    public double getBalance(String playerName, String world) {
        /*
            BigMoney does not support world specific balances
        */
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return 0D;
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return 0D;
        }

        return a.getAccount().getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        /*
            BigMoney does not support world specific balances
        */
        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return 0D;
        }

        return a.getAccount().getBalance();
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return false;
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return false;
        }

        return a.getAccount().getBalance() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return false;
        }

        return a.getAccount().getBalance() >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        /*
            BigMoney does not support world specific balances
        */
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return false;
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return false;
        }

        return a.getAccount().getBalance() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        /*
            BigMoney does not support world specific balances
        */
        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return false;
        }

        return a.getAccount().getBalance() >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not fetch account.");
        }

        AccountResponse subtractBalance = a.getAccount().subtractBalance(amount);
        if (!subtractBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, subtractBalance.getReason());
        }

        return new EconomyResponse(amount, a.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not fetch account.");
        }

        AccountResponse subtractBalance = a.getAccount().subtractBalance(amount);
        if (!subtractBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, subtractBalance.getReason());
        }

        return new EconomyResponse(amount, a.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        /*
         *  Big Money does not support specific world balances
         */
        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not fetch account.");
        }

        AccountResponse subtractBalance = a.getAccount().subtractBalance(amount);
        if (!subtractBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, subtractBalance.getReason());
        }

        return new EconomyResponse(amount, a.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {

        // Does not support specific world balances

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not fetch account.");
        }

        AccountResponse subtractBalance = a.getAccount().subtractBalance(amount);
        if (!subtractBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, subtractBalance.getReason());
        }

        return new EconomyResponse(amount, a.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {

        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, a.getReason());
        }

        AccountResponse addBalance = a.getAccount().addBalance(amount);
        if(!addBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, addBalance.getReason());
        }

        return new EconomyResponse(amount, addBalance.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");

    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, a.getReason());
        }

        AccountResponse addBalance = a.getAccount().addBalance(amount);
        if(!addBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, addBalance.getReason());
        }

        return new EconomyResponse(amount, addBalance.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {

        // Does not support multi world

        Player player = this.plugin.getServer().getPlayer(playerName);

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, a.getReason());
        }

        AccountResponse addBalance = a.getAccount().addBalance(amount);
        if(!addBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, addBalance.getReason());
        }

        return new EconomyResponse(amount, addBalance.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {

        // Does not support multi world

        if(player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Could not find player.");
        }

        AccountResponse a = this.plugin.getAccountManager().fetchAccount(player.getUniqueId());
        if(!a.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, a.getReason());
        }

        AccountResponse addBalance = a.getAccount().addBalance(amount);
        if(!addBalance.isSuccess()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, addBalance.getReason());
        }

        return new EconomyResponse(amount, addBalance.getAccount().getBalance(), EconomyResponse.ResponseType.SUCCESS, "");

    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0,0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "BigMoney does not support banking by default");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }
}
