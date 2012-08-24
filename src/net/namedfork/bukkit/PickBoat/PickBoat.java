package net.namedfork.bukkit.PickBoat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * PickBoat for Bukkit
 *
 * @author Jesús A. Álvarez (zydeco@namedfork.net)
 */
public class PickBoat extends JavaPlugin {
    private Map<String,FileConfiguration> configByWorld = new HashMap<String,FileConfiguration>();

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
    
    public FileConfiguration getConfig(World w) {
        String worldName = w.getName();
        if (!configByWorld.containsKey(worldName)) {
            // load config for world
            File worldConfigFile = new File(this.getDataFolder(), worldName + ".yml");
            if (!worldConfigFile.canRead()) {
                // can't read world config file, use default
                configByWorld.put(worldName, getConfig());
                return getConfig();
            } else {
                // load world config file
                FileConfiguration worldConfig = YamlConfiguration.loadConfiguration(worldConfigFile);
                configByWorld.put(worldName, worldConfig);
                return worldConfig;
            }
        }
        
        return configByWorld.get(worldName);
    }
}

