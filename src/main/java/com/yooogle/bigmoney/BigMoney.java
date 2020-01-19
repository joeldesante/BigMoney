package com.yooogle.bigmoney;

import com.yooogle.bigmoney.account.AccountManager;
import com.yooogle.bigmoney.commands.SubtractMoney.SubtractMoneyCommand;
import com.yooogle.bigmoney.commands.addmoney.AddMoneyCommand;
import com.yooogle.bigmoney.commands.balance.BalanceCommand;
import com.yooogle.bigmoney.commands.pay.PayCommand;
import com.yooogle.bigmoney.events.PlayerListener;
import com.yooogle.bigmoney.storage.Database;
import com.yooogle.bigmoney.vault.VaultHook;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BigMoney extends JavaPlugin {

    private Database database;
    private AccountManager accountManager;
    private EconomyImplementor economyImplementor;
    private VaultHook vaultHook;
    private YamlConfiguration configuration;

    @Override
    public void onEnable() {

        // Setup Config
        this.saveDefaultConfig();

        // Setup Vault
        this.economyImplementor = new EconomyImplementor(this);
        this.vaultHook = new VaultHook(this);
        this.vaultHook.hook();

        // Setup Database
        this.database = new Database(this.getDataFolder().getAbsolutePath() + "/test.db", this);
        this.database.init();

        // Setup Account Manager
        this.accountManager = new AccountManager(this);

        if(this.getConfig().getBoolean("auto-save")) {
            this.accountManager.autosave(this.getConfig().getLong("auto-save-delay"), this.accountManager, this);
        }

        for(Player p : this.getServer().getOnlinePlayers()) {
            // Load up any players in the event of a reload
            accountManager.load(p.getUniqueId());
        }

        // Setup Listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Setup Commands
        new BalanceCommand(this);
        new PayCommand(this);

        // Admin Commands
        new AddMoneyCommand(this);
        new SubtractMoneyCommand(this);

    }

    @Override
    public void onDisable() {

        // Kill the autosave
        this.accountManager.cancelAutosave();

        // Unload all the users
        this.accountManager.saveAll();
        this.accountManager.unloadAll();

        // Unhook vault
        this.vaultHook.unhook();

    }

    public Database getDatabase() {
        return this.database;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    public EconomyImplementor getEconomyImplementor() {
        return this.economyImplementor;
    }
}
