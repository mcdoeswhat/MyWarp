package me.albert.mywarp.tabcompleter;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            if (args.length == 1 && MyWarp.getInstance().getConfig().getBoolean("enable-tab-complete")){
                ArrayList<String> warpNames = new ArrayList<>();
                final List<String> completions = new ArrayList<>();
                for (Warp warp : WarpUtil.warps){
                    warpNames.add(warp.getName());
                }
                StringUtil.copyPartialMatches(args[0], warpNames, completions);
                Collections.sort(completions);
                return completions;
            }
            return null;
        }
        return null;
    }
}
