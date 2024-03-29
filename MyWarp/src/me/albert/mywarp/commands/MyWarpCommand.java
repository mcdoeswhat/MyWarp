package me.albert.mywarp.commands;

import me.albert.mywarp.IWarp;
import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import me.albert.mywarp.hooks.Import;
import me.albert.mywarp.inventory.MyWarpInv;
import me.albert.mywarp.tasks.MyWarpTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MyWarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(MyWarp.getInstance(), () -> {
            String prefix = Messages.getMsg("prefix");
            switch (args.length) {
                case 1:
                    switch (args[0].toLowerCase()) {
                        case "info":
                            sender.sendMessage(prefix + "§a/mywarp info <warp>");
                            return;
                        case "list":
                            sender.sendMessage(prefix + "§a/mywarp list <player> [page]");
                            return;
                        case "purge":
                            if (!sender.hasPermission("mywarp.purge")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            sender.sendMessage(prefix + Messages.getMsg("waiting_task"));
                            MyWarpTask.purgeWarps(sender);
                            return;
                        case "reload":
                            if (!sender.hasPermission("mywarp.reload")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            if (MyWarpInv.isLoading.get()) {
                                sender.sendMessage(prefix + Messages.getMsg("loadinggui"));
                                return;
                            }
                            MyWarp.getInstance().reloadConfig();
                            WarpUtil.loadWarps();
                            MyWarpInv.loadInventory();
                            Messages.reloadMsg();
                            sender.sendMessage(prefix + Messages.getMsg("reload"));
                            return;
                        case "import":
                            if (!sender.hasPermission("mywarp.import")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            if (!Import.canImport()) {
                                sender.sendMessage(prefix + Messages.getMsg("import_error"));
                                return;
                            }
                            sender.sendMessage(prefix + Messages.getMsg("waiting_task"));
                            MyWarpTask.importWarps(sender);
                            return;
                        case "listall":
                            if (printAll(0).size() == 0) {
                                sender.sendMessage(prefix + Messages.getMsg("empty_warp"));
                                return;
                            }
                            sender.sendMessage(prefix + Messages.getMsg("warp_list"));
                            sender.sendMessage(printAll(0).toString());
                            String next = prefix + Messages.getMsg("warp_list_next").replace("[0]", "2");
                            sender.sendMessage(next);
                            return;
                        case "gui":
                            if (!sender.hasPermission("mywarp.gui")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(prefix + Messages.getMsg("player_only"));
                                return;
                            }
                            if (!MyWarp.getInstance().getConfig().getBoolean("enable-gui")) {
                                sender.sendMessage(prefix + Messages.getMsg("gui_notenable"));
                                return;
                            }
                            Player p = (Player) sender;
                            if (WarpUtil.warps.size() == 0) {
                                sender.sendMessage(prefix + Messages.getMsg("empty_warp"));
                                return;
                            }
                            if (MyWarpInv.isLoadingGUI() || MyWarpInv.visitorsSort.size() == 0) {
                                p.sendMessage(prefix + Messages.getMsg("loadinggui"));
                                return;
                            }
                            if (MyWarpInv.tempInv.containsKey(p.getUniqueId())) {
                                Bukkit.getScheduler().runTask(MyWarp.getInstance(), () -> {
                                    p.openInventory(MyWarpInv.tempInv.get(p.getUniqueId()));
                                });
                                return;
                            }
                            Bukkit.getScheduler().runTask(MyWarp.getInstance(), () -> {
                                p.openInventory(MyWarpInv.visitorsSort.get(0));
                            });
                            return;
                    }
                case 2:
                    switch (args[0].toLowerCase()) {
                        case "info":
                            if (!sender.hasPermission("mywarp.info")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            if (!WarpUtil.hasWarp(args[1])) {
                                String msg = prefix + Messages.getMsg("no_warp").replace("[0]", args[1]);
                                sender.sendMessage(msg);
                                return;
                            }
                            Warp warp = IWarp.getWarp(args[1]);
                            List<String> warpInfo = Messages.getList("warp_info");
                            replaceChar(warpInfo, "%name%", warp.getName());
                            replaceChar(warpInfo, "%X%", String.valueOf(warp.getLocation().getX()));
                            replaceChar(warpInfo, "%Y%", String.valueOf(warp.getLocation().getY()));
                            replaceChar(warpInfo, "%Z%", String.valueOf(warp.getLocation().getZ()));
                            replaceChar(warpInfo, "%world%", warp.getLocation().getWorld().getName());
                            replaceChar(warpInfo, "%owner%", warp.getOwner().getName() != null ? warp.getOwner().getName() : "null");
                            replaceChar(warpInfo, "%visits%", String.valueOf(warp.getVisitors().size()));
                            replaceChar(warpInfo, "%create_date%", String.valueOf(new Date(warp.getTimecreated())));
                            replaceChar(warpInfo, "%last_visit_date%", String.valueOf(new Date(warp.getLastvisit())));
                            for (String message : warpInfo) {
                                sender.sendMessage(message);
                            }
                            return;
                        case "list":
                            if (!sender.hasPermission("mywarp.list")) {
                                sender.sendMessage(prefix + Messages.getMsg("no_permission"));
                                return;
                            }
                            ArrayList<String> warps = printWarp(args[1], 0);
                            if (warps.size() == 0) {
                                sender.sendMessage(prefix + Messages.getMsg("player_nowarp"));
                                return;
                            }
                            String msg = prefix + Messages.getMsg("player_warp_list").replace("[0]", args[1]);
                            sender.sendMessage(msg);
                            sender.sendMessage(warps.toString());
                            String msg2 = prefix + Messages.getMsg("player_warp_list_next").replace("[0]", args[1])
                                    .replace("[1]", "2");
                            sender.sendMessage(msg2);
                            return;
                        case "listall":
                            if (notNumeric(args[1])) {
                                sender.sendMessage(prefix + Messages.getMsg("not_num"));
                                return;
                            }

                            int page = Integer.parseInt(args[1]);
                            page = Math.abs(page);

                            if ((printAll(page - 1).size() == 0)) {
                                sender.sendMessage(prefix + Messages.getMsg("last_page"));
                                return;
                            }
                            sender.sendMessage(prefix + Messages.getMsg("warp_list"));
                            sender.sendMessage(printAll(page - 1).toString());
                            String next = prefix + Messages.getMsg("warp_list_next").replace("[0]", String.valueOf(page + 1));
                            sender.sendMessage(next);
                            return;
                    }
                case 3:
                    if (args[0].equalsIgnoreCase("list")) {
                        if (notNumeric(args[2])) {
                            sender.sendMessage(prefix + Messages.getMsg("not_num"));
                            return;
                        }
                        int page = Integer.parseInt(args[2]);
                        page = Math.abs(page);
                        ArrayList<String> warps = printWarp(args[1], page - 1);
                        if (printWarp(args[1], 0).size() == 0) {
                            sender.sendMessage(prefix + Messages.getMsg("player_nowarp"));
                            return;
                        }
                        if (printWarp(args[1], page - 1).size() == 0) {
                            sender.sendMessage(prefix + Messages.getMsg("last_page"));
                            return;
                        }
                        String msg = prefix + Messages.getMsg("player_warp_list").replace("[0]", args[1]);
                        sender.sendMessage(msg);
                        sender.sendMessage(warps.toString());
                        String msg2 = prefix + Messages.getMsg("player_warp_list_next").replace("[0]", args[1])
                                .replace("[1]", String.valueOf(page + 1));
                        sender.sendMessage(msg2);
                        return;
                    }
            }
            for (String msg : Messages.getList("mywarp_help")) {
                sender.sendMessage(msg);
            }
        });
        return true;
    }

    private void replaceChar(List<String> list, String key, String s) {
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).replace(key, s);
            list.set(i, str);
        }
    }

    private ArrayList<String> printAll(int page) {
        ArrayList<String> results = new ArrayList<>();
        for (int i = page * 20; i < page * 20 + 20 && i < WarpUtil.warps.size(); i++) {
            results.add(WarpUtil.warps.get(i).getName());
        }
        return results;
    }

    private ArrayList<String> printWarp(String name, int page) {
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> warps = new ArrayList<>();
        for (Warp w : WarpUtil.warps) {
            if (w.getOwner().getName().equalsIgnoreCase(name)) {
                warps.add(w.getName());
            }
        }
        for (int i = page * 20; i < page * 20 + 20 && i < warps.size(); i++) {
            results.add(warps.get(i));
        }
        return results;
    }

    private boolean notNumeric(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}
