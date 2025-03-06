package me.parsa.depositplugin;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import me.parsa.depositplugin.Listeners.ChestListener;
import me.parsa.depositplugin.Listeners.GameListener;
import me.parsa.depositplugin.Utils.Cache;
import me.parsa.depositplugin.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class DepositPlugin extends JavaPlugin {

    public static FileConfiguration configuration;

    public static DepositPlugin plugin;
    //Main
    public static BedWars bedWars;

    @Override
    public void onEnable() {
        plugin = this;

        bedWars = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabling plugin");
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading version v" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("[Deposit] Loading configs");
        String packageName = getServer().getClass().getPackage().getName();
        String version = packageName.split("\\.")[3];



        saveDefaultConfig();
        configuration = getConfig();

        Bukkit.getConsoleSender().sendMessage("[Deposit] Registering events");
        Bukkit.getConsoleSender().sendMessage("[Deposit] Hooking into bw1058");
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().severe("BedWars1058 was not found. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Cache.loadCache();
            // await all arenas to load
            for (IArena arena : Arena.getArenas()) {
                String worldName = arena.getWorldName();
                if(Cache.cache.isSet("arenas."+worldName)) {
                    ArrayList<Location> locations = new ArrayList<>();
                    for (int i = 1; i < 30; i++) {
                        String str = "arenas."+worldName+".location"+i;

                        if(Cache.cache.isSet(str)) {
                            Location location = (Location) Cache.cache.get(str);
                            locations.add(location);
                        } else break;
                    }
                    System.out.println("Loaded "+locations.size()+" for arena"+worldName);
                    Utils.chestLocations.put(worldName, locations);
                } else {
                    Utils.calculateChestLocations(arena);
                }
            }
        }, 20*20);
        getServer().getPluginManager().registerEvents(new ChestListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getConsoleSender().sendMessage("[Deposit] Enabled plugin");

    }

    @Override
    public void onDisable() {
        Cache.save();
    }
}