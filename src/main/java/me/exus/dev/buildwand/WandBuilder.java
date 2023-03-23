package me.exus.dev.buildwand;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WandBuilder {

    static Main plugin = Main.getInstance();
    static NamespacedKey durability = new NamespacedKey(plugin, "wandDurability");
    static NamespacedKey wandType = new NamespacedKey(plugin, "wandType");
    static NamespacedKey maxBlocks = new NamespacedKey(plugin, "maxBlocks");
    static NamespacedKey buildLength = new NamespacedKey(plugin, "buildLength");
    static NamespacedKey cooldown = new NamespacedKey(plugin, "cooldown");
    NamespacedKey blockSelection = new NamespacedKey(plugin, "blockSelection");

    public static ItemStack createWand(String wand) {
        if(plugin.getConfig().getString("wands." + wand + ".item_type") != null)
        {
            ItemStack buildWand = new ItemStack(Material.valueOf(plugin.getConfig().getString("wands." + wand + ".item_type")));
            ItemMeta bW = buildWand.getItemMeta();
            Timestamp time = new Timestamp(System.currentTimeMillis());
            PersistentDataContainer container = bW.getPersistentDataContainer();
            int bL = 5;
            bW.setDisplayName(plugin.chatColor(plugin.getConfig().getString("wands." + wand + ".name")));

            container.set(durability, PersistentDataType.INTEGER, plugin.getConfig().getInt("wands." + wand + ".durability"));
            container.set(wandType, PersistentDataType.STRING, wand);
            container.set(maxBlocks, PersistentDataType.INTEGER, plugin.getConfig().getInt("wands." + wand + ".max-block"));
            container.set(buildLength, PersistentDataType.INTEGER, bL);
            container.set(cooldown, PersistentDataType.INTEGER, Integer.valueOf((int) time.getTime()));

            List<String> lores = new ArrayList<String>();
            List<String> configLore = plugin.getConfig().getStringList("wands." + wand + ".description");
            if(plugin.getConfig().getString("wands." + wand + ".description") != null)
            {
                for(int i = 0; i < configLore.size(); i++)
                {
                    lores.add(plugin.chatColor(configLore.get(i)));
                }
            }
            lores.add("");
            if(plugin.getConfig().getInt("wands." + wand + ".durability") == -1)
            {
                lores.add(plugin.chatColor(plugin.getConfig().getString("durability-chat-color") + "Wand can place infinite more times."));
            } else {
                lores.add(plugin.chatColor(plugin.getConfig().getString("durability-chat-color") + "Wand can place " +
                        plugin.getConfig().getInt("wands." + wand + ".durability") + " more times."));
            }

            bW.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            bW.setLore(lores);
            buildWand.setItemMeta(bW);
            if(plugin.getConfig().getBoolean("wands." + wand + ".enchanted") == true)
            {
                buildWand.addUnsafeEnchantment(Enchantment.LURE, 1);
            }

            return buildWand;
        }
        else {
            plugin.getLogger().info("&f[&6BuildWand&f] Wand does not exist in the config.yml. Please ensure your spelling is correct.");
            ItemStack buildWand = new ItemStack(Material.DIRT);
            return buildWand;
        }
    }
}
