package me.parsa.depositapi.versionsupport;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public interface IVersionSupport {

    ItemStack getSkull(String base64);

    @NotNull
    ItemStack applyRenderer(MapRenderer mapRenderer, MapView mapView);
}