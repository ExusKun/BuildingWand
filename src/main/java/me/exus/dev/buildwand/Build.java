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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.Timestamp;
import java.util.Map;

public class Build {
    static Main plugin = Main.getInstance();
    static NamespacedKey durability = new NamespacedKey(plugin, "wandDurability");
    static NamespacedKey wandType = new NamespacedKey(plugin, "wandType");
    static NamespacedKey maxBlocks = new NamespacedKey(plugin, "maxBlocks");
    static NamespacedKey buildLength = new NamespacedKey(plugin, "buildLength");
    static NamespacedKey cooldown = new NamespacedKey(plugin, "cooldown");
    static NamespacedKey blockSelection = new NamespacedKey(plugin, "blockSelection");

    public static void builder(String buildMethod, PlayerInteractEvent e, Player p, PersistentDataContainer container) {

        ItemMeta bW = e.getItem().getItemMeta();
        BlockFace bF = e.getBlockFace();
        Material mat = Material.getMaterial(container.get(blockSelection, PersistentDataType.STRING));
        int bLength = container.get(buildLength, PersistentDataType.INTEGER);
        Block blockBase = e.getClickedBlock();
        int blockX = blockBase.getX();
        int blockY = blockBase.getY();
        int blockZ = blockBase.getZ();
        int blocksPlaced = 0;
        World world = e.getClickedBlock().getWorld();
        switch(buildMethod)
        {
            case "buildToFace":
                if (bF.toString() == "UP") {
                    for (int i = 0; i < bLength; i++) {
                        blockY += 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (bF.toString() == "DOWN") {
                    for (int i = 0; i < bLength; i++) {
                        blockY -= 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (bF.toString() == "EAST") {
                    for (int i = 0; i < bLength; i++) {
                        blockX += 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (bF.toString() == "WEST") {
                    for (int i = 0; i < bLength; i++) {
                        blockX -= 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (bF.toString() == "NORTH") {
                    for (int i = 0; i < bLength; i++) {
                        blockZ -= 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (bF.toString() == "SOUTH") {
                    for (int i = 0; i < bLength; i++) {
                        blockZ += 1;
                        if (world.getBlockAt(blockX, blockY, blockZ).getType() == Material.AIR) {
                            if(inGuardedArea(p, new Location(world, blockX, blockY, blockZ)))
                            {
                                blocksPlaced += 1;
                                placeBlocks(p, mat, world, blockX, blockY, blockZ);
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }
                if (p.getGameMode() != GameMode.CREATIVE && !p.getInventory().contains(mat)) {
                    p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] You do not have the required number of &a" + container.get(blockSelection, PersistentDataType.STRING) + " &fto place more."));
                }
                plugin.getLogger().info(p.getName() + " placed " + blocksPlaced + " " + container.get(blockSelection, PersistentDataType.STRING));
                e.getItem().setItemMeta(bW);
        }
    }

    private static void placeBlocks(Player p, Material mat, World world, Integer blockX, Integer blockY, Integer blockZ)
    {
        if (p.getGameMode() == GameMode.CREATIVE || p.getInventory().contains(mat)) {
            consumeItem(p, 1, mat);
            world.getBlockAt(blockX, blockY, blockZ).setType(mat);
            world.playSound(new Location(world, blockX, blockY, blockZ), Sound.BLOCK_STONE_PLACE, 1, 1);
        }
    }

    private static boolean inGuardedArea(Player p, Location b) {
        Boolean bool = true;
        WorldBorder border = b.getWorld().getWorldBorder();
        p.sendMessage(border.toString());
        p.sendMessage(String.valueOf(border.getSize()/2));

        if(border.isInside(b))
        {
            return true;
        }

        if (plugin.hasSuperiorSkyblock()) {
            Island is = SuperiorSkyblockAPI.getIslandAt(b);
            if(is != null)
            {
                bool = is.hasPermission(p, IslandPrivilege.getByName("Build"));
                if(!bool)
                {
                    p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] &cYou do not have permission to build here."));
                    plugin.getLogger().info(p.getName() + " tried placing blocks at " + b.getX() + " " + b.getY() + " " + b.getZ() + " and failed due to insufficient SkyBlock building permissions" );
                }
                return bool;
            }
        }
        if (plugin.hasWorldGuard()) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
            com.sk89q.worldedit.util.Location loc2 = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(b);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
            if (!(query.testState(loc2, localPlayer, Flags.BUILD)) && !canBypass) {
                p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] &cYou do not have permission to build here."));
                plugin.getLogger().info(p.getName() + " tried placing blocks at " + b.getX() + " " + b.getY() + " " + b.getZ() + " and failed due to insufficient WorldGuard building permissions" );
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static boolean consumeItem(Player player, int count, Material mat){
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found)
            return false;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
        return true;
    }
}
