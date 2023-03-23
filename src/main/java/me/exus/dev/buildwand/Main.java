package me.exus.dev.buildwand;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main plugin;

    public static Main getInstance()
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
        if(hasWorldGuard()) getLogger().info("Found World Guard - Initializing Connection");
        if(hasSuperiorSkyblock()) getLogger().info("Found Superior SkyBlock - Initializing Connection");
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

    public boolean hasWorldGuard()
    {
        Boolean bool = getServer().getPluginManager().getPlugin("WorldGuard") != null;
        return bool;
    }

    public boolean hasSuperiorSkyblock()
    {
        Boolean bool = getServer().getPluginManager().getPlugin("SuperiorSkyblock2") != null;
        return bool;
    }

}
