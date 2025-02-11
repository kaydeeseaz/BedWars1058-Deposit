package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameStartListener implements Listener {

    private final Plugin plugin;
    private final FileConfiguration config;
    boolean successGameAssgin = false;
    boolean succesGameState = false;

    public GameStartListener(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler
    public void onGameStart(GameStateChangeEvent event) {
        if (event.getNewState() == GameState.playing) {
            if (DepositPlugin.plugin.getConfig().getString("hologram-register-event").equalsIgnoreCase("GameStateChangeEvent")) {
                DepositPlugin.debug("TeamAssignEvent triggered");
                succesGameState = true;

                DepositPlugin.debug("Player: ");
                World world = event.getArena().getWorld();
                DepositPlugin.debug("World: " + (world != null ? world.getName() : "null"));

                if (world == null) {
                    Bukkit.getLogger().warning("World is null for player ");
                    return;
                }

                String worldName = world.getName();
                DepositPlugin.debug("World Name: " + worldName);
                String path = "worlds." + worldName + ".chestLocations";
                DepositPlugin.debug("Config path: " + path);

                if (!config.contains(path)) {
                    DepositPlugin.debug("First time loading the map, searching for chests");
                    List<String> chestLocations = new ArrayList<>();

                    for (Chunk chunk : world.getLoadedChunks()) {
                        DepositPlugin.debug("Checking chunk: " + chunk.getX() + ", " + chunk.getZ());
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < world.getMaxHeight(); y++) {
                                for (int z = 0; z < 16; z++) {
                                    Block block = chunk.getBlock(x, y, z);
                                    if (block.getType() == Material.ENDER_CHEST || block.getType() == Material.CHEST) {
                                        DepositPlugin.debug("Chest found at: " + block.getLocation());
                                        chestLocations.add(serializeLocation(block.getLocation()));
                                    }
                                }
                            }
                        }
                    }

                    config.set(path, chestLocations);
                    ArenasConfig.save();
                    DepositPlugin.debug("Chest locations saved to config");
                }

                List<String> chestLocations = config.getStringList(path);
                DepositPlugin.debug("Loaded " + chestLocations.size() + " chest locations from config");

                String[] lines = {
                        ChatColor.GRAY + "DEPOSIT.",
                        ChatColor.GRAY + "PUNCH TO"
                };

                for (String locString : chestLocations) {
                    DepositPlugin.debug("Processing chest location: " + locString);
                    Location chestLocation = deserializeLocation(locString, world);
                    Location baseLocation = chestLocation.add(0.5, 0.9, 0.5);

                    for (int i = 0; i < lines.length; i++) {
                        Location hologramLocation = baseLocation.clone().add(0, 0.3 * i, 0);
                        DepositPlugin.debug("Spawning hologram at: " + hologramLocation);

                        ArmorStand hologram = (ArmorStand) world.spawnEntity(hologramLocation, EntityType.ARMOR_STAND);
                        hologram.setVisible(false);
                        hologram.setMarker(true);
                        hologram.setCustomName(lines[i]);
                        hologram.setCustomNameVisible(true);
                        hologram.setGravity(false);
                        DepositPlugin.debug("Hologram created with text: " + lines[i]);
                    }
                }
            } else {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if (!successGameAssgin && DepositPlugin.plugin.getConfig().getString("hologram-register-event").equalsIgnoreCase("TeamAssignEvent")) {
                            event.getArena().getPlayers().forEach(player -> {
                                if (player.isOp()) {
                                    player.sendMessage(ChatColor.RED + "It looks like your event for registering holograms didn't work, you can make it work with changing hologram-register-event value to GameStateChangeEvent");
                                }
                            });
                        }
                    }
                }.runTaskLaterAsynchronously(DepositPlugin.plugin, 20L);
            }

        }

    }

    private String serializeLocation(Location location) {
        DepositPlugin.debug("Serializing location: " + location);
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private Location deserializeLocation(String locString, World world) {
        DepositPlugin.debug("Deserializing location string: " + locString);
        String[] parts = locString.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int z = Integer.parseInt(parts[2]);
        Location location = new Location(world, x, y, z);
        DepositPlugin.debug("Deserialized location: " + location);
        return location;
    }

    @EventHandler
    public void onGameAssgin(TeamAssignEvent event) {
        if (DepositPlugin.plugin.getConfig().getString("hologram-register-event").equalsIgnoreCase("TeamAssignEvent")) {
            DepositPlugin.debug("TeamAssignEvent triggered");
            successGameAssgin = true;
            Player player = event.getPlayer();
            DepositPlugin.debug("Player: " + player.getName());
            World world = player.getWorld();
            DepositPlugin.debug("World: " + (world != null ? world.getName() : "null"));

            if (world == null) {
                Bukkit.getLogger().warning("World is null for player " + player.getName());
                return;
            }

            String worldName = world.getName();
            DepositPlugin.debug("World Name: " + worldName);
            String path = "worlds." + worldName + ".chestLocations";
            DepositPlugin.debug("Config path: " + path);

            if (!config.contains(path)) {
                DepositPlugin.debug("First time loading the map, searching for chests");
                List<String> chestLocations = new ArrayList<>();

                for (Chunk chunk : world.getLoadedChunks()) {
                    DepositPlugin.debug("Checking chunk: " + chunk.getX() + ", " + chunk.getZ());
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < world.getMaxHeight(); y++) {
                            for (int z = 0; z < 16; z++) {
                                Block block = chunk.getBlock(x, y, z);
                                if (block.getType() == Material.ENDER_CHEST || block.getType() == Material.CHEST) {
                                    DepositPlugin.debug("Chest found at: " + block.getLocation());
                                    chestLocations.add(serializeLocation(block.getLocation()));
                                }
                            }
                        }
                    }
                }

                config.set(path, chestLocations);
                plugin.saveConfig();
                DepositPlugin.debug("Chest locations saved to config");
            }

            List<String> chestLocations = config.getStringList(path);
            DepositPlugin.debug("Loaded " + chestLocations.size() + " chest locations from config");

            String[] lines = {
                    ChatColor.GRAY + "DEPOSIT.",
                    ChatColor.GRAY + "PUNCH TO"
            };

            for (String locString : chestLocations) {
                DepositPlugin.debug("Processing chest location: " + locString);
                Location chestLocation = deserializeLocation(locString, world);
                Location baseLocation = chestLocation.add(0.5, 0.9, 0.5);

                for (int i = 0; i < lines.length; i++) {
                    Location hologramLocation = baseLocation.clone().add(0, 0.3 * i, 0);
                    DepositPlugin.debug("Spawning hologram at: " + hologramLocation);

                    ArmorStand hologram = (ArmorStand) world.spawnEntity(hologramLocation, EntityType.ARMOR_STAND);
                    hologram.setVisible(false);
                    hologram.setMarker(true);
                    hologram.setCustomName(lines[i]);
                    hologram.setCustomNameVisible(true);
                    hologram.setGravity(false);
                    DepositPlugin.debug("Hologram created with text: " + lines[i]);
                }
            }
        } else {
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (!succesGameState && DepositPlugin.plugin.getConfig().getString("hologram-register-event").equalsIgnoreCase("GameStateChangeEvent")) {
                        event.getArena().getPlayers().forEach(player -> {
                            if (player.isOp()) {
                                player.sendMessage(ChatColor.RED + "It looks like your event for registering holograms didn't work, you can make it work with changing hologram-register-event value to TeamAssignEvent");
                            }
                        });
                    }
                }
            }.runTaskLaterAsynchronously(DepositPlugin.plugin, 10L);
        }

    }

}
