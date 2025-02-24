package me.parsa.depositapi.versionsupport;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public interface IVersionSupport {
    void sendTitle(Player player, String title, String subtitle);
}
