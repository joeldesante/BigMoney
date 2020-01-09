package com.yooogle.bigmoney.commands;

import com.yooogle.bigmoney.BigMoney;
import com.yooogle.bigmoney.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command implements CommandExecutor {

    private final String commandName;
    private final String[] commandNames;
    private final String permission;
    private final boolean canConsoleUse;
    private BigMoney plugin;
    private final ArrayList<SubCommand> registeredSubcommands = new ArrayList<SubCommand>();

    public Command(final String commandName, final String permission, final boolean canConsoleUse, BigMoney plugin) {
        this.commandName = commandName;
        this.commandNames = null;
        this.permission = permission;
        this.canConsoleUse = canConsoleUse;
        this.plugin = plugin;
        plugin.getCommand(this.commandName).setExecutor(this);
    }

    public Command(final String[] commandNames, final String permission, final boolean canConsoleUse, BigMoney plugin) {
        this.commandName = commandNames[0];
        this.commandNames = commandNames;
        this.permission = permission;
        this.canConsoleUse = canConsoleUse;
        this.plugin = plugin;
        plugin.getCommand(this.commandName).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if(this.commandNames != null) {
            if(!Util.ArrayContains(this.commandNames, command.getLabel())) {
                return true;
            }
        } else {
            if(!command.getLabel().equalsIgnoreCase(this.commandName)) {
                return true;
            }
        }

        // Check if a sub command is being used
        if(args.length > 0) {
            for(SubCommand s : this.registeredSubcommands) {
                if(s.getLabel().equalsIgnoreCase(args[0])) {
                    s.onCall(sender, Arrays.copyOfRange(args, 1, args.length));
                    return true;
                }
            }
        }

        if(!sender.hasPermission(this.permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            return true;
        }

        if(!canConsoleUse && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be used by players.");
            return true;
        }

        // Run any pre-execute code
        this.preExecute(sender, args, this.plugin);

        this.execute(sender, args, this.plugin);
        return true;
    }

    public void registerSubCommand(SubCommand subCommand) {
        this.registeredSubcommands.add(subCommand);
    }

    public void unregisterSubCommand(SubCommand subCommand) {
        this.registeredSubcommands.remove(subCommand);
    }

    public abstract void preExecute(CommandSender sender, String[] args, BigMoney plugin);
    public abstract void execute(CommandSender sender, String args[], BigMoney plugin);
}
