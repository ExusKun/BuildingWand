package me.exus.dev.buildwand;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EventListener implements Listener {

    Main plugin = Main.getInstance();
    NamespacedKey durability = new NamespacedKey(plugin, "wandDurability");
    NamespacedKey wandType = new NamespacedKey(plugin, "wandType");
    NamespacedKey maxBlocks = new NamespacedKey(plugin, "maxBlocks");
    NamespacedKey buildLength = new NamespacedKey(plugin, "buildLength");
    NamespacedKey cooldown = new NamespacedKey(plugin, "cooldown");
    NamespacedKey blockSelection = new NamespacedKey(plugin, "blockSelection");

    cmd cmdBW;

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        ItemMeta bW = item.getItemMeta();
        PersistentDataContainer container = bW.getPersistentDataContainer();
        if (container.has(wandType, PersistentDataType.STRING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWandUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        Build build = new Build();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null) {
                ItemMeta bW = e.getItem().getItemMeta();
                PersistentDataContainer container = bW.getPersistentDataContainer();
                if (container.has(wandType, PersistentDataType.STRING)) {
                    int viewDist = plugin.getConfig().getInt("wands." + container.get(wandType, PersistentDataType.STRING) + ".max-block");
                    Block b = p.getTargetBlock(null, 100);
                    if (p.isSneaking()) {
                        if (!plugin.getConfig().getList("block-blacklist").contains(b.getType().toString())) {
                            bW.setDisplayName(plugin.chatColor(plugin.getConfig().getString("wands." + container.get(wandType, PersistentDataType.STRING) + ".name"))
                                    + plugin.chatColor(" &a(") + b.getType().toString() + plugin.chatColor("&a)"));
                            container.set(blockSelection, PersistentDataType.STRING, b.getType().toString());
                            p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Selected &a" + b.getType().toString() + "&f as building block"));
                            e.getItem().setItemMeta(bW);
                        }
                    } else {
                        if (container.has(wandType, PersistentDataType.STRING) && bW.hasLore())
                        {
                            if (container.get(blockSelection, PersistentDataType.STRING) != null)
                            {
                                if (a == Action.RIGHT_CLICK_BLOCK) {
                                    if (container.get(durability, PersistentDataType.INTEGER) > -1)
                                    {
                                        cmdBW.weakenDurability(plugin, e, bW, container);
                                    }
                                    build.builder("buildToFace", e, p, container);
                                }
                            } else
                            {
                                p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] You must first select a block to build with! Simply shift and right-click while looking at any block to select it."));
                            }
                        }
                    }
                }
            }
            return;
        }
    }
}
