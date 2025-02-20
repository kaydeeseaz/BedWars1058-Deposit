package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.arena.Arena;
import com.sun.tools.doclint.HtmlTag;
import me.parsa.depositapi.Events.PlayerDepositEvent;
import me.parsa.depositapi.Types.DepositType;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.andrei1058.bedwars.api.language.Language.getMsg;

public class EnderChestClick implements Listener {

    private static final HashSet<UUID> selectionModePlayers = new HashSet<>();

    public static void addPlayerToSelectionMode(Player player) {
        selectionModePlayers.add(player.getUniqueId());
    }


    public static void removePlayerFromSelectionMode(Player player) {
        selectionModePlayers.remove(player.getUniqueId());
    }
    private void createHologram(Location chestLocation, String ... lines) {
        Location baseLocation = chestLocation.clone().add(0.5, 0.9, 0.5);

        for (int i = 0; i < lines.length; i++) {
            Location hologramLocation = baseLocation.clone().add(0, 0.3 * i, 0);
            ArmorStand hologram = (ArmorStand) chestLocation.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);

            hologram.setVisible(false);
            hologram.setMarker(true);
            hologram.setCustomName(lines[i]);
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
        }
    }
    private void deleteHologram(Location chestLocation) {

        Location baseLocation = chestLocation.clone().add(0.5, 0.9, 0.5);


        for (Entity entity : chestLocation.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                ArmorStand hologram = (ArmorStand) entity;

                if (hologram.getLocation().distance(baseLocation) < 1) {
                    hologram.remove();
                }
            }
        }
    }


    @EventHandler
    public void onPlayerLeftClickEnderChest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (DepositPlugin.bedWars.isInSetupSession(p.getUniqueId())) {
            if (p.isSneaking()) {
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Block block = e.getClickedBlock();
                    if (block == null) return;


                    if (block.getType() == Material.ENDER_CHEST || block.getType() == Material.CHEST) {
                        String chestLocation = block.getLocation().getBlockX() + "," +
                                block.getLocation().getBlockY() + "," +
                                block.getLocation().getBlockZ();
                        String path = "worlds." + p.getWorld().getName() + ".chestLocations";
                        List<String> chestLocations = ArenasConfig.get().getStringList(path);

                        if (!chestLocations.contains(chestLocation)) {
                            chestLocations.add(chestLocation);
                            ArenasConfig.get().set(path, chestLocations);
                            ArenasConfig.save();


                            p.sendMessage(ChatColor.GREEN + "Chest location added: " + chestLocation);
                            List<String> list = ArenasConfig.get().getStringList("worlds." + p.getWorld().getName() + ".chestLocations");
                            int size = list.size();
                            String the = "§6 ▪ §7ChestLocations: " + ((size == 0) ? "&c&l(NOT SET) " : (size < 16) ? "&e&l(NOT PROPERLY SET) " : (size == 16) ? "&a&l(SET) " : "&c&l(NOT SET) ") + "§8 - §eShift + Left-Click ";
                            p.sendMessage(the);
                            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            removePlayerFromSelectionMode(p);
                            createHologram(block.getLocation(), ChatColor.GOLD + "Deposit Chest Set");
                        } else {
                            p.sendMessage(ChatColor.YELLOW + "This chest is already set!");
                            removePlayerFromSelectionMode(p);

                        }
                        e.setCancelled(true);
                    }
                }
            }
        } else {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
                    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
                    DepositPlugin.debug(p.getName() + " left-clicked on an Ender Chest!");
                    if (bedwarsAPI.getArenaUtil().isPlaying(p)) {
                        DepositPlugin.debug("Player is playing and clicked ");
                        ItemStack item = p.getItemInHand();
                        Material itemMat = item.getType();
                        Inventory enderChest = p.getEnderChest();
                        if (item.getType() != Material.AIR &&
                                item.getType() != Material.WOOD_SWORD &&
                                item.getType() != Material.IRON_SWORD &&
                                item.getType() != Material.DIAMOND_SWORD &&
                                item.getType() != Material.STONE_SWORD &&
                                item.getType() != Material.COMPASS &&
                                item.getType() != Material.WOOD_PICKAXE &&
                                item.getType() != Material.STONE_PICKAXE &&
                                item.getType() != Material.IRON_PICKAXE &&
                                item.getType() != Material.DIAMOND_PICKAXE &&
                                item.getType() != Material.GOLD_PICKAXE &&
                                item.getType() != Material.WOOD_AXE &&
                                item.getType() != Material.STONE_AXE &&
                                item.getType() != Material.IRON_AXE &&
                                item.getType() != Material.DIAMOND_AXE &&
                                item.getType() != Material.GOLD_AXE &&
                                item.getType() != Material.SHEARS) {
                            if (enderChest.firstEmpty() == -1) {
                                return;
                            } else {
                                PlayerDepositEvent playerDepositEvent = new PlayerDepositEvent(p, DepositType.ENDER_CHEST, e.getClickedBlock());
                                Bukkit.getPluginManager().callEvent(playerDepositEvent);
                                if (!playerDepositEvent.isCancelled()) {
                                    final int[] itemCount = {0};
                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            if (DepositPlugin.plugin.getConfig().getBoolean("deposit-whole-itemstack")) {
                                                for (ItemStack itemStack : p.getInventory().getContents()) {
                                                    if (itemStack == null) {
                                                        continue;
                                                    }
                                                    if (itemStack.getType() == itemMat) {
                                                        itemCount[0] = itemStack.getAmount() + itemCount[0];
                                                        p.getInventory().removeItem(item);
                                                        enderChest.addItem(item);
                                                    }
                                                }
                                                if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.GOLD_INGOT) {
                                                    String itemName = Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                                                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                                            .collect(Collectors.joining(" "));
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + itemCount[0] + " " + ChatColor.GOLD + itemName + ChatColor.GRAY + " to the" + ChatColor.LIGHT_PURPLE + " Ender Chest");
                                                } else {
                                                    String itemName = Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                                                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                                            .collect(Collectors.joining(" "));
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + itemCount[0] + " " + ChatColor.WHITE + itemName + ChatColor.GRAY + " to the" + ChatColor.LIGHT_PURPLE + " Ender Chest");
                                                }
                                                p.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + p.getName() + " deposited " + ChatColor.WHITE + item.getAmount() + "x " + item.getType() + ChatColor.GOLD + " to the ender chest");
                                                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);


                                            } else {
                                                if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.GOLD_INGOT) {
                                                    String itemName = Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                                                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                                            .collect(Collectors.joining(" "));
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + item.getAmount() + " " + ChatColor.GOLD + itemName + ChatColor.GRAY + " to the" + ChatColor.LIGHT_PURPLE + " Ender Chest");
                                                } else {
                                                    String itemName = Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                                                            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                                            .collect(Collectors.joining(" "));
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + item.getAmount() + " " + ChatColor.WHITE + itemName + ChatColor.GRAY + " to the" + ChatColor.LIGHT_PURPLE + " Ender Chest");
                                                }
                                                p.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + p.getName() + " deposited " + ChatColor.WHITE + item.getAmount() + "x " + item.getType() + ChatColor.GOLD + " to the ender chest");
                                                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                                                p.getInventory().removeItem(item);
                                                enderChest.addItem(item);
                                            }

                                        }
                                    }.runTaskAsynchronously(DepositPlugin.plugin);


