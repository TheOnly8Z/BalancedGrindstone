package theonly8z.github.io;

import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class BGListener implements Listener {

    private FileConfiguration config;
    private BalancedGrindstone main;

    public BGListener(BalancedGrindstone main) {
        this.main = main;
        config = main.getConfig();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.GRINDSTONE) {

            if (config.getDouble("disenchant.bookDestroy", 0.5) > 0) {

                main.sendMessage(event.getPlayer(), "Grindstones are nerfed and now have a "
                        + Math.round(config.getDouble("disenchant.bookDestroy", 0.5) * 100) + "% chance to " +
                        "destroy enchanted books.");

                if (config.getDouble("disenchant.gearDamage.percent", 0.3) > 0) {
                    main.sendMessage(event.getPlayer(), "Additionally, it will damage gear by " +
                            Math.round(config.getDouble("disenchant.gearDamage.percent", 0.3) * 100) + "% of its " +
                            (config.getBoolean("disenchant.useMax") ? "max " : "current ") + "durability.");
                }

            } else {

                if (config.getDouble("disenchant.gearDamage.percent", 0.3) > 0) {
                    main.sendMessage(event.getPlayer(), "Grindstones are nerfed and now damage gear by " +
                            Math.round(config.getDouble("disenchant.gearDamage.percent", 0.3) * 100) + "% of its " +
                            (config.getBoolean("disenchant.useMax") ? "max " : "current ") + "durability.");
                }

            }


        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        try {
            if (event.getClickedInventory().getType() == InventoryType.GRINDSTONE
                    && event.getSlotType() == InventoryType.SlotType.RESULT) {
                // main.getLogger().info("Detected click on Grindstone result, item is " + event.getCurrentItem().toString() + ".");

                final Inventory inv = event.getClickedInventory();
                final HumanEntity ply = event.getWhoClicked();
                final Inventory plyInv = event.getWhoClicked().getInventory();
                final ItemStack item = event.getCurrentItem();
                final Block grindstone = inv.getLocation().getBlock();

                // If 1 slot is empty it's disenchanting, otherwise it is repair
                if (event.getClickedInventory().getItem(0) == null
                        || event.getClickedInventory().getItem(1) == null) {

                    // Disenchant

                    if (item.getType() == Material.BOOK) {

                        // Destroy book

                        if (Math.random() <= config.getDouble("disenchant.bookDestroy", 0.5)) {
                            if (inv.getItem(0) != null)
                                inv.remove(inv.getItem(0));
                            else
                                inv.remove(inv.getItem(1));

                            event.setCancelled(true);

                            ply.getWorld().playSound(grindstone.getLocation(), "block.enchantment_table.use", SoundCategory.BLOCKS, 2.0f, 2f);
                            ply.getWorld().playSound(grindstone.getLocation(), "block.grindstone.use", SoundCategory.BLOCKS, 2.0f, 1.5f);
                            // main.sendMessage(ply, "Your enchanted book was destroyed when disenchanting...");
                        }

                    } else {

                        // Damage gear
                        if (config.getDouble("disenchant.gearDamage.percent", 0.3) > 0) {
                            double damagePercent = config.getDouble("disenchant.gearDamage.percent", 0.3);
                            // A little sanity check never killed anyone
                            damagePercent = Math.max(damagePercent, 0.0);
                            damagePercent = Math.min(damagePercent, 1.0);

                            int dmg = ((Damageable) item.getItemMeta()).getDamage();
                            if (config.getBoolean("disenchant.gearDamage.useMax", true)) {
                                dmg += (int) Math.round(item.getType().getMaxDurability() * damagePercent);
                            } else {
                                dmg = (int) (dmg * (1 + damagePercent));
                            }

                            // Prevent destruction if config says so
                            if (!config.getBoolean("disenchant.gearDamage.canDestroy", true)) {
                                dmg = Math.min(dmg, item.getType().getMaxDurability() - 1);
                            }

                            if (dmg >= item.getType().getMaxDurability()) {
                                if (inv.getItem(0) != null)
                                    inv.remove(inv.getItem(0));
                                else
                                    inv.remove(inv.getItem(1));
                                event.setCancelled(true);
                                main.sendMessage(ply, "Your equipment was destroyed during disenchanting.");
                                ply.getWorld().playSound(grindstone.getLocation(), "block.enchantment_table.use", SoundCategory.BLOCKS, 2f, 2f);
                                ply.getWorld().playSound(grindstone.getLocation(), "block.grindstone.use", SoundCategory.BLOCKS, 1.0f, 1.5f);
                            } else {
                                final Damageable dmgb = (Damageable) item.getItemMeta();
                                dmgb.setDamage(dmg);
                                item.setItemMeta((ItemMeta) dmgb);
                                ply.getWorld().playSound(grindstone.getLocation(), "block.enchantment_table.use", SoundCategory.BLOCKS, 0.5f, 1.5f);
                            }

                        }
                    }

                } else {

                    if (config.getDouble("repair.bonusDurability", 0.5) > 0) {
                        int bonusDurability = (int)Math.round(
                                config.getDouble("repair.bonusDurability", 0.5) * item.getType().getMaxDurability());
                        final Damageable dmgb = (Damageable) item.getItemMeta();
                        dmgb.setDamage(Math.max((dmgb.getDamage() - bonusDurability), 0));
                        item.setItemMeta((ItemMeta) dmgb);
                        ply.getWorld().playSound(grindstone.getLocation(), "entity.experience_orb.pickup", SoundCategory.BLOCKS, 2f, 0.5f);
                    }

                }



            }
        } catch (NullPointerException e) {
            // Do nothing, I guess?
        }

    }

}
