package me.albert.mywarp.tabcompleter;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpTabCompleter implements TabCompleter {

    public static void main(String[] args) {
        System.out.println("sk".startsWith("sk"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1 && MyWarp.getInstance().getConfig().getBoolean("enable-tab-complete")) {
                final List<String> completions = new ArrayList<>();
                for (Warp warp : WarpUtil.warps) {
                    if (warp.getName().toLowerCase().startsWith(args[0].toLowerCase())
                            || warp.getName().toLowerCase().contains(args[0].toLowerCase())) {
                        completions.add(warp.getName());
                    }
                }
                return completions;
            }
            return null;
        }
        return null;
    }
}
