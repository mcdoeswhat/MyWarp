package me.albert.mywarp.commands;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.regex.Pattern;

public class SetWarp implements CommandExecutor {
    private static MyWarp instance = MyWarp.getInstance();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String prefix = Messages.getMsg("prefix");
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + Messages.getMsg("player_only"));
            return true;
        }
        Player p = (Player) sender;
        int cost = instance.getConfig().getInt("set-warp-cost");
        if (args.length == 0) {
            sender.sendMessage(prefix + Messages.getMsg("usage_setwarp"));
            return true;
        }
        int max = instance.getConfig().getInt("max-warp-name-length");
        int min = instance.getConfig().getInt("min-warp-name-length");
        if (args[0].length() > max || args[0].length() < min) {
            sender.sendMessage(prefix + Messages.getMsg("invalid_length"));
            return true;
        }
        String regex = "["+instance.getConfig().getString("warp-name-regex")+"]*";
        if (!Pattern.matches(regex,args[0])){
            sender.sendMessage(prefix + Messages.getMsg("invalid_char"));
            return true;

        }
        if (MyWarp.getEconomy().getBalance(p) < cost) {
            String msg = prefix+Messages.getMsg("ins_balance").replace("[0]",String.valueOf(cost));
            p.sendMessage(msg);
            return true;
        }
        if (WarpUtil.hasWarp(args[0])) {
            String msg = prefix+Messages.getMsg("has_warp").replace("[0]",args[0]);
            sender.sendMessage(msg);
            return true;
        }
        if (WarpUtil.getWarps(p.getUniqueId()).size() >= instance.getConfig().getInt("max-warp-per-user")) {
            String msg = prefix+Messages.getMsg("warp_limit").replace("[0]",
                    String.valueOf(instance.getConfig().getInt("max-warp-per-user")));
            p.sendMessage(msg);
            p.sendMessage(prefix+Messages.getMsg("usage_warplist"));
            return true;
        }
        if (WarpUtil.getNearbyWarp(p.getLocation()) != null) {
            String msg = prefix+Messages.getMsg("has_warp_nearby").replace("[0]"
            , Objects.requireNonNull(WarpUtil.getNearbyWarp(p.getLocation())).getName());
            sender.sendMessage(msg);
            return true;
        }
        if (instance.getConfig().getBoolean("only_canbuild") && !WarpUtil.canBuild(p)){
            sender.sendMessage(prefix+Messages.getMsg("cant_build"));
            return true;
        }
        MyWarp.getEconomy().withdrawPlayer(p, cost);
        WarpUtil.createWarp(p.getLocation(),p, args[0]);
        String msg = prefix+Messages.getMsg("create_warp").replace("[0]",args[0]);
        String msg2 = prefix+Messages.getMsg("balance_take").replace("[0]",String.valueOf(cost));
        sender.sendMessage(msg);
        sender.sendMessage(msg2);
        return true;
    }
}
