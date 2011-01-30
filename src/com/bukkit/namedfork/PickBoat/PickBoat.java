package com.bukkit.namedfork.PickBoat;

import java.io.*;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PickBoat for Bukkit
 *
 * @author Jesús A. Álvarez (zydeco@namedfork.net)
 */
public class PickBoat extends JavaPlugin {
    private final PickBoatBoatListener boatListener = new PickBoatBoatListener(this);
    
    public PickBoat(PluginLoader pluginLoader, Server instance,
            PluginDescriptionFile desc, File folder, File plugin,
            ClassLoader cLoader) throws IOException {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

    }

    public void onEnable() {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.VEHICLE_DAMAGE, boatListener, Priority.Normal, this);
        
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }
    
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Disabling " + pdfFile.getName());
    }
}

