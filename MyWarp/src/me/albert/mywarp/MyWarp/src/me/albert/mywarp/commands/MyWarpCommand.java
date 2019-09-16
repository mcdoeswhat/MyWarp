package me.albert.mywarp.commands;

import me.albert.mywarp.IWarp;
import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import me.albert.mywarp.hooks.Import;
import me.albert.mywarp.inventory.MyWarpInv;
import me.albert.mywarp.tasks.MyWarpTask;
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
        String prefix = Messages.getMsg("prefix");
        switch (args.length){
            case 1:
                switch (args[0].toLowerCase()){
                    case "info":
                        sender.sendMessage(prefix+"§a/mywarp info <warp>");
                        return true;
                    case "list":
                        sender.sendMessage(prefix+"§a/mywarp list <player> [page]");
                        return true;
                    case "purge":
                        if (!sender.hasPermission("mywarp.purge")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        sender.sendMessage(prefix+Messages.getMsg("waiting_task"));
                        MyWarpTask.purgeWarps(sender);
                        return true;
                    case "reload":
                        if (!sender.hasPermission("mywarp.reload")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        if (MyWarpInv.isLoading.get()){
                            sender.sendMessage(prefix+Messages.getMsg("loadinggui"));
                            return true;
                        }
                        MyWarp.getInstance().reloadConfig();
                        WarpUtil.loadWarps();
                        MyWarpInv.loadInventory();
                        Messages.reloadMsg();
                        sender.sendMessage(prefix+Messages.getMsg("reload"));
                        return true;
                    case "import":
                        if (!sender.hasPermission("mywarp.import")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        if (!Import.canImport()){
                            sender.sendMessage(prefix+Messages.getMsg("import_error"));
                            return true;
                        }
                        sender.sendMessage(prefix+Messages.getMsg("waiting_task"));
                        MyWarpTask.importWarps(sender);
                        return true;
                    case "listall":
                        if (printAll(0).size() == 0){
                            sender.sendMessage(prefix+Messages.getMsg("empty_warp"));
                            return true;
                        }
                        sender.sendMessage(prefix+Messages.getMsg("warp_list"));
                        sender.sendMessage(printAll(0).toString());
                        String next = prefix+Messages.getMsg("warp_list_next").replace("[0]","2");
                        sender.sendMessage(next);
                        return true;
                    case "gui":
                        if (!sender.hasPermission("mywarp.gui")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        if (!(sender instanceof Player)){
                            sender.sendMessage(prefix+Messages.getMsg("player_only"));
                            return true;
                        }
                        if (!Messages.getBoolean("enable-gui")){
                            sender.sendMessage(prefix+Messages.getMsg("gui_notenable"));
                            return true;
                        }
                        Player p = (Player)sender;
                        if (WarpUtil.warps.size() == 0){
                            sender.sendMessage(prefix+Messages.getMsg("empty_warp"));
                            return true;
                        }
                        if (MyWarpInv.isLoadingGUI()){
                            p.sendMessage(prefix+Messages.getMsg("loadinggui"));
                            return true;
                        }
                        if (MyWarpInv.tempInv.containsKey(p.getUniqueId())){
                            p.openInventory(MyWarpInv.tempInv.get(p.getUniqueId()));
                            return true;
                        }
                        p.openInventory(MyWarpInv.visitorsSort.get(0));
                        return true;
                }
            case 2:
                switch (args[0].toLowerCase()){
                    case "info":
                        if (!sender.hasPermission("mywarp.info")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        if (!WarpUtil.hasWarp(args[1])){
                            String msg = prefix+Messages.getMsg("empty_warp").replace("[0]",args[1]);
                            sender.sendMessage(msg);
                            return true;
                        }
                        Warp warp = IWarp.getWarp(args[1]);
                        List<String> warpInfo = Messages.getList("warp_info");
                        replaceChar(warpInfo,"%name%",warp.getName());
                        replaceChar(warpInfo,"%X%", String.valueOf(warp.getLocation().getX()));
                        replaceChar(warpInfo,"%Y%", String.valueOf(warp.getLocation().getY()));
                        replaceChar(warpInfo,"%Z%", String.valueOf(warp.getLocation().getZ()));
                        replaceChar(warpInfo,"%world%", warp.getLocation().getWorld().getName());
                        replaceChar(warpInfo,"%owner%", warp.getOwner().getName() != null ? warp.getOwner().getName() : "null");
                        replaceChar(warpInfo,"%visits%", String.valueOf(warp.getVisitors().size()));
                        replaceChar(warpInfo,"%create_date%", String.valueOf(new Date(warp.getTimecreated())));
                        replaceChar(warpInfo,"%last_visit_date%", String.valueOf(new Date(warp.getLastvisit())));
                        for (String message : warpInfo){
                            sender.sendMessage(message);
                        }
                        return true;
                    case "list":
                        if (!sender.hasPermission("mywarp.list")){
                            sender.sendMessage(prefix+Messages.getMsg("no_permission"));
                            return true;
                        }
                        ArrayList<String> warps = printWarp(args[1],0);
                        if (warps.size() == 0){
                            sender.sendMessage(prefix+Messages.getMsg("player_nowarp"));
                            return true;
                        }
                        String msg = prefix+Messages.getMsg("player_warp_list").replace("[0]",args[1]);
                        sender.sendMessage(msg);
                        sender.sendMessage(warps.toString());
                        String msg2 = prefix+Messages.getMsg("player_warp_list_next").replace("[0]",args[1])
                                .replace("[1]","2");
                        sender.sendMessage(msg2);
                        return true;
                    case "listall":
                        if (notNumeric(args[1])){
                            sender.sendMessage(prefix+Messages.getMsg("not_num"));
                            return true;
                        }

                        int page = Integer.parseInt(args[1]);
                        page = Math.abs(page);

                        if ((printAll(page-1).size() == 0)){
                          sender.sendMessage(prefix+ Messages.getMsg("last_page"));
                          return true;
                        }
                        sender.sendMessage(prefix+Messages.getMsg("warp_list"));
                        sender.sendMessage(printAll(page-1).toString());
                        String next = prefix+Messages.getMsg("warp_list_next").replace("[0]",String.valueOf(page+1));
                        sender.sendMessage(next);
                        return true;
                }
            case 3:
                if (args[0].equalsIgnoreCase("list")) {
                    if (notNumeric(args[2])){
                        sender.sendMessage(prefix+Messages.getMsg("not_num"));
                        return true;
                    }
                    int page = Integer.parseInt(args[2]);
                    page = Math.abs(page);
                    ArrayList<String> warps = printWarp(args[1],page-1);
                    if (printWarp(args[1],0).size() == 0){
                        sender.sendMessage(prefix+Messages.getMsg("player_nowarp"));
                        return true;
                    }
                    if (printWarp(args[1],page-1).size() == 0){
                        sender.sendMessage(prefix+Messages.getMsg("last_page"));
                        return true;
                    }
                    String msg = prefix+Messages.getMsg("player_warp_list").replace("[0]",args[1]);
                    sender.sendMessage(msg);
                    sender.sendMessage(warps.toString());
                    String msg2 = prefix+Messages.getMsg("player_warp_list_next").replace("[0]",args[1])
                            .replace("[1]",String.valueOf(page+1));
                    sender.sendMessage(msg2);
                    return true;
                }
        }
        for (String msg : Messages.getList("mywarp_help")){
            sender.sendMessage(msg);
        }
        return true;
    }
    private void replaceChar(List<String> list,String key,String s){
        for (int i = 0;i<list.size();i++){
            String str = list.get(i).replace(key,s);
            list.set(i,str);
        }
    }
    private ArrayList<String> printAll(int page){
        ArrayList<String> results = new ArrayList<>();
        for (int i = page*20;i < page * 20+20 && i<WarpUtil.warps.size();i++){
            results.add(WarpUtil.warps.get(i).getName());
        }
        return results;
    }
    private ArrayList<String> printWarp(String name,int page){
        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> warps = new ArrayList<>();
        for (Warp w : WarpUtil.warps){
            if (w.getOwner().getName().equalsIgnoreCase(name)){
                warps.add(w.getName());
            }
        }
        for (int i = page*20;i < page * 20+20 && i<warps.size();i++){
            results.add(warps.get(i));
        }
        return results;
    }
    private boolean notNumeric(String num){
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e){
            return true;
        }
        return false;
    }
}
