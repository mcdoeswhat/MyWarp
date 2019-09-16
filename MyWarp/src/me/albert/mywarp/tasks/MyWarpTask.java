package me.albert.mywarp.tasks;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import me.albert.mywarp.hooks.Import;
import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyWarpTask {
    private static Plugin mywarp = MyWarp.getInstance();
    private static int importWarps;
    private static int purgeWarps;
    public static AtomicBoolean isPurging = new AtomicBoolean(false);
    public static AtomicBoolean isImporting = new AtomicBoolean(false);

    public static void purgeWarps(CommandSender sender){
        Bukkit.getScheduler().cancelTask(purgeWarps);
        purgeWarps = Bukkit.getScheduler().runTaskAsynchronously(mywarp, () -> {
            isPurging.set(true);
            String msg = Messages.getMsg("prefix")+Messages.getMsg("purge_warp").replace("[0]",
                    String.valueOf(WarpUtil.purgeWarp()));
            sender.sendMessage(msg);
            isPurging.set(false);
            MyWarpInv.loadInventory();
                }
        ).getTaskId();
    }

    public static void importWarps(CommandSender sender){
        Bukkit.getScheduler().cancelTask(importWarps);
        importWarps = Bukkit.getScheduler().runTaskAsynchronously(mywarp, () -> {
                    if (isImporting.get()){ return; }
                    isImporting.set(true);
                    String result = Messages.getMsg("prefix")+ Messages.getMsg("import").replace("[0]", Objects.requireNonNull(Import.getHookedPlugin())
                    ).replace("[1]",String.valueOf(Import.startImport()));
                    sender.sendMessage(result);
                    isImporting.set(false);
                    MyWarpInv.loadInventory();
        }
        ).getTaskId();
    }
}
