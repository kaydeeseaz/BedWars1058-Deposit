package me.parsa.depositplugin.Utils;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.arena.Arena;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.*;

public class Utils {
    //                  ArenaWorldName, Location
    public static HashMap<String, ArrayList<Location>> chestLocations = new HashMap<>();
    public static HashMap<IArena, ArrayList<ArmorStand>> holograms = new HashMap<>();

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();
        int startX = location.getBlockX() - radius;
        int endX = location.getBlockX() + radius;
        int startY = Math.max(0, location.getBlockY() - radius); // Avoid going below Y=0
        int endY = Math.min(world.getMaxHeight() - 1, location.getBlockY() + radius); // Avoid going above world height
        int startZ = location.getBlockZ() - radius;
        int endZ = location.getBlockZ() + radius;

        // Optional: Sphere check for true radius
        int radiusSquared = radius * radius;

        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                for (int z = startZ; z <= endZ; ++z) {
                    // Sphere check: Comment out if you want a cube instead
                    Block block = world.getBlockAt(x, y, z);

                    if (location.distanceSquared(block.getLocation()) > radiusSquared) continue;

                    if (block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    public static void spawnHolograms(IArena arena) {
        ArrayList<ArmorStand> armorStands = new ArrayList<>();
        for (Location bLocation : chestLocations.getOrDefault(arena.getWorldName(), new ArrayList<>())) {
            List<String> holograms = DepositPlugin.configuration.getStringList("holograms");
            Collections.reverse(holograms);
            armorStands.addAll(createHologram(bLocation, holograms));
        }
        holograms.put(arena, armorStands);
    }
    public static void deleteHolograms(IArena arena) {
        for (ArmorStand armorStand : holograms.getOrDefault(arena, new ArrayList<>())) {
            armorStand.remove();
        }
    }

    public static ArrayList<ArmorStand> createHologram(Location chestLocation, List<String> lines) {
        ArrayList<ArmorStand> stands = new ArrayList<>();
        Location baseLocation = chestLocation.clone().add(0.5, 0.9, 0.5);

        for (int i = 0; i < lines.size(); i++) {
            Location hologramLocation = baseLocation.clone().add(0, DepositPlugin.configuration.getDouble("distance") * i, 0);

            ArmorStand hologram = (ArmorStand) chestLocation.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

            hologram.setVisible(false);
            hologram.setMarker(true);
            hologram.setCustomName(lines.get(i));
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
            stands.add(hologram);
        }
        return stands;
    }
    public static void calculateChestLocations(IArena arena) {
        String worldName = arena.getWorldName();
        ArrayList<Location> locations = new ArrayList<>();
        for (ITeam team : arena.getTeams()) {
                for (Block nearbyBlock : getNearbyBlocks(team.getSpawn(), arena.getIslandRadius())) {
                    if(
                            nearbyBlock.getType() == Material.CHEST ||
                            nearbyBlock.getType() == Material.ENDER_CHEST) {

                        locations.add(nearbyBlock.getLocation());

                    }
                }
                int i = 1;
                for (Location location : locations) {
                    Cache.cache.set("arenas."+worldName+".location"+i, location);
                    i++;
                }

        }
        chestLocations.put(worldName, locations);

    }


}
