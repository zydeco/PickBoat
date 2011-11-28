package net.namedfork.bukkit.PickBoat;

import org.bukkit.*;
import org.bukkit.util.Vector;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

public class PickBoatBoatListener extends VehicleListener {
    private final PickBoat plugin;

    public PickBoatBoatListener(final PickBoat plugin) {
        this.plugin = plugin;
    }
    
    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvent(Event.Type.VEHICLE_DESTROY, this, Priority.High, plugin);
    }
    
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;
        Boat boat = (Boat) event.getVehicle();
        Entity attacker = event.getAttacker();
        if (plugin.boatsDieWhenCrashed && attacker == null) return;
        if (plugin.boatsDieWhenDestroyed && attacker != null) return;
        
        if (plugin.boatsNeverCrash && attacker == null) {
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
        if (plugin.boatsReturnToOwner && boat.getPassenger() instanceof Player) {
            boatReceiver = (Player) boat.getPassenger();
        } else if (plugin.boatsReturnToAttacker && attacker instanceof Player) {
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
