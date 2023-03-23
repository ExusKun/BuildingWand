package me.exus.dev.buildwand;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class ExBuildingWand extends JavaPlugin {

    private static ExBuildingWand plugin;

    public static ExBuildingWand getInstance()
    {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        this.getCommand("bw").setExecutor(new cmd());
        getServer().getPluginManager().registerEvents(new EventListener(), getInstance());
        getLogger().info("Loading Config");
        this.getCommand("bw").setTabCompleter(new cmdTab());
        List<String> wands = new ArrayList<>();
        for (String s : plugin.getConfig().getConfigurationSection("wands").getKeys(false)) {
            if(s != null)
            {
                wands.add(s.toString());
            }
        }
        getLogger().info("Loaded " + wands.size() + " wands.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String chatColor(String chat)
    {
        String result = ChatColor.translateAlternateColorCodes('&', chat);
        return result;
    }

    public boolean hasWorldGuard() {
        Boolean bool = getServer().getPluginManager().getPlugin("World Guard") != null;
        return bool;
    }

}
