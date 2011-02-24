package net.namedfork.bukkit.PickBoat;

import org.bukkit.entity.Boat;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.EntityBoat;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.entity.Player;

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
            event.getVehicle().remove();
            event.setCancelled(true);

            // find out who gets the boat
            Player p = null;
            if (plugin.getConfiguration().getBoolean("boats_return_to_attacker", false) && event.getAttacker() instanceof Player) {
                p = (Player)event.getAttacker();
            } else if (plugin.getConfiguration().getBoolean("boats_return_to_owner", false) && cb.getPassenger() instanceof Player) {
                p = (Player)cb.getPassenger();
            }

            // give or drop boat
            this.giveOrDropBoat(p, cb.getWorld(), loc);
        }
    }

   public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
        if (!(event.getVehicle() instanceof Boat)) return;
        CraftBoat cb = (CraftBoat)event.getVehicle();
        Location loc = event.getVehicle().getLocation();
        double speed = event.getVehicle().getVelocity().length();

        if (speed > 0.15) { // boat will die¡
            event.getVehicle().setVelocity(new Vector(0,0,0));
            if (plugin.getConfiguration().getBoolean("boats_never_crash", false)) return;
            event.getVehicle().remove(); // destroy boat

            // find out who gets the boat
            Player p = null;
            if (plugin.getConfiguration().getBoolean("boats_return_to_owner", false) && cb.getPassenger() instanceof Player) {
                p = (Player)cb.getPassenger();
            }
            
            // give or drop boat
            this.giveOrDropBoat(p, cb.getWorld(), loc);
        }
    }
}
