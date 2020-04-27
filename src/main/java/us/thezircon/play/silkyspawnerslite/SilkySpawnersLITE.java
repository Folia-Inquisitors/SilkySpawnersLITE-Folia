package us.thezircon.play.silkyspawnerslite;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import us.thezircon.play.silkyspawnerslite.commands.CheckSpawner;
import us.thezircon.play.silkyspawnerslite.commands.SilkySpawner.Silky;
import us.thezircon.play.silkyspawnerslite.events.breakSpawner;
import us.thezircon.play.silkyspawnerslite.events.placeSpawner;
import us.thezircon.play.silkyspawnerslite.events.playerJoin;
import us.thezircon.play.silkyspawnerslite.events.renameSpawner;
import us.thezircon.play.silkyspawnerslite.nms.*;
import us.thezircon.play.silkyspawnerslite.utils.Metrics;
import us.thezircon.play.silkyspawnerslite.utils.UpdateConfigs;
import us.thezircon.play.silkyspawnerslite.utils.VersionChk;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class SilkySpawnersLITE extends JavaPlugin {

    private nmsHandler nms;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    public boolean UP2Date = true;

    @Override
    public void onEnable() {
        //Create & Update Configs
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (configFile.exists()) {UpdateConfigs.config();}
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        createLangConfig();
        UpdateConfigs.lang();

        //NMS Setup & Checks
        setNMSVersion();

        //Check for vault
        if (!setupEconomy() ) {
            log.warning(String.format("[%s] - Some features will be disabled due to not having Vault installed!", getDescription().getName()));
            if (getConfig().getBoolean("chargeOnBreak.enabled")) {
                getConfig().set("chargeOnBreak.enabled", false);
                saveConfig();
                reloadConfig();
            }
        }

        //Commands
        getCommand("silky").setExecutor(new Silky());
        getCommand("checkspawner").setExecutor(new CheckSpawner());

        //Events & Listeners
        getServer().getPluginManager().registerEvents(new breakSpawner(), this);
        getServer().getPluginManager().registerEvents(new placeSpawner(), this);
        getServer().getPluginManager().registerEvents(new renameSpawner(), this);
        getServer().getPluginManager().registerEvents(new playerJoin(), this);

        //bStats
        Metrics metrics = new Metrics(this, 6579);

        //Version Check
        try {
        VersionChk.checkVersion(this.getName(),76103);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void setNMSVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        if (version.contains("1_15")) {
            nms = new NMS_1_15();
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&bSilky&6Spawners&7] &7Loading &cNMS&7 version &e1.15"));
        } else if (version.contains("1_14")) {
            nms = new NMS_1_14();
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&bSilky&6Spawners&7] &7Loading &cNMS&7 version &e1.14"));
        } else if (version.contains("1_13")) {
            nms = new NMS_1_13();
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&bSilky&6Spawners&7] &7Loading &cNMS&7 version &e1.13"));
        } else if (version.contains("1_12")) {
            nms = new NMS_1_12();
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&bSilky&6Spawners&7] &7Loading &cNMS&7 version &e1.12"));
        } else {
            nms = new NMS_1_15();
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&bSilky&6Spawners&7] &4Unknown Version - Trying Latest &7Loading &cNMS&7 version &e1.15"));
        }
    }

    public nmsHandler getNMS() {
        return  nms;
    }

    //Lang.yml
    private File customLangFile;
    private FileConfiguration customLangConfig;

    private void createLangConfig() {
        customLangFile = new File(getDataFolder(), "lang.yml");
        if (!customLangFile.exists()) {
            customLangFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        customLangConfig= new YamlConfiguration();
        try {
            customLangConfig.load(customLangFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getLangConfig() {
        return this.customLangConfig;
    }

    public void langReload(){
        customLangConfig = YamlConfiguration.loadConfiguration(customLangFile);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
