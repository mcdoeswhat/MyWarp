package me.albert.mywarp.commands;

import me.albert.mywarp.IWarp;
import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeWarp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(MyWarp.getInstance(), () -> {
            String prefix = Messages.getMsg("prefix");
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix + Messages.getMsg("player_only"));
                return;
            }
            if (args.length == 0) {
                sender.sendMessage(prefix + Messages.getMsg("usage_dewarp"));
                return;
            }
            if (!WarpUtil.hasWarp(args[0])) {
                String nowarp = prefix + Messages.getMsg("no_warp").replace("[0]", args[0]);
                sender.sendMessage(nowarp);
                return;
            }
            Warp warp = IWarp.getWarp(args[0]);
            Player p = (Player) sender;
            if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
                if (GriefPrevention.instance.dataStore.getClaimAt(warp.getLocation(), true, null) != null) {
                    Claim claim = GriefPrevention.instance.dataStore.getClaimAt(warp.getLocation(), true, null);
                    if (p.getName().equalsIgnoreCase(claim.getOwnerName())) {
                        warp.delete();
                        p.sendMessage(prefix + Messages.getMsg("delete_warp"));
                        return;
                    }
                }
            }
            if (!warp.getOwner().getUniqueId().equals(p.getUniqueId()) && !p.hasPermission("mywarp.delete.other")) {
                p.sendMessage(prefix + Messages.getMsg("not_owner"));
                return;
            }
            warp.delete();
            p.sendMessage(prefix + Messages.getMsg("delete_warp"));
        });
        return true;
    }
}
