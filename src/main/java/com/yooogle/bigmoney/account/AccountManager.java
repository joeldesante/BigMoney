package com.yooogle.bigmoney.account;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.response.DatabaseResponse;
import com.yooogle.bigmoney.response.ResponseType;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class AccountManager {

    private ArrayList<Account> loadedAccounts = new ArrayList<>();
    private BukkitTask autoSave;
    private BigMoney plugin;

    public AccountManager(BigMoney plugin) {
        this.plugin = plugin;
    }

    public void autosave(long delay, AccountManager accountManager, BigMoney plugin) {
        // Setup autosave
        this.autoSave = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (accountManager.getLoadedAccounts().size() > 0) {
                    accountManager.saveAll();

                    if(plugin.getConfig().getBoolean("save-logs")) {
                        plugin.getLogger().info(ChatColor.GREEN + "All loaded accounts have been saved to to disk.");
                    }
                }
            }
        }, 20*delay, 20*delay);
    }

    public void cancelAutosave() {
        this.autoSave.cancel();
    }

    public AccountResponse load(UUID ownerUUID) {
        // Loads the account from file
        DatabaseResponse account = this.plugin.getDatabase().fetchAccount(ownerUUID);

        if(account.isSuccess()) {
            this.loadedAccounts.add(account.getAccount());
            return new AccountResponse(account.getAccount(), ResponseType.SUCCESS, "Account loaded.");
        }

        // If it does not exist in file
        return new AccountResponse(null, ResponseType.FAILURE, "Account does not exist.");
    }

    public AccountResponse unload(UUID ownerUUID) {

        Account target = null;

        for(Account a : this.loadedAccounts) {
            if(a.getOwnerUUID() == ownerUUID) {
                target = a;
                break;
            }
        }

        if(target != null) {
            this.loadedAccounts.remove(target);
            return new AccountResponse(target, ResponseType.SUCCESS, "Account unloaded");
        }

        return new AccountResponse(null, ResponseType.FAILURE, "Account not loaded");
    }

    public void unloadAll() {
        this.loadedAccounts.clear();
    }

    public AccountResponse save(UUID ownerUUID) {
        for(Account a : this.loadedAccounts) {
            if(a.getOwnerUUID() == ownerUUID) {
                DatabaseResponse updateAccount =  this.plugin.getDatabase().update(a.getOwnerUUID(), a.getBalance(), a.getAccountStatus());

                if(updateAccount.isSuccess()) {
                    return new AccountResponse(a, ResponseType.SUCCESS, updateAccount.getReason());
                }

                return new AccountResponse(null, ResponseType.FAILURE, updateAccount.getReason());
            }
        }

        return new AccountResponse(null, ResponseType.FAILURE, "Account not loaded");
    }

    public void saveAll() {
        for(Account a : this.loadedAccounts) {

            DatabaseResponse saveAccount = this.plugin.getDatabase().update(a.getOwnerUUID(), a.getBalance(), a.getAccountStatus());

            if(!saveAccount.isSuccess()) {
                this.plugin.getLogger().severe("ACCOUNT FAILED TO SAVE: " + saveAccount.getReason());
            }

        }
    }

    public AccountResponse create(UUID ownerUUID, double startingBalance) {

        DatabaseResponse fetchAccount = this.plugin.getDatabase().fetchAccount(ownerUUID);
        if(fetchAccount.isSuccess()) {
            return new AccountResponse(null, ResponseType.FAILURE, "Account already exists.");
        }

        Account a = new Account(ownerUUID, this.plugin);
        a.setBalance(startingBalance);

        DatabaseResponse createAccount = this.plugin.getDatabase().insert(a.getOwnerUUID(), a.getBalance(), a.getAccountStatus());
        if(!createAccount.isSuccess()) {
            return new AccountResponse(null, ResponseType.FAILURE, createAccount.getReason());
        }

        this.plugin.getAccountManager().load(a.getOwnerUUID());
        return new AccountResponse(a, ResponseType.SUCCESS, "Account created.");
    }

    public AccountResponse create(UUID ownerUUID) {
        double initialBalance = this.plugin.getConfig().getDouble("initial-balance");
        return this.create(ownerUUID, initialBalance);
    }

    public ArrayList<Account> getLoadedAccounts() {
        return loadedAccounts;
    }

    public AccountResponse getLoadedAccount(UUID ownerUUID) {
        for(Account a : this.loadedAccounts) {
            if(a.getOwnerUUID() == ownerUUID) {
                return new AccountResponse(a, ResponseType.SUCCESS, "Account was found in memory.");
            }
        }

        return new AccountResponse(null, ResponseType.FAILURE, "Account was not found.");
    }

    public boolean hasAccount(UUID ownerUUID) {

        // First check the loaded accounts
        for (Account a : this.loadedAccounts) {
            if(a.getOwnerUUID() == ownerUUID) {
                return true;
            }
        }

        // If not already loaded in then check the database
        DatabaseResponse accountCheck = this.plugin.getDatabase().fetchAccount(ownerUUID);
        if(accountCheck.isSuccess()) {
            return true;
        }

        // Otherwise there is no account
        return false;

    }

    public AccountResponse fetchAccount(UUID ownerUUID) {

        // First check the loaded accounts
        for (Account a : this.loadedAccounts) {
            if(a.getOwnerUUID() == ownerUUID) {
                return new AccountResponse(a, ResponseType.SUCCESS, "Account was found in memory.");
            }
        }

        // If not already loaded in then check the database
        DatabaseResponse fetchAccount = this.plugin.getDatabase().fetchAccount(ownerUUID);
        if(fetchAccount.isSuccess()) {
            return new AccountResponse(fetchAccount.getAccount(), ResponseType.SUCCESS, "Account was found in the database.");
        }

        return new AccountResponse(null, ResponseType.FAILURE, "Account was not found.");
    }
}
