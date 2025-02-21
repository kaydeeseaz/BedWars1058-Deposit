package me.parsa.depositplugin;

import com.andrei1058.bedwars.api.BedWars;
import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.Listeners.EnderChestClick;
import me.parsa.depositplugin.Listeners.GameStartListener;
import me.parsa.depositplugin.Listeners.PlayerDeathListener;
import me.parsa.depositplugin.Listeners.PlayerJoin;
import me.parsa.depositplugin.Listeners.bedwars1058.AndreiDeathListener;
import me.parsa.depositplugin.Listeners.bedwars1058.AndreiStartListener;
import me.parsa.depositplugin.Listeners.bedwars2023.TomDeathListenera;
import me.parsa.depositplugin.Listeners.bedwars2023.TomStartListener;
import me.parsa.depositplugin.plugintypes.PluginType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DepositPlugin extends JavaPlugin {

    private static Logger logger;
    public static Level logLevel;
    public static DepositApi api;

    public static DepositPlugin plugin;
    //Main
    public static PluginType pluginType;

    public static BedWars bedWars;

    public static com.tomkeuper.bedwars.api.BedWars bedWars2023;

    @Override
    public void onEnable() {
        logger = getLogger();
        api= new API();
        plugin = this;

        Plugin bw1058 = Bukkit.getPluginManager().getPlugin("BedWars1058");
        Plugin bw2023 = Bukkit.getPluginManager().getPlugin("BedWars2023");

        getServer().getServicesManager().register(DepositApi.class, api, this, ServicePriority.Normal);

        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabling plugin");
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading version v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading configs");


        ArenasConfig.setup();
        ArenasConfig.get().options().copyDefaults(true);
        ArenasConfig.save();
        saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage("[Deposit] Registering events");
        Bukkit.getConsoleSender().sendMessage("[Deposit] Hooking into bw1085");


        if (bw1058 != null) {
            pluginType = PluginType.BEDWARS1058;
            getLogger().info("Detected BedWars1058.");
            bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
        } else if (bw2023 != null) {
            pluginType = PluginType.BEDWARS2023;
            getLogger().info("Detected BedWars2023.");
            bedWars2023 = Bukkit.getServicesManager().getRegistration(com.tomkeuper.bedwars.api.BedWars.class).getProvider();
        } else {
            getLogger().severe("Neither BedWars1058 nor BedWars2023 was found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new EnderChestClick(), this);
        if (pluginType == PluginType.BEDWARS2023) {
            getServer().getPluginManager().registerEvents(new TomStartListener(this, ArenasConfig.get()), this);
            getServer().getPluginManager().registerEvents(new TomDeathListenera(), this);
        }
        if (pluginType == PluginType.BEDWARS1058) {
            getServer().getPluginManager().registerEvents(new AndreiStartListener(this, ArenasConfig.get()), this);
            getServer().getPluginManager().registerEvents(new AndreiDeathListener(), this);
        }

        //getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        //getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabled plugin");
        String levelName = getConfig().getString("log-level", "INFO").toUpperCase();
        logLevel = Level.parse(levelName);
        logger.setLevel(logLevel);

        debug("Log level set to: " + logLevel);

    }

    @Override
    public void onDisable() {

    }
    public static void debug(String message) {
        if (logLevel.intValue() <= Level.FINE.intValue()) {
            logger.info("[DEBUG] " + message);
        }
    }

    public static void info(String message) {
        if (logLevel.intValue() <= Level.INFO.intValue()) {
            logger.info("[INFO] " + message);
        }
    }

    public static void warn(String message) {
        if (logLevel.intValue() <= Level.WARNING.intValue()) {
            logger.warning("[WARNING] " + message);
        }
    }

    public static void error(String message) {
        if (logLevel.intValue() <= Level.SEVERE.intValue()) {
            logger.severe("[ERROR] " + message);
        }
    }
}