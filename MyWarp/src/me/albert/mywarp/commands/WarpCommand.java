package me.albert.mywarp.commands;

import me.albert.mywarp.IWarp;
import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static me.albert.mywarp.WarpUtil.hasWarp;

public class WarpCommand implements CommandExecutor {
    private HashMap<UUID,Long> cds = new HashMap<>();
    private long delay = MyWarp.getInstance().getConfig().getInt("teleport-delay");
    private String prefix = Messages.getMsg("prefix");
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        prefix = Messages.getMsg("prefix");
        if (!(sender instanceof Player)){
            sender.sendMessage(prefix+Messages.getMsg("player_only"));
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(prefix+Messages.getMsg("usage_warp"));
            return true;
        }
        if (!hasWarp(args[0])){
            String msg = prefix+Messages.getMsg("no_warp").replace("[0]",args[0]);
            sender.sendMessage(msg);
            return true;
        }
        Warp warp = IWarp.getWarp(args[0]);
        Player p = (Player)sender;
        if (p.hasPermission("mywarp.cooldown.bypass")){
            teleport(p,warp);
            return true;
        }
        if (cds.containsKey(p.getUniqueId()) && System.currentTimeMillis()-cds.get(p.getUniqueId())
                < delay*1000){
            p.sendMessage(prefix+Messages.getMsg("teleporting"));
            return true;
        }
        Location previousLoc = p.getLocation();
        cds.put(p.getUniqueId(),System.currentTimeMillis());
        String msg = prefix+Messages.getMsg("teleport_in").replace("[0]",String.valueOf(delay));
        p.sendMessage(msg);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MyWarp.getInstance(), () -> {
            if (!p.getLocation().getWorld().equals(previousLoc.getWorld())){
                return;
            }
            if (p.getLocation().distanceSquared(previousLoc) > 2){
                p.sendMessage(prefix+Messages.getMsg("player_moved"));
                return;
            }
            teleport(p,warp);
        }, 20*delay);
        return true;
    }
    private void teleport(Player p,Warp warp){
        prefix = Messages.getMsg("prefix");
        warp.updateLastvisit();
        warp.addVisitor(p.getUniqueId());
        p.teleport(warp.getLocation());
        p.sendMessage(prefix+Messages.getMsg("teleported"));
    }
}
