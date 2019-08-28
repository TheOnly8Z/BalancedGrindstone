package theonly8z.github.io;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class BGListener implements Listener {

    private FileConfiguration config;
    private BalancedGrindstone main;

    public BGListener(BalancedGrindstone main) {
        this.main = main;
        config = main.getConfig();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        try {
            if (event.getClickedInventory().getType() == InventoryType.GRINDSTONE
                    && event.getSlotType() == InventoryType.SlotType.RESULT) {
                main.getLogger().info("Detected click on Grindstone result, item is " + event.getCurrentItem().toString() + ".");

                final Inventory inv = event.getClickedInventory();
                final ItemStack item = event.getCurrentItem();
                final Block grindstone = inv.getLocation().getBlock();

                // If slot 1 is empty it's disenchanting, otherwise it is repair
                if (event.getClickedInventory().getItem(1) == null) {

                    // Disenchant

                    if (item.getType() == Material.BOOK) {

                        // Destroy book

                        if (Math.random() <= config.getDouble("disenchant.bookDestroy", 0.5)) {
                            event.setCancelled(true); // Is this necessary?
                            inv.remove(item);
                            // TODO Sound for destroyed book
                        }

                    } else {

                        // Destroy gear

                        if (Math.random() <= config.getDouble("disenchant.gearDestroy", 0)) {
                            event.setCancelled(true);
                            inv.remove(item);
                            // TODO Sound for destroyed gear
                        }

                        // Damage gear

                        if (!event.isCancelled()) {
                            double damagePercent = config.getDouble("disenchant.gearDamage.percent", 0.3);
                            // A little sanity check never killed anyone
                            damagePercent = Math.max(damagePercent, 0.0);
                            damagePercent = Math.min(damagePercent, 1.0);

                            final Damageable dmgb = (Damageable) item.getItemMeta();
                            int dmg = dmgb.getDamage();
                            if (config.getBoolean("disenchant.gearDamage.useMax", true)) {
                                dmg += (int)Math.round(item.getType().getMaxDurability() * damagePercent);
                            } else {
                                dmg = (int)(dmg * (1 + damagePercent));
                            }

                            // Prevent destruction if config says so
                            if (!config.getBoolean("disenchant.gearDamage.canDestroy", true)) {
                                dmg = Math.min(dmg, item.getType().getMaxDurability() - 1);
                            }

                            dmgb.setDamage(dmg);

                            // TODO Sound for damaged gear

                        }
                    }

                    // TODO Reduce durability of grindstone


                } else {

                    // TODO Repair

                    // TODO Repair durability damage

                }



            }
        } catch (NullPointerException e) {
            // Do nothing, I guess?
        }

    }

}
