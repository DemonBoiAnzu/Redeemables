package com.github.steeldev.redeemables;

import com.github.steeldev.redeemables.listeners.commands.admin.RedeemAdminCommand;
import com.github.steeldev.redeemables.listeners.commands.admin.RedeemReload;
import com.github.steeldev.redeemables.listeners.commands.user.RedeemCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Redeemables extends JavaPlugin {
    public static Redeemables instance;

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");

    public FileConfiguration config = null;
    public FileConfiguration redeemCodes = null;
    public Random rand = new Random();
    String configPath = getDataFolder() + "/Config.yml";
    public final File configFile = new File(configPath);
    String redeemCodesPath = getDataFolder() + "/RedeemCodes.yml";
    public final File redeemCodesFile = new File(redeemCodesPath);

    public static Redeemables getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        loadCustomConfigs();
        registerCommands();

        getLogger().info(colorize("&bRedeemables &2Enabled!"));
    }

    @Override
    public void onDisable() {
        getLogger().info(colorize("&bRedeemables &cDisabled!"));
    }

    public void loadCustomConfigs() {
        if (!configFile.exists())
            saveResource("Config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!redeemCodesFile.exists())
            saveResource("RedeemCodes.yml", false);
        redeemCodes = YamlConfiguration.loadConfiguration(redeemCodesFile);
    }

    public void registerCommands() {
        this.getCommand("redeemablesreload").setExecutor(new RedeemReload());
        this.getCommand("redeemablesadmin").setExecutor(new RedeemAdminCommand());
        this.getCommand("redeem").setExecutor(new RedeemCommand());
    }

    public String colorize(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = string.substring(0, matcher.start());
            final String after = string.substring(matcher.end());
            string = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public boolean chanceOf(int chance) {
        return rand.nextInt(100) < chance;
    }
}
