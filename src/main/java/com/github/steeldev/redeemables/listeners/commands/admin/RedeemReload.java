package com.github.steeldev.redeemables.listeners.commands.admin;

import com.github.steeldev.redeemables.Redeemables;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RedeemReload implements CommandExecutor {
    final Redeemables main = Redeemables.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        main.loadCustomConfigs();
        commandSender.sendMessage(main.colorize(String.format("%s&aSuccessfully reloaded configurations!", main.config.getString("Prefix"))));
        return true;
    }
}
