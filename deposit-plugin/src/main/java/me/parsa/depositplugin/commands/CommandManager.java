package me.parsa.depositplugin.commands;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.command.ParentCommand;
import com.andrei1058.bedwars.api.command.SubCommand;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import me.parsa.depositplugin.Listeners.EnderChestClick;
import me.parsa.depositplugin.Listeners.GameStartListener;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends SubCommand {
    private Location spawnLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();

    private World spawn_world = spawnLocation.getWorld();

    public CommandManager(ParentCommand parent, String name) {
        super(parent, name);
        String unmade = "§6 ▪ §7/bw " + "deposit"  + "        §8   - §e" + "See bedwars deposit commands";
        String made = ChatColor.translateAlternateColorCodes('&', unmade);
        net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(made);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to see the bedwars1058-deposit ")}));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bw deposit"));
        setDisplayInfo(textComponent);
        setPriority(14);
        setPermission("deposit.admin");

    }

    @Override
    public boolean execute(String[] strings, CommandSender commandSender) {

        Player player = (Player) commandSender;

        if (player.hasPermission("deposit.admin")) {
            if (strings.length == 0) {
                List<String> list = ArenasConfig.get().getStringList("worlds." + player.getWorld().getName() + ".chestLocations");
                int size = list.size();
                String the = "§6 ▪ §7/bw deposit SetChestLoc " + ((size == 0) ? "&c&l(NOT SET) " : (size < 16) ? "&e&l(NOT PROPERLY SET) " : (size == 16) ? "&a&l(SET) " : "&c&l(NOT SET) ") + "§8 - §eSets chest locs";
                String made = ChatColor.translateAlternateColorCodes('&', the);
                TextComponent textComponent = new TextComponent(made);
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bw deposit SetChestLoc"));
                textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to run ")}));
                player.spigot().sendMessage(textComponent);
                String otherss = "§6 ▪ §7/bw deposit AutoAddChestLocs " + "§8- §eAuto Sets chest locs";
                String mades = ChatColor.translateAlternateColorCodes('&', otherss);
                TextComponent s = new TextComponent(mades);
                s.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/bw deposit AutoAddChestLocs"));
                s.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("Click to run ")}));
                player.spigot().sendMessage(s);
                return true;
            } else {

                if (strings[0].equalsIgnoreCase("SetChestLoc")) {
                    if (player.getWorld().equals(spawn_world)) {
                        player.sendMessage(ChatColor.RED + "Sorry you have to be in an arena to use this command");
                        return true;
                    }
                    EnderChestClick.addPlayerToSelectionMode(player);
                    player.sendMessage(ChatColor.GREEN + "Right-click a chest or ender chest to set its location!");

                }
                if (strings[0].equalsIgnoreCase("AutoAddChestLocs")) {
                    player.sendMessage(ChatColor.GREEN + "Starting...");
                    new GameStartListener(DepositPlugin.plugin, ArenasConfig.get()).createHDLocations();
                    player.sendMessage(ChatColor.GREEN + "Done");
                }

                DepositPlugin.debug("Command ran");
            }

        }
        return true;
    }

    @Override
    public List<String> getTabComplete() {
        ArrayList<String> list = new ArrayList<>();
        list.add("SetChestLoc");
        list.add("AutoAddChestLocs");
        return list;
    }

}
