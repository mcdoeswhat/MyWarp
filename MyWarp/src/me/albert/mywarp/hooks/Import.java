package me.albert.mywarp.hooks;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.earth2me.essentials.Essentials;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.config.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class Import {
    private static Essentials ess;
    public static void loadHooks(){
        if (getPlugin("Essentials")){
            ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            Bukkit.getLogger().info("[MyWarp] §aHooked into Essentials!");
        }
        if (getPlugin("GriefPrevention")){
            Bukkit.getLogger().info("[MyWarp] §aHooked into GriefPrevention!");
        }
        if (getPlugin("Residence")){
            Bukkit.getLogger().info("[MyWarp] §aHooked into Residence!");
        }
    }
    private static boolean getPlugin(String name){
        return Bukkit.getServer().getPluginManager().getPlugin(name) != null;
    }


    public static String getHookedPlugin(){
        if (getPlugin("Essentials")){
            return "Essentials";
        }
        return null;
    }
    public static boolean canImport(){
        return getPlugin("Essentials");
    }

    public static int startImport(){
        int i =0;
        boolean notifyOP = true;
        if (getPlugin("Essentials")) {
            for (String warp : ess.getWarps().getList()) {
                try {
                    String w;
                    OfflinePlayer p;
                    Location loc = ess.getWarps().getWarp(warp);
                    if (getPlugin("GriefPrevention") && GriefPrevention.instance.dataStore.getClaimAt(loc, true, null) != null) {
                        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
                        if (claim.ownerID == null) {
                            continue;
                        }
                        p = Bukkit.getOfflinePlayer(claim.ownerID);
                        w = warp;
                        i++;
                    } else
                    if (getPlugin("Residence") && ResidenceApi.getResidenceManager().getByLoc(loc).getOwnerUUID() != null){
                        p = Bukkit.getOfflinePlayer(ResidenceApi.getResidenceManager().getByLoc(loc).getOwnerUUID());
                        w = warp;
                        i++;
                    } else {
                        OfflinePlayer p1 = Bukkit.getOperators().stream().findFirst().orElse(null);
                        if (p1 == null){
                            if (notifyOP) {
                                Bukkit.getLogger().info(
                                        "§c[MyWarp] No Server Operators found! Warps without claims|" +
                                                "residence in its location will not be imported!");
                                notifyOP = false;
                            }
                            continue;
                        }
                        p = p1;
                        w = warp;
                        i++;
                    }
                    assert p != null;
                    WarpUtil.createWarp(loc, p,w);
                    String msg = Messages.getMsg("prefix")+Messages.getMsg("warp_imported")
                            .replace("[0]",w);
                    Bukkit.getLogger().info(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return i;
    }
}
