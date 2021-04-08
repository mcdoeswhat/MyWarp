package me.albert.mywarp;

import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IWarp implements Warp{
    private final String name;
    private final Location loc;
    private final Long lastvisit;
    private final Long timecreated;
    private final List<UUID> visitors = new ArrayList<>();
    private final UUID owner;
    private final File warpFile;
    private final FileConfiguration warpConfig;
    private final String texture;

    public List<UUID> getVisitors() {return visitors;}

    public Long getLastvisit() {return lastvisit;}

    public Long getTimecreated() {return timecreated;}


    public String getTexture() {
        return texture;
    }

    public void setTexture(String value) {
        warpConfig.set("texture",value);
        saveConfig();
    }

    public void updateLastvisit(){
        warpConfig.set("last-visit",System.currentTimeMillis());
        saveConfig();
    }

    public void addVisitor(UUID uuid){
        if (!visitors.contains(uuid)) {
            ArrayList<String> UUIDs = new ArrayList<>();
            visitors.forEach(uuid1 -> {UUIDs.add(uuid1.toString());});
            UUIDs.add(uuid.toString());
            warpConfig.set("visitors",UUIDs);
            saveConfig();
        }
    }
    private void saveConfig(){
        try {
            warpConfig.save(warpFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() { return name;}

    public Location getLocation(){ return loc;}

    public OfflinePlayer getOwner(){
        return Bukkit.getOfflinePlayer(owner);
    }

    IWarp(String name){
        this.name = name;
        FileConfiguration config = WarpUtil.getWarp(name + ".yml");
        loc = new Location(Bukkit.getWorld(config.getString("location.world")),
                config.getDouble("location.x"),
                config.getDouble("location.y"),
                config.getDouble("location.z"),
                (float)config.getDouble("location.yaw"),
                (float)config.getDouble("location.pitch"));
        lastvisit = config.getLong("last-visit");
        timecreated = config.getLong("time-created");
        owner = UUID.fromString(config.getString("owner"));
        texture = config.getString("texture");
        warpConfig = config;
        warpFile = new File(MyWarp.getInstance().getDataFolder() + "/warps", name + ".yml");
        for (Object uuid : config.getList("visitors")) {
            UUID temp = UUID.fromString(uuid.toString());
            visitors.add(temp);
        }

    }

    public static Warp getWarp(String name) {
            return new IWarp(name);
    }

   @SuppressWarnings("ResultOfMethodCallIgnored")
   public void delete(){
        File warpFile = new File(MyWarp.getInstance().getDataFolder()+"/warps", name+".yml");
        for (int i =0;i <WarpUtil.warps.size();i++){
            Warp w = WarpUtil.warps.get(i);
            if (w.getName().equalsIgnoreCase(name)){
                WarpUtil.warps.remove(w);
            }
        }
        warpFile.delete();
        MyWarpInv.loadInventory();
    }
}
