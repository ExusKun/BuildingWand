package me.exus.dev.buildwand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class cmdTab implements TabCompleter {    //create a static array of values

    Main plugin = Main.getInstance();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> completions = new ArrayList<>();
        Player p = (Player) sender;
        if (args.length == 1) {
            completions.add("give");
            completions.add("repair");
            completions.add("length");
            completions.add("help");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "give":
                    ConfigurationSection section = plugin.getConfig().getConfigurationSection("wands");
                    for (String s : section.getKeys(false)) {
                        if(s != null)
                        {
                            completions.add(s.toString());
                        }
                    }
                    break;
                case "repair":
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                    break;
            }
            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        }
        return null;
    }
}