//                                p.getInventory().removeItem(item);

                                } else {
                                    DepositPlugin.warn("Player deposit event has been canceled");
                                }
                            }


                        }

                    }

                    e.setCancelled(true);

                }
            }
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

                if (e.getClickedBlock().getType() == Material.CHEST) {
                    BedWars bedwarsAPI = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();
                    DepositPlugin.debug(p.getName() + " left-clicked on a Chest!");

                    if (bedwarsAPI.getArenaUtil().isPlaying(p)) {
                        DepositPlugin.debug("Player is playing and clicked ");
                        ItemStack item = p.getItemInHand();
                        Material itemMat = item.getType();
                        String itemName = Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                .collect(Collectors.joining(" "));

                        Block clickedBlock = e.getClickedBlock();
                        Chest chest = (Chest) clickedBlock.getState();
                        Inventory chestInventory = chest.getInventory();

                        if (item.getType() != Material.AIR &&
                                item.getType() != Material.WOOD_SWORD &&
                                item.getType() != Material.IRON_SWORD &&
                                item.getType() != Material.DIAMOND_SWORD &&
                                item.getType() != Material.STONE_SWORD &&
                                item.getType() != Material.COMPASS &&
                                item.getType() != Material.WOOD_PICKAXE &&
                                item.getType() != Material.STONE_PICKAXE &&
                                item.getType() != Material.IRON_PICKAXE &&
                                item.getType() != Material.DIAMOND_PICKAXE &&
                                item.getType() != Material.GOLD_PICKAXE &&
                                item.getType() != Material.WOOD_AXE &&
                                item.getType() != Material.STONE_AXE &&
                                item.getType() != Material.IRON_AXE &&
                                item.getType() != Material.DIAMOND_AXE &&
                                item.getType() != Material.GOLD_AXE &&
                                item.getType() != Material.SHEARS) {


                            if (chestInventory.firstEmpty() == -1) {
                                return;
                            } else {
                                PlayerDepositEvent playerDepositEvent = new PlayerDepositEvent(p, DepositType.CHEST, clickedBlock);
                                Bukkit.getPluginManager().callEvent(playerDepositEvent);
                                if (!playerDepositEvent.isCancelled()) {
                                    final int[] itemCount = {0};
                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            if (DepositPlugin.plugin.getConfig().getBoolean("deposit-whole-itemstack")) {
                                                for (ItemStack itemStack : p.getInventory().getContents()) {
                                                    if (itemStack == null) {
                                                        continue;
                                                    }
                                                    if (itemStack.getType() == itemMat) {
                                                        itemCount[0] = itemStack.getAmount() + itemCount[0];
                                                        p.getInventory().removeItem(item);
                                                        chestInventory.addItem(item);
                                                    }

                                                }
                                                if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.GOLD_INGOT) {
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + itemCount[0] + " " + ChatColor.GOLD + itemName + ChatColor.GRAY + " to the" + ChatColor.AQUA + " Team Chest");
                                                } else {
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + itemCount[0] + " " + ChatColor.WHITE + itemName + ChatColor.GRAY + " to the" + ChatColor.AQUA + " Team Chest");
                                                }
                                                p.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + p.getName() + " deposited " + ChatColor.WHITE + item.getAmount() + "x " + item.getType() + ChatColor.GOLD + " to the chest");
                                                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                                                p.getInventory().removeItem(item);
                                            } else {
                                                if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.GOLD_INGOT) {
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + item.getAmount() + " " + ChatColor.GOLD + itemName + ChatColor.GRAY + " to the" + ChatColor.AQUA + " Team Chest");
                                                } else {
                                                    p.sendMessage(ChatColor.GRAY + "You" + " deposited x" + item.getAmount() + " " + ChatColor.WHITE + itemName + ChatColor.GRAY + " to the" + ChatColor.AQUA + " Team Chest");
                                                }
                                                p.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + p.getName() + " deposited " + ChatColor.WHITE + item.getAmount() + "x " + item.getType() + ChatColor.GOLD + " to the chest");
                                                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                                                p.setItemInHand(null);
                                                chestInventory.addItem(item);
                                            }

                                        }
                                    }.runTaskAsynchronously(DepositPlugin.plugin);




                                } else {
                                    DepositPlugin.warn("Player deposit event has been canceled");
                                }

                            }

                        }

                    }

                    e.setCancelled(true);

                }

            }
        }




    }

    @EventHandler
    public void enderChestClick(PlayerDepositEvent event) {
        if (event.getDepositType() == DepositType.CHEST) {
            Player p = event.getPlayer();
            DepositPlugin.debug("Checking deposit event for player: " + p.getName());

            IArena a = DepositPlugin.bedWars.getArenaUtil().getArenaByPlayer(p);
            if (a == null) {
                DepositPlugin.debug("Arena is null for player: " + p.getName());
                return;
            }

            ITeam owner = null;
            int isRad = a.getConfig().getInt(ConfigPath.ARENA_ISLAND_RADIUS);
            DepositPlugin.debug("Arena radius: " + isRad);

            Block block = event.getBlock();
            if (block == null) {
                DepositPlugin.debug("Block is null for player: " + p.getName());
                return;
            }

            Location blockLoc = block.getLocation();
            DepositPlugin.debug("Block location: " + blockLoc);

            for (ITeam t : a.getTeams()) {
                if (t == null || t.getSpawn() == null) {
                    DepositPlugin.debug("Skipping team due to null spawn.");
                    continue;
                }
                DepositPlugin.debug("Checking team: " + t.getName() + " with spawn at " + t.getSpawn());

                if (t.getSpawn().distance(blockLoc) <= isRad) {
                    owner = t;
                    DepositPlugin.debug("Found owner: " + t.getName());
                }
            }

            if (owner != null) {
                if (!owner.isMember(p)) {
                    DepositPlugin.debug("Player " + p.getName() + " is NOT a member of " + owner.getName());

                    if (!(owner.getMembers().isEmpty() && owner.isBedDestroyed())) {
                        DepositPlugin.debug("Team " + owner.getName() + " is still in the game. Cancelling event.");
                        event.setCancelled(true);
                        p.sendMessage(getMsg(p, Messages.INTERACT_CHEST_CANT_OPEN_TEAM_ELIMINATED));
                    } else {
                        DepositPlugin.debug("Team " + owner.getName() + " is eliminated, allowing chest interaction.");
                    }
                } else {
                    DepositPlugin.debug("Player " + p.getName() + " is a member of " + owner.getName() + ", allowing access.");
                }
            } else {
                DepositPlugin.debug("No team owns this chest.");
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
}