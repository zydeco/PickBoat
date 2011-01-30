package com.bukkit.namedfork.PickBoat;

import org.bukkit.entity.Boat;
import org.bukkit.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.EntityBoat;
import org.bukkit.craftbukkit.entity.CraftBoat;

public class PickBoatBoatListener extends VehicleListener {
    private final PickBoat plugin;

    public PickBoatBoatListener(final PickBoat plugin) {
        this.plugin = plugin;
    }

    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() == null) return;
        if (!(event.getVehicle() instanceof Boat)) return;

        // get boat damage
        // some day, bukkit will implement this
        CraftBoat cb = (CraftBoat)event.getVehicle();
        EntityBoat eb = (EntityBoat)cb.getHandle();
        Location loc = event.getVehicle().getLocation();
        int damage = eb.a;
        
        // kill it and drop a boat
        if (damage + event.getDamage()*10 > 40) {
            eb.q(); // destroy boat
            event.setCancelled(true);
            event.getVehicle().getWorld().dropItemNaturally(loc, new ItemStack(Material.BOAT, 1));
        }
    }
}
