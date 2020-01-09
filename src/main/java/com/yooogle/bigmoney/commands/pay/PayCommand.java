package com.yooogle.bigmoney.commands.pay;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.account.AccountStatus;
import com.yooogle.bigmoney.commands.Command;
import com.yooogle.bigmoney.response.AccountResponse;
import com.yooogle.bigmoney.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Date;

public class PayCommand extends Command {

    public PayCommand(BigMoney plugin) {
        super("pay", "bigmoney.pay", false, plugin);
    }

    @Override
    public void preExecute(CommandSender sender, String[] args, BigMoney plugin) {
        // Nothing...
    }

    @Override
    public void execute(CommandSender sender, String[] args, BigMoney plugin) {

        Player player = (Player) sender;    // This is the sender

        if(args.length <= 0) {
            player.sendMessage(ChatColor.RED + "Please specify a player whom you'de like to pay.");
            return;
        }

        if(args.length <= 1) {
            player.sendMessage(ChatColor.RED + "Please specify an amount to pay.");
            return;
        }

        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);    // This is the player who is getting paid.

        // Make a quick check for same person payments
        if(target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(ChatColor.RED + "That's no way to make money...");

            // TODO: Send player to jail for fraud?

            return;
        }

        // Fetch both the accounts
        AccountResponse senderAccount = plugin.getAccountManager().getLoadedAccount(player.getUniqueId());
        AccountResponse targetAccount = plugin.getAccountManager().fetchAccount(target.getUniqueId());

        if(!senderAccount.isSuccess()) {
            player.sendMessage(ChatColor.RED + "There was an issue processing your account.");
            return;
        }

        if(!targetAccount.isSuccess()) {
            player.sendMessage(ChatColor.RED + "The given user does not have an account.");
            return;
        }

        if (senderAccount.getAccount().getAccountStatus().equals(AccountStatus.TERMINATED)
                || senderAccount.getAccount().getAccountStatus().equals(AccountStatus.SUSPENDED)) {
            player.sendMessage(ChatColor.RED + "The transaction was canceled because your account is currently. " + senderAccount.getAccount().getAccountStatus().toString());
            return;
        }

        if (targetAccount.getAccount().getAccountStatus().equals(AccountStatus.TERMINATED)
                || targetAccount.getAccount().getAccountStatus().equals(AccountStatus.SUSPENDED)) {
            player.sendMessage(ChatColor.RED + "The transaction was canceled because the other players account is currently." + targetAccount.getAccount().getAccountStatus().toString());
            return;
        }

        // Okay now to validate the amount
        double amount = 0D;

        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException ne) {
            player.sendMessage(ChatColor.RED + "Please check the payment amount and try again.");
            return;
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "There was an unexpected error.");
            e.printStackTrace();
            return;
        }

        if (amount < plugin.getConfig().getDouble("min-payment") || amount < 0) {
            player.sendMessage(ChatColor.RED + "The amount specified does not exceed the minimum payment amount of " + Util.format(plugin.getConfig().getDouble("min-payment")));
            return;
        }

        // Now validate that the player can afford the transaction.
        if (senderAccount.getAccount().getBalance() < amount) {
            player.sendMessage(ChatColor.RED + "You can not afford this transaction.");
            return;
        }

        // If all these tests pass, then execute the transaction
        AccountResponse withdrawSender = senderAccount.getAccount().subtractBalance(amount);

        if(!withdrawSender.isSuccess()) {
            player.sendMessage(ChatColor.RED + "The transaction failed.");
            return;
        }

        // The money has been removed from the senders account. Now add to the targets.
        AccountResponse depositTarget = targetAccount.getAccount().addBalance(amount);

        if(!depositTarget.isSuccess()) {
            // This transaction failed, return the money to the sender
            player.sendMessage(ChatColor.RED + "The transaction failed.");

            AccountResponse returnFunds = senderAccount.getAccount().addBalance(amount);

            if(!returnFunds.isSuccess()) {
                String err = ChatColor.RED + "Something has gone really wrong.\n" +
                        "Please send this message to an Admin to receive any lost funds.\n" +
                        "AMOUNT: " + amount + " - Failed to return funds to user: " + player.getName() + "\n" +
                        "Timestamp: " + new Timestamp(new Date().getTime()) + "\n" +
                        "(Logged to console. Validate in console.)";
                player.sendMessage(err);

                for(int i = 0; i < 5; i++) {
                    plugin.getLogger().severe(err + " -- Reason: " + returnFunds.getReason());
                }

                return;
            }

            return;
        }

        // If you are here it means the money made it to the other players account.
        // It's time to celebrate!

        player.sendMessage(ChatColor.GREEN + "You paid " + Util.format(amount) + " to " + target.getName() + ".");

        if(target.isOnline()) {
            // Only send the message to the target if they are around to see it
            ((Player) target).sendMessage(ChatColor.GREEN + "You received " + Util.format(amount) + " from " + sender.getName() + ".");
        }

        // Done... Phew...

        // TODO: Log all transactions in a database for administrative review.

    }
}
