package net.namedfork.bukkit.PickBoat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PickBoat for Bukkit
 *
 * @author Jesús A. Álvarez (zydeco@namedfork.net)
 */
public class PickBoat extends JavaPlugin {

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
        
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        
        PickBoatBoatListener boatListener = new PickBoatBoatListener(this);
        getServer().getPluginManager().registerEvents(boatListener, this);
    }
    
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Disabling " + pdfFile.getName());
    }
}

