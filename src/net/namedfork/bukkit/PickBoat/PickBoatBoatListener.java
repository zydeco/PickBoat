package net.namedfork.bukkit.PickBoat;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

public class PickBoatBoatListener implements Listener {
    private final PickBoat plugin;

    public PickBoatBoatListener(final PickBoat plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;
        Boat boat = (Boat) event.getVehicle();
        Entity attacker = event.getAttacker();
        if ((plugin.getConfig().getBoolean("boats_die_when_crashed") && attacker == null) ||
                (plugin.getConfig().getBoolean("boats_die_when_destroyed") && attacker != null)) {
            // remove boat
            boat.remove();
            event.setCancelled(true);
            
            // drop things
            ConfigurationSection drops = null;
            try {
                drops = (ConfigurationSection)plugin.getConfig().get("boat_drop");
            } catch (Exception e) {
                System.out.println("[PickBoat] using default boat_drop");
                drops = (ConfigurationSection)plugin.getConfig().getDefaults().get("boat_drop");
            }
            
            Set<String> dropKeys = drops.getKeys(false);
            Iterator<String> i = dropKeys.iterator();
            while(i.hasNext()) {
                String rawKey = i.next();
                String key[] = rawKey.toUpperCase(Locale.ENGLISH).split(":");
                
                // find material
                Material m = Material.AIR;
                try {
                    m = Material.getMaterial(Integer.parseInt(key[0]));
                } catch (NumberFormatException e) {
                    m = Material.getMaterial(key[0]);
                }
                
                // find value
                int value = 0;
                if (key.length >= 2) try {
                    value = Integer.parseInt(key[1]);
                } catch (NumberFormatException e) {
                    value = 0;
                }
                
                // find amount
                int amount = drops.getInt(rawKey);
                
                // drop
                if (m != null && amount > 0) {
                    MaterialData md = new MaterialData(m, (byte)value);
                    Location loc = boat.getLocation();
                    for(int a=0; a < amount; a++) {
                        loc.getWorld().dropItemNaturally(loc, md.toItemStack(1));
                    }
                }
            }
            
            return;
        }
        
        if (plugin.getConfig().getBoolean("boats_never_crash") && attacker == null) {
            // boat crashed
            boat.setVelocity(new Vector(0,0,0));
            event.setCancelled(true);
            return;
        }
        
        // destroy boat
        boat.remove();
        event.setCancelled(true);
        
        // find out who gets the boat
        Player boatReceiver = null;
        if (plugin.getConfig().getBoolean("boats_return_to_owner") && boat.getPassenger() instanceof Player) {
            boatReceiver = (Player) boat.getPassenger();
        } else if (plugin.getConfig().getBoolean("boats_return_to_attacker") && attacker instanceof Player) {
            boatReceiver = (Player) attacker;
        }
        
        // give or drop boat
        ItemStack boatStack = new ItemStack(Material.BOAT, 1);
        if (boatReceiver == null || boatReceiver.getInventory().addItem(boatStack).isEmpty() == false) {
            Location loc = boat.getLocation();
            loc.getWorld().dropItemNaturally(loc, boatStack);
        }
    }
}
