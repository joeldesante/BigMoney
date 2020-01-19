package com.yooogle.bigmoney.commands.addmoney;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.commands.Command;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddMoneyCommand extends Command {

    public AddMoneyCommand(BigMoney plugin) {
        super("addmoney", "bigmoney.admin.addmoney", false, plugin);
    }

    @Override
    public void preExecute(CommandSender sender, String[] args, BigMoney plugin) {
        // Nothing
    }

    @Override
    public void execute(CommandSender sender, String[] args, BigMoney plugin) {

        Player player = (Player) sender;

        if(args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please specify a player.");
            return;
        }

        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Please specify an amount.");
            return;
        }

        OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(args[0]);
        double amount = 0;

        try {
            amount = Double.parseDouble(args[1]);
        } catch(NumberFormatException ne) {
            player.sendMessage(ChatColor.RED + "Please check your amount and try again.");
            return;
        } catch(Exception e) {
            player.sendMessage(ChatColor.RED + "Unexpected error occurred.");
            e.printStackTrace();
            return;
        }

        if(amount <= 0) {
            player.sendMessage(ChatColor.RED + "You must choose an amount greater than zero.");
            return;
        }

        AccountResponse targetAccount = plugin.getAccountManager().fetchAccount(targetPlayer.getUniqueId());
        if(!targetAccount.isSuccess()) {
            player.sendMessage(ChatColor.RED + "Account not found.");
            return;
        }

        AccountResponse addMoney = targetAccount.getAccount().addBalance(amount);
        if(!addMoney.isSuccess()) {
            player.sendMessage(ChatColor.RED + "Failed to deposit funds into account. Check the console for errors.");
            plugin.getLogger().severe("Failed to deposit funds into " + targetPlayer.getName() + "'s account. " + addMoney.getReason());
            return;
        }

        // Success!
        player.sendMessage(ChatColor.GREEN + "Successfully deposited " + Util.format(amount) + " into " + targetPlayer.getName() + "'s account.");
        if(targetPlayer.isOnline()) {
            // Send if the player is on the server.
            ((Player) targetPlayer).sendMessage(ChatColor.GREEN + Util.format(amount) + " has been deposited into your account.");
        }

    }
}
