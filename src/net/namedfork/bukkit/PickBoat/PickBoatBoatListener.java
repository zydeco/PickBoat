package net.namedfork.bukkit.PickBoat;

import org.bukkit.entity.Boat;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.EntityBoat;
import org.bukkit.event.Cancellable;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.entity.*;

public class PickBoatBoatListener extends VehicleListener {
    private final PickBoat plugin;

    public PickBoatBoatListener(final PickBoat plugin) {
        this.plugin = plugin;
    }

    private void giveOrDropBoat(Player p, World w, Location loc) {
        ItemStack boat = new ItemStack(Material.BOAT, 1);
        if (p == null || p.getInventory().addItem(boat).isEmpty() == false)
            w.dropItemNaturally(loc, boat);
    }
    
    private void destroyBoat(VehicleEvent event, Boat boat, Entity attacker) {
        boat.remove();
        if (event instanceof Cancellable) ((Cancellable)event).setCancelled(true);

        // find out who gets the boat
        Player p = null;
        if (plugin.boatsReturnToAttacker && attacker instanceof Player) {
            p = (Player) attacker;
        } else if (plugin.boatsReturnToOwner && boat.getPassenger() instanceof Player) {
            p = (Player) boat.getPassenger();
        }

        // give or drop boat
        this.giveOrDropBoat(p, boat.getWorld(), boat.getLocation());
    }

    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null) return;
        if (!(event.getVehicle() instanceof Boat)) return;
        Boat boat = (Boat) event.getVehicle();

        this.destroyBoat(event, boat, event.getAttacker());
    }
    
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() == null) return;
        if (!(event.getVehicle() instanceof Boat)) return;

        // get boat damage
        // some day, bukkit will implement this
        CraftBoat cb = (CraftBoat)event.getVehicle();
        EntityBoat eb = (EntityBoat)cb.getHandle();
        int damage = eb.a;
        
        // kill it and drop a boat
        if (damage + event.getDamage()*10 > 40)
            this.destroyBoat(event, cb, event.getAttacker());
    }

    public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;
        Boat boat = (Boat)event.getVehicle();
        Location loc = event.getVehicle().getLocation();
        double motX = boat.getVelocity().getX();
        double motZ = boat.getVelocity().getZ();
        double speed = Math.sqrt(motX * motX + motZ * motZ);

        if (speed > 0.15) { // boat will die
            boat.setVelocity(new Vector(0,0,0));
            if (plugin.boatsNeverCrash) return;
            boat.remove(); // destroy boat

            // find out who gets the boat
            Player p = null;
            try {
                if (plugin.boatsReturnToOwner && boat.getPassenger() instanceof Player)
                p = (Player)boat.getPassenger();
            } catch (Exception e) {}

            // give or drop boat
            this.giveOrDropBoat(p, boat.getWorld(), loc);
        }
    }
}
