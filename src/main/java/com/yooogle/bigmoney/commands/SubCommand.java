package com.yooogle.bigmoney.commands;

import com.yooogle.bigmoney.BigMoney;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class SubCommand {

    private final String label;
    private final String permission;
    private final boolean canConsoleUse;
    private BigMoney plugin;
    private ArrayList<SubCommand> registeredSubcommands = new ArrayList<SubCommand>();

    public SubCommand(final String label, final String permission, final boolean canConsoleUse, BigMoney plugin) {
        this.label = label;
        this.permission = permission;
        this.canConsoleUse = canConsoleUse;
        this.plugin = plugin;
    }

    public String getLabel() {
        return this.label;
    }

    public String getPermission() {
        return this.permission;
    }

    public void onCall(CommandSender sender, String[] args) {

        // Check if a sub command is being used
        if(args.length > 0) {
            for(SubCommand s : this.registeredSubcommands) {
                if(s.getLabel().equalsIgnoreCase(args[0])) {
                    s.onCall(sender, Arrays.copyOfRange(args, 1, args.length));
                    return;
                }
            }
        }

        // Check for permission
        if(!sender.hasPermission(this.permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            return;
        }

        // Console check
        if(!canConsoleUse && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be used by players.");
            return;
        }

        this.execute(sender, args, plugin);
    }

    public abstract void execute(CommandSender sender, String[] args, BigMoney plugin);

}
