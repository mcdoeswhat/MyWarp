package me.albert.mywarp;

import me.albert.mywarp.config.Messages;
import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class WarpUtil {
    public static ArrayList<Warp> warps = new ArrayList<>();
    private static MyWarp instance = MyWarp.getInstance();

    public static void loadWarps() {
        long start = System.currentTimeMillis();
        int warpcount = 0;
        warps.clear();
        File folder = new File(instance.getDataFolder() + "/warps");
        if (folder.listFiles() == null){
            return;
        }
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.isDirectory() || file.getName().contains(".yml")) {
                try{
                    warps.add(IWarp.getWarp(file.getName().replace(".yml", "")));
                    warpcount++;
                } catch (Exception e){
                    e.printStackTrace();
                    MyWarp.getInstance().getLogger().warning("Error loading warp: "+file.getName());
                }
            }
        }
        Long end = System.currentTimeMillis()-start;
        String msg = Messages.getMsg("prefix")+Messages.getMsg("warps_loaded")
                .replace("[0]",String.valueOf(warpcount)).replace("[1]",String.valueOf(end));
        Bukkit.getLogger().info(msg);
    }

    public static boolean hasWarp(String name) {
        for (Warp w : warps) {
            if (w.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Warp> getWarps(UUID uuid) {
       ArrayList<Warp> warpS = new ArrayList<>();
        for (Warp w : warps) {
            if (w.getOwner().getUniqueId().toString().equalsIgnoreCase(uuid.toString())) {
                warpS.add(w);
            }
        }
        return warpS;
    }

    public static void createWarp(Location loc, OfflinePlayer p, String name) {
        File ConfigFile = new File(instance.getDataFolder() + "/warps", name+".yml");
        FileConfiguration warp = getWarp(name+".yml");
        warp.set("location.world", loc.getWorld().getName());
        warp.set("location.x", loc.getX());
        warp.set("location.y", loc.getY());
        warp.set("location.z", loc.getZ());
        warp.set("location.yaw", loc.getYaw());
        warp.set("location.pitch", loc.getPitch());
        warp.set("owner", p.getUniqueId().toString());
        warp.set("time-created", System.currentTimeMillis());
        warp.set("visitors", new ArrayList<String>());
        warp.set("last-visit", System.currentTimeMillis());
        try {
            warp.save(ConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        warps.add(new IWarp(name));
        MyWarpInv.loadInventory();
    }

    static FileConfiguration getWarp(String name) {
        File file = new File(instance.getDataFolder() + "/warps", name);
        FileConfiguration Config = new YamlConfiguration();
        try {
            Config.load(file);
        } catch (IOException | InvalidConfigurationException ignored) {
        }
        return Config;
    }

    public static Warp getNearbyWarp(Location loc) {
        for (Warp w : WarpUtil.warps) {
            Location loc2 = w.getLocation();
            if (!loc.getWorld().equals(loc2.getWorld())){
                continue;
            }
            double distance = loc.distance(loc2);
            if (MyWarp.getInstance().getConfig().getInt("warp-distance") > distance) {
                return w;
            }
        }
        return null;
    }

    public static boolean canBuild(Player player){
        BlockBreakEvent e = new BlockBreakEvent(player.getLocation().getBlock(), player);
        Bukkit.getServer().getPluginManager().callEvent(e);
        return !e.isCancelled();
    }

    public static int purgeWarp(){
        int amount = 0;
        for (int i = 0; i<WarpUtil.warps.size();i++) {
            Warp toPurge = null;
            Warp warp = null;
            for (Warp w : WarpUtil.warps) {
                if (getNearbyWarp(w.getLocation()) != null) {
                    String name = Objects.requireNonNull(getNearbyWarp(w.getLocation())).getName();
                    if (w.getName().equalsIgnoreCase(name)){continue;}
                    toPurge = w;
                    warp = getNearbyWarp(w.getLocation());
                    break; } }
            if (toPurge == null){continue;}
            assert warp != null;
            String msg = Messages.getMsg("prefix")+Messages.getMsg("warp_purged")
                    .replace("[0]",toPurge.getName()).replace("[1]",warp.getName());
            Bukkit.getLogger().info(msg);
            toPurge.delete();
            amount++;
        }
        return amount;
    }
}
