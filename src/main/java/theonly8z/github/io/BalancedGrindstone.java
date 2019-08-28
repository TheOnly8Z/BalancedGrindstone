package theonly8z.github.io;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BalancedGrindstone extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        // Listener(s)
        BGListener bgListener = new BGListener(this);
        getServer().getPluginManager().registerEvents(bgListener, this);

        // Default config(s)
        /*
        config.addDefault("durability.maxHits", 3);
        config.addDefault("durability.repairChance", 0.1);
        config.addDefault("durability.disenchantChance", 0.2);

        config.addDefault("disenchant.bookDestroy", 0.5);
        config.addDefault("disenchant.gearDestroy", 0);
        config.addDefault("disenchant.gearDamage.percent", 0.3);
        config.addDefault("disenchant.gearDamage.useMax", true);
        config.addDefault("disenchant.gearDamage.canDestroy", true);

        config.addDefault("repair.additionalDurability", 0.15);

        this.saveDefaultConfig();
        */
        // config.options().copyDefaults(true);
        // saveConfig();
    }
    @Override
    public void onDisable() {

    }

}
