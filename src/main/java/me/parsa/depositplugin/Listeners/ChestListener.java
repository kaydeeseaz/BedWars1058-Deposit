package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChestListener implements Listener {


    @EventHandler
    public void onPlayerLeftClickEnderChest(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.ENDER_CHEST || e.getClickedBlock().getType() == Material.CHEST) {
                IArena a = Arena.getArenaByPlayer(e.getPlayer());
                if (a != null && a.isPlayer(e.getPlayer())) {
                    ItemStack item = p.getItemInHand();
                    Material itemMat = item.getType();
                    Inventory inv;
                    if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
                        inv = p.getEnderChest();
                    } else if (e.getClickedBlock().getType() == Material.CHEST) {
                        Block clickedBlock = e.getClickedBlock();
                        Chest chest = (Chest) clickedBlock.getState();
                        inv = chest.getInventory();
                    } else return;

                    List<String> undepositable = DepositPlugin.configuration.getStringList("undepositable");
                    String mat = item.getType().name();
                    boolean depositable = true;
                    for (String s : undepositable) {
                        if (mat.contains(s)) {
                            depositable = false;
                            break;
                        }
                    }
                    if (depositable) {
                        if (inv.firstEmpty() == -1) {
                            return;
                        }
                        e.setCancelled(true);


//                          PlayerDepositEvent playerDepositEvent = new PlayerDepositEvent(p, DepositType.ENDER_CHEST, e.getClickedBlock());
//                          Bukkit.getPluginManager().callEvent(playerDepositEvent);

                        Bukkit.getScheduler().runTask(DepositPlugin.plugin, () -> {
                            boolean ender = e.getClickedBlock().getType() == Material.ENDER_CHEST;

                            int totalCount = 0;

                            for (ItemStack itemStack : p.getInventory().getContents()) {
                                if (itemStack == null || itemStack.getType() != itemMat) {
                                    continue;
                                }

                                int amount = itemStack.getAmount();
                                totalCount += amount;


                                p.getInventory().removeItem(itemStack);


                                inv.addItem(itemStack);
                            }


                            if (totalCount > 0) {
                                String itemName = Arrays.stream(itemMat.toString().toLowerCase().split("_"))
                                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                                        .collect(Collectors.joining(" "));

                                p.sendMessage(ChatColor.GRAY + "You deposited x" + totalCount + " " + (itemMat == Material.GOLDEN_APPLE || itemMat == Material.GOLD_INGOT ? ChatColor.GOLD : ChatColor.WHITE) + itemName + ChatColor.GRAY + " to the" + ChatColor.LIGHT_PURPLE + " "+(ender ? "Ender " : "") +"Chest");

                                p.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + p.getName() + " deposited " + ChatColor.WHITE + totalCount + "x " + itemMat + ChatColor.GOLD + " to the "+(ender ? "ender " : "")+"chest");

                                p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1.0f, 1.0f);
                            } else {
                                p.sendMessage(ChatColor.RED + "You don't have any " + itemMat + " to deposit!");
                            }
                        });
                    }
                }
            }
        }
    }
}