package me.albert.mywarp.commands;

import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WarpList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        String prefix = Messages.getMsg("prefix");
        if (!(sender instanceof Player)){
            sender.sendMessage(prefix+Messages.getMsg("player_only"));
            return true;
        }
        Player p = (Player)sender;
        if (!p.hasPermission("mywarp.warplist")){
            p.sendMessage(prefix+Messages.getMsg("no_permission"));
            return true;
        }
        ArrayList<String> warps = new ArrayList<>();
        for (Warp w : WarpUtil.warps){
            if (w.getOwner().getUniqueId().equals(p.getUniqueId())){
                warps.add(w.getName());
            }
        }
        p.sendMessage(prefix+Messages.getMsg("warplist")+warps.toString());
        return true;
    }

    public static ArrayList<Warp> getWarps(OfflinePlayer p){
        ArrayList<Warp> warps = new ArrayList<>();
        for (Warp w : WarpUtil.warps){
            if (w.getOwner().getUniqueId().equals(p.getUniqueId())){
                warps.add(w);
            }
        }
        return warps;
    }
    public static ArrayList<OfflinePlayer> getWarpUsers(){
        ArrayList<OfflinePlayer> users = new ArrayList<>();
        for (Warp w : WarpUtil.warps){
            if (users.contains(w.getOwner())){
                continue;
            }
            users.add(w.getOwner());
        }
        return users;
    }
}
