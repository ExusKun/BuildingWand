package me.exus.dev.buildwand;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class cmd implements CommandExecutor {
    WandBuilder wB;
    Main plugin = Main.getInstance();
    NamespacedKey durability = new NamespacedKey(plugin, "wandDurability");
    NamespacedKey wandType = new NamespacedKey(plugin, "wandType");
    NamespacedKey maxBlocks = new NamespacedKey(plugin, "maxBlocks");
    NamespacedKey buildLength = new NamespacedKey(plugin, "buildLength");
    NamespacedKey cooldown = new NamespacedKey(plugin, "cooldown");
    NamespacedKey blockSelection = new NamespacedKey(plugin, "blockSelection");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args.length == 0)
            {
                player.sendMessage("&f[&6BuildWand&f] send help info here");
            }
            if(args.length > 0)
            {
                if(args[0].equals("repair"))
                {
                    repairDurability(plugin, player);
                }

                if(args[0].equals("reload"))
                {
                    plugin.reloadConfig ();
                    plugin.saveConfig ();
                    plugin.getLogger().info("Successfully reloaded plugin");
                }

                if(args[0].equals("give")) {
                    if(args.length == 1)
                    {
                        player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Please specify a Build Wand ID"));
                    }
                    if(args.length == 2)
                    {
                        if (plugin.getConfig().getString("wands." + args[1] + ".item_type") != null) {
                            ItemStack wand = wB.createWand(args[1]);
                            if(wand != null)
                            {
                                player.getInventory().addItem(wB.createWand(args[1]));
                                player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Given &6" + player.getDisplayName() + " &f" + plugin.getConfig().getString("wands." + args[1] + ".name") + " &fwand."));
                                plugin.getLogger().info("Given " + player.getDisplayName() + " " + plugin.getConfig().getString("wands." + args[1] + ".name") + " wand.");
                            }
                            else
                            {
                                player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Build Wand ID incorrect - please try again."));
                            }
                        }
                    }
                }
                if(args[0].equals("length") || args[0].equals("l"))
                {
                    if(args.length == 2)
                    {
                        changeLength(player, args, plugin);
                    }
                    else if(args.length == 1)
                    {
                        player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Please specify the amount of blocks you'd like the wand to place."));
                    }
                }
            }
        }

        return true;
    }
    public void weakenDurability(Main plugin, PlayerInteractEvent e, ItemMeta bW, PersistentDataContainer container) {
        Integer dura = container.get(durability, PersistentDataType.INTEGER);
        dura = dura-1;
        container.set(durability, PersistentDataType.INTEGER, dura);
        List<String> lores = bW.getLore();
        lores.set(lores.size()-1, plugin.chatColor(plugin.getConfig().getString("durability-chat-color") + "Wand can place "
                + dura + " more times."));
        bW.setLore(lores);
        e.getItem().setItemMeta(bW);
    }

    public void repairDurability(Main plugin, Player p) {
        ItemMeta bW = p.getItemInHand().getItemMeta();
        PersistentDataContainer container = bW.getPersistentDataContainer();
        Integer dura = container.get(durability, PersistentDataType.INTEGER);
        if(dura == -1)
        {
            p.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Your " + bW.getDisplayName() + " &falready has infinite durability!"));
        }
        if(dura != -1)
        {
            dura = plugin.getConfig().getInt("wands." + container.get(wandType, PersistentDataType.STRING) + ".durability");
            container.set(durability, PersistentDataType.INTEGER, dura);
            List<String> lores = bW.getLore();
            lores.set(lores.size()-1, plugin.chatColor(plugin.getConfig().getString("durability-chat-color") + "Wand can place "
                    + dura + " more times."));
            bW.setLore(lores);
            p.getItemInHand().setItemMeta(bW);
            p.sendMessage(plugin.chatColor("[&6BuildWand&f] Repaired " + bW.getDisplayName()));
        }
    }

    public void changeLength(Player player, String[] args, Main plugin)
    {
        try
        {
            int length = Integer.parseInt(args[1]);
            if(length < 1)
            {
                length = 1;
            }
            if(player.getItemInHand() != null)
            {
                ItemMeta bW = player.getItemInHand().getItemMeta();
                PersistentDataContainer container = bW.getPersistentDataContainer();
                if(container.has(wandType, PersistentDataType.STRING))
                {
                    if(length <= container.get(maxBlocks, PersistentDataType.INTEGER))
                    {
                        container.set(buildLength, PersistentDataType.INTEGER, length);
                        player.getItemInHand().setItemMeta(bW);
                        player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Set building length to " + length));
                    }
                    else
                    {
                        player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] This wand only allows a build length of up to " + container.get(maxBlocks, PersistentDataType.INTEGER)));
                        length = container.get(maxBlocks, PersistentDataType.INTEGER);
                        container.set(buildLength, PersistentDataType.INTEGER, length);
                        player.getItemInHand().setItemMeta(bW);
                        player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Set building length to " + length));
                    }
                }
                else
                {
                    player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] Please hold a Build Wand when attempting to change the building length."));
                }
            }
        }
        catch (Exception e)
        {
            player.sendMessage(plugin.chatColor("&f[&6BuildWand&f] &cError: Please use a whole number."));
        }
    }
}
