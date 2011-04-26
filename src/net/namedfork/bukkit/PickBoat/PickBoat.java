package net.namedfork.bukkit.PickBoat;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * PickBoat for Bukkit
 *
 * @author Jesús A. Álvarez (zydeco@namedfork.net)
 */
public class PickBoat extends JavaPlugin {
    private final PickBoatBoatListener boatListener = new PickBoatBoatListener(this);
    public boolean boatsDieWhenDestroyed, boatsDieWhenCrashed, boatsReturnToOwner, boatsReturnToAttacker, boatsNeverCrash;
    
    public void onLoad() {
        
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
        
        // load configuration
        this.loadConfigFile();
        Configuration cfg = this.getConfiguration();
        boatsDieWhenDestroyed = cfg.getBoolean("boats_die_when_destroyed", false);
        boatsDieWhenCrashed = cfg.getBoolean("boats_die_when_crashed", false);
        boatsReturnToOwner = cfg.getBoolean("boats_return_to_owner", false);
        boatsReturnToAttacker = cfg.getBoolean("boats_return_to_attacker", false);
        boatsNeverCrash = cfg.getBoolean("boats_never_crash", false);

        PluginManager pm = getServer().getPluginManager();
        try {
            pm.registerEvent(Event.Type.VEHICLE_DESTROY, boatListener, Priority.High, this);
        } catch (Exception e) {
            if (!boatsDieWhenDestroyed)
                pm.registerEvent(Event.Type.VEHICLE_DAMAGE, boatListener, Priority.High, this);
        }
        if (!boatsDieWhenCrashed)
            pm.registerEvent(Event.Type.VEHICLE_COLLISION_BLOCK, boatListener, Priority.High, this);
    }
    
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("Disabling " + pdfFile.getName());
    }

    public void loadConfigFile() {
		// load config file, creating it first if it doesn't exist
		File configFile = new File(this.getDataFolder(), "config.yml");
		if (!configFile.canRead()) try {
			configFile.getParentFile().mkdirs();
			JarFile jar = new JarFile(this.getFile());
			JarEntry entry = jar.getJarEntry("config.yml");
			InputStream is = jar.getInputStream(entry);
			FileOutputStream os = new FileOutputStream(configFile);
			byte[] buf = new byte[(int)entry.getSize()];
			is.read(buf, 0, (int)entry.getSize());
			os.write(buf);
			os.close();
			this.getConfiguration().load();
		} catch (Exception e) {
			System.out.println("PickBoat: could not create configuration file");
		}


	}
}

