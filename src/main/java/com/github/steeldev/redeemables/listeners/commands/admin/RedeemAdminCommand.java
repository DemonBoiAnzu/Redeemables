package com.github.steeldev.redeemables.listeners.commands.admin;

import com.github.steeldev.redeemables.Redeemables;
import com.github.steeldev.redeemables.systems.RedeemSystem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RedeemAdminCommand implements CommandExecutor, TabCompleter {
    private static final String[] SUB_COMMANDS = {"testgen", "createnew", "remove", "list", "announce"};
    final Redeemables main = Redeemables.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase(SUB_COMMANDS[0])) {
                try {
                    int length = 0;
                    String type = main.config.getString("Code.Type");
                    switch (type.toLowerCase()) {
                        case "short":
                            length = 4;
                            break;
                        case "medium":
                            length = 8;
                            break;
                        case "long":
                            length = 12;
                            break;
                    }
                    commandSender.sendMessage(main.colorize(String.format("%sTEST CODE: &a%s &7(Type: &2%s&7, Length: &2%s&7)", main.config.getString("Prefix"), RedeemSystem.generateRedeemCode(), type, length)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (strings[0].equalsIgnoreCase(SUB_COMMANDS[1])) {
                if (strings.length > 1) {
                    String type = strings[1];
                    if (main.config.get("RedeemTypes." + type) == null) {
                        commandSender.sendMessage(main.colorize(String.format("%s&cExpected a valid type!", main.config.getString("Prefix"))));
                        return true;
                    }
                    int maxRedeems;
                    int amount;
                    String display;
                    String code = null;
                    try {
                        code = RedeemSystem.generateRedeemCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ItemStack item = null;

                    if (strings.length > 2) {
                        if (strings[2].equalsIgnoreCase("inf") || strings[2].equalsIgnoreCase("infinite") || strings[2].equalsIgnoreCase("0")) {
                            maxRedeems = 0;
                        } else {
                            try {
                                maxRedeems = Integer.parseInt(strings[2]);
                            } catch (NumberFormatException nfe) {
                                commandSender.sendMessage(main.colorize(String.format("%s&cExpected a number.", main.config.getString("Prefix"))));
                                return true;
                            }
                        }
                        if (strings.length > 3) {
                            try {
                                amount = Integer.parseInt(strings[3]);
                            } catch (NumberFormatException nfe) {
                                commandSender.sendMessage(main.colorize(String.format("%s&cExpected a number.", main.config.getString("Prefix"))));
                                return true;
                            }
                            if (amount <= 0) {
                                commandSender.sendMessage(main.colorize(String.format("%s&cAmount must be above 0!", main.config.getString("Prefix"))));
                                return true;
                            }
                            String action = main.config.getString("RedeemTypes." + type + ".Action");
                            int max = (action.equalsIgnoreCase("GiveItem")) ? 64 : 999999999;
                            if (amount > max) {
                                commandSender.sendMessage(main.colorize(String.format("%s&cAmount cannot exceed " + max + "!", main.config.getString("Prefix"))));
                                return true;
                            }
                            if (strings.length > 4) {
                                display = strings[4].replace("-", " ");
                                String action1 = main.config.getString("RedeemTypes." + type + ".Action");
                                if (action1.equalsIgnoreCase("GiveItem")) {
                                    item = ((Player) commandSender).getInventory().getItemInMainHand().clone();
                                    if (item.getType().equals(Material.AIR)) {
                                        commandSender.sendMessage(main.colorize(String.format("%s&cPlease hold the item you wish to set as the reward!", main.config.getString("Prefix"))));
                                        return true;
                                    }
                                    amount = item.getAmount();
                                }

                                try {
                                    RedeemSystem.createRedeem(type, item, amount, maxRedeems, code, display);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String itDisp = (item == null) ? "None" : (item.getItemMeta() == null || item.getItemMeta().getDisplayName().isEmpty()) ? item.getType().toString() : item.getItemMeta().getDisplayName();

                                String am = (maxRedeems == 0) ? "Infinite" : String.valueOf(maxRedeems);
                                commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&aSuccessfully created a Redeem for &e" + display + "&a! &8(&7Type: " + type + ", Amount: " + amount + ", MaxRedeems: " + am + ", Item: " + itDisp + "&8) &aWith the code: &6" + code + "&a!"));
                            } else {
                                commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a display!"));
                            }
                        } else {
                            commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide an amount!"));
                        }
                    } else {
                        commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide the max amount of times this code can be redeemed!"));
                    }
                } else {
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a type!"));
                }
            } else if (strings[0].equalsIgnoreCase(SUB_COMMANDS[2])) {
                if (strings.length > 1) {
                    String code = strings[1];
                    if (main.redeemCodes.get("Codes." + code) != null) {
                        String display = main.redeemCodes.getString("Codes." + code + ".RedeemDisplay");
                        main.redeemCodes.set("Codes." + code, null);
                        try {
                            main.redeemCodes.save(main.redeemCodesFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&aSuccessfully removed the Redeem &e" + display + "&a! &8(&7" + code + "&8)"));
                    } else {
                        commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a valid code!"));
                    }
                } else {
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a code!"));
                }
            } else if (strings[0].equalsIgnoreCase(SUB_COMMANDS[3])) {
                commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "All created codes:"));
                List<String> allCodes = new ArrayList<>();

                ConfigurationSection codes = main.redeemCodes.getConfigurationSection("Codes");
                if (codes != null) {
                    allCodes.addAll(codes.getKeys(false));
                }

                if (allCodes.size() > 0) {
                    int ind = 1;
                    for (String code : allCodes) {
                        String display = main.redeemCodes.getString("Codes." + code + ".RedeemDisplay");
                        String type = main.redeemCodes.getString("Codes." + code + ".Type");
                        int amount = main.redeemCodes.getInt("Codes." + code + ".Amount");
                        int maxRedeems = main.redeemCodes.getInt("Codes." + code + ".MaxRedeems");
                        List<String> redeemedBy = main.redeemCodes.getStringList("Codes." + code + ".RedeemedBy");
                        String action = main.config.getString("RedeemTypes." + type + ".Action");

                        ItemStack item = main.redeemCodes.getItemStack("Codes." + code + ".Item");

                        StringBuilder finalMsg = new StringBuilder().append("&7[&2" + ind + "&7] &a" + code + " &7(Name: " + display + ", Type: " + type + ", Action: " + action);

                        if (action.equalsIgnoreCase("GiveItem")) {
                            String itDisp = (item == null) ? "None" : (item.getItemMeta() == null || item.getItemMeta().getDisplayName().isEmpty()) ? item.getType().toString() : item.getItemMeta().getDisplayName();
                            finalMsg.append(", Item: " + itDisp);
                        }

                        String am = (maxRedeems == 0) ? "Infinite" : String.valueOf(maxRedeems);

                        finalMsg.append(", Amount: " + amount + ", Redeems: " + redeemedBy.size() + "/" + am + ")");

                        commandSender.sendMessage(main.colorize(finalMsg.toString()));
                        ind++;
                    }
                } else {
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cThere are no Redeems."));
                }
            } else if (strings[0].equalsIgnoreCase(SUB_COMMANDS[4])) {
                if (strings.length > 1) {
                    String code = strings[1];
                    if (main.redeemCodes.get("Codes." + code) != null) {
                        String display = main.redeemCodes.getString("Codes." + code + ".RedeemDisplay");
                        String type = main.redeemCodes.getString("Codes." + code + ".Type");
                        int amount = main.redeemCodes.getInt("Codes." + code + ".Amount");
                        int maxRedeems = main.redeemCodes.getInt("Codes." + code + ".MaxRedeems");
                        List<String> redeemedBy = main.redeemCodes.getStringList("Codes." + code + ".RedeemedBy");
                        String remainingRedeems = (maxRedeems == 0) ? "Infinite" : String.valueOf(maxRedeems - redeemedBy.size());

                        StringBuilder finalMsg = new StringBuilder().append(main.config.getString("Prefix") + "&2Redeem this code: &a" + code + " &2For &a" + amount + " &2of " + display + "!");

                        if (!remainingRedeems.equalsIgnoreCase("Infinite"))
                            finalMsg.append(" &a" + remainingRedeems + " &2Redeems remaining until the code is no longer valid!");

                        main.getServer().broadcastMessage(main.colorize(finalMsg.toString()));
                    } else {
                        commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a valid code!"));
                    }
                } else {
                    commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cPlease provide a code!"));
                }
            } else {
                commandSender.sendMessage(main.colorize(main.config.getString("Prefix") + "&cYou must provide a sub command!"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase(SUB_COMMANDS[2]) || strings[0].equalsIgnoreCase(SUB_COMMANDS[4])) {
                final List<String> completions = new ArrayList<>();
                ConfigurationSection codes = main.redeemCodes.getConfigurationSection("Codes");
                if (codes != null && codes.getKeys(false).size() > 0 && strings.length > 1) {
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
