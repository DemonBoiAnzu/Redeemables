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

public class Redeemables extends JavaPlugin {
    public static Redeemables instance;

    public FileConfiguration config = null;
    public FileConfiguration redeemCodes = null;
    public Random rand = new Random();
    String configPath = getDataFolder() + "/Configuration/Config.yml";
    public final File configFile = new File(configPath);
    String redeemCodesPath = getDataFolder() + "/Data/RedeemCodes.yml";
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
            saveResource("Configuration/Config.yml", false);
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!redeemCodesFile.exists())
            saveResource("Data/RedeemCodes.yml", false);
        redeemCodes = YamlConfiguration.loadConfiguration(redeemCodesFile);
    }

    public void registerCommands() {
        this.getCommand("redeemablesreload").setExecutor(new RedeemReload());
        this.getCommand("redeemablesadmin").setExecutor(new RedeemAdminCommand());
        this.getCommand("redeem").setExecutor(new RedeemCommand());
    }

    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public boolean chanceOf(int chance) {
        return rand.nextInt(100) < chance;
    }
}
