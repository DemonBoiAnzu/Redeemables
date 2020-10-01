package com.github.steeldev.redeemables.listeners.commands.admin;

import com.github.steeldev.redeemables.Redeemables;
import com.github.steeldev.redeemables.main.RedeemSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RedeemAdminCommand implements CommandExecutor, TabCompleter {
    final Redeemables main = Redeemables.getInstance();
    private static final String[] SUB_COMMANDS = {"testgen", "createnew", "remove"};
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length > 0){
            if(strings[0].equalsIgnoreCase(SUB_COMMANDS[0])){
                commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&7Test Code: &a" + RedeemSystem.generateRedeemCode()));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            if(strings[0].equalsIgnoreCase(SUB_COMMANDS[2])){
                final List<String> completions = new ArrayList<>();
                ConfigurationSection codes = main.redeemCodes.getConfigurationSection("Codes");
                if(codes != null && codes.getKeys(false).size() > 0 && strings.length > 1) {
                    StringUtil.copyPartialMatches(strings[1], codes.getKeys(false), completions);
                    Collections.sort(completions);
                    return completions;
                }
                return new ArrayList<>();
            }
            if (strings.length > 1) {
                return new ArrayList<>();
            }
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(strings[0], Arrays.asList(SUB_COMMANDS), completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
