package theonly8z.github.io;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class BalancedGrindstone extends JavaPlugin {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        // Listener(s)
        BGListener bgListener = new BGListener(this);
        getServer().getPluginManager().registerEvents(bgListener, this);

        // Default config(s)

        config.addDefault("disenchant.bookDestroy", 0.5);
        config.addDefault("disenchant.gearDamage.percent", 0.3);
        config.addDefault("disenchant.gearDamage.useMax", true);
        config.addDefault("disenchant.gearDamage.canDestroy", true);

        config.addDefault("repair.additionalDurability", 0.15);

        this.saveDefaultConfig();
        // config.options().copyDefaults(true);
        // saveConfig();
    }
    @Override
    public void onDisable() {

    }


    public void sendMessage(HumanEntity ply, String message) {
        ply.sendMessage("[" + "§7BalancedGrindstone§f" + "]" + ": §r" + message);
    }

}
