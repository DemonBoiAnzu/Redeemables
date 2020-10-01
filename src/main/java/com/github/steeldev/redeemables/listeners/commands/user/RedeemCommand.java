package com.github.steeldev.redeemables.listeners.commands.user;

import com.github.steeldev.redeemables.Redeemables;
import com.github.steeldev.redeemables.main.RedeemSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedeemCommand implements CommandExecutor, TabCompleter {
    final Redeemables main = Redeemables.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cOnly players may execute this command!"));
            return true;
        }
        if (strings.length > 0) {
            Player player = (Player) commandSender;
            String code = strings[0];

            RedeemSystem.RedeemState attemptResult = RedeemSystem.attemptRedeemCode(code, player);

            switch (attemptResult) {
                case CODE_INVALID:
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a valid code! Either you typed it wrong, or this code is no longer valid!"));
                    break;
                case CANT_BE_REDEEMED:
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cYou are unable to redeem this code at this time! Most likely, the code has ran out of uses!"));
                    break;
                case ALREADY_REDEEMED:
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cYou have already redeemed this code!"));
                    break;
                case ERROR:
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cSomething went wrong when attempting to redeem this code! Please contact an admin!"));
                    break;
                case INVENTORY_FULL:
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cYour inventory is full! You cannot redeem this code with a full inventory."));
                    break;
                case SUCCESS:
                    String type = main.redeemCodes.getString("Codes." + code + ".Type");
                    String display = main.redeemCodes.getString("Codes." + code + ".RedeemDisplay");

                    int amount = main.redeemCodes.getInt("Codes." + code + ".Amount");

                    List<String> redeemedBy = main.redeemCodes.getStringList("Codes." + code + ".RedeemedBy");

                    ItemStack item = main.redeemCodes.getItemStack("Codes." + code + ".Item");

                    String action = main.config.getString("RedeemTypes." + type + ".Action");

                    String cmd = (main.config.getString("RedeemTypes." + type + ".Command") == null) ? "" : main.config.getString("RedeemTypes." + type + ".Command").replaceAll("PLAYER", player.getName()).replaceAll("AMOUNT", String.valueOf(amount)).replaceAll("/", "");

                    if (action.equalsIgnoreCase("GiveItem")) {
                        item.setAmount(amount);

                        player.getInventory().addItem(item);
                    } else if (action.equalsIgnoreCase("RunCommand")) {
                        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                        Bukkit.dispatchCommand(console, cmd);
                    }
                    redeemedBy.add(player.getUniqueId().toString());
                    main.redeemCodes.set("Codes." + code + ".RedeemedBy", redeemedBy);
                    try {
                        main.redeemCodes.save(main.redeemCodesFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&aYou have successfully redeemed &e" + display + "&a! Congratulations!"));
                    break;
            }
        } else {
            commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a code!"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
