package me.parsa.depositplugin;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.server.VersionSupport;
import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.Listeners.EnderChestClick;
import me.parsa.depositplugin.Listeners.GameStartListener;
import me.parsa.depositplugin.Listeners.PlayerDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DepositPlugin extends JavaPlugin {

    private static Logger logger;
    public static Level logLevel;
    public static DepositApi api;

    public static DepositPlugin plugin;

    public static BedWars bedWars;

    @Override
    public void onEnable() {
        logger = getLogger();
        api= new API();
        plugin = this;

        bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
        getServer().getServicesManager().register(DepositApi.class, api, this, ServicePriority.Normal);

        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabling plugin");
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading version v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading configs");
        ArenasConfig.setup();
        ArenasConfig.get().options().copyDefaults(true);
        ArenasConfig.save();
        saveDefaultConfig();
        Bukkit.getConsoleSender().sendMessage("[Deposit] Registering events");
        getServer().getPluginManager().registerEvents(new EnderChestClick(), this);
        getServer().getPluginManager().registerEvents(new GameStartListener(this, ArenasConfig.get()), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabled plugin");
        String levelName = getConfig().getString("log-level", "INFO").toUpperCase();
        logLevel = Level.parse(levelName);
        logger.setLevel(logLevel);

        debug("Log level set to: " + logLevel);
//        System.out.println("Log level set to: " + logLevel);
//        debug("This is a DEBUG message.");
//        info("This is an INFO message.");
//        warn("This is a WARNING message.");
//        error("This is an ERROR message.");

    }

    @Override
    public void onDisable() {

    }
    public static void debug(String message) {
        if (logLevel.intValue() <= Level.FINE.intValue()) {
            //System.out.println("DEBUG CALLED !");
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
