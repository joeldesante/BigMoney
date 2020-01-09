package com.yooogle.bigmoney.commands.balance;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.commands.Command;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand extends Command {

    public BalanceCommand(BigMoney plugin) {

        super(new String[]{"balance", "bal", "money"}, "bigmoney.balance", true, plugin);

        // Register SubCommands

    }

    @Override
    public void preExecute(CommandSender sender, String[] args, BigMoney plugin) {
        // Nothing...
    }

    @Override
    public void execute(CommandSender sender, String[] args, BigMoney plugin) {

        OfflinePlayer target;

        if(args.length > 0) {
            if (sender.hasPermission("bigmoney.balance.other")) {
                target = plugin.getServer().getOfflinePlayer(args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
                return;
            }
        } else {
            // If there are no args be sure to make sure the sender is a player
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command may only be used by players.");
                return;
            }
            target = (Player) sender;
        }

        AccountResponse accountBalance = plugin.getAccountManager().fetchAccount(target.getUniqueId());

        if(accountBalance.isSuccess()) {
            double balance = accountBalance.getAccount().getBalance();
            sender.sendMessage(ChatColor.GREEN + "Account Balance: " + Util.format(balance));
            return;
        }

        sender.sendMessage(ChatColor.RED + "No account found.");

    }
}
