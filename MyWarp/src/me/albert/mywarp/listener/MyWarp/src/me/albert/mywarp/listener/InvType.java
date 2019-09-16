package me.albert.mywarp.listener;

import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.inventory.Inventory;

public enum InvType{
    CREATED_SORT,
    VISITORS_SORT,
    NAME_SORT,
    PLAYER_SORT;
    public static InvType getType(Inventory inv){
        if (MyWarpInv.createdSort.contains(inv)){
            return CREATED_SORT;
        }
        if (MyWarpInv.visitorsSort.contains(inv)){
            return VISITORS_SORT;
        }
        if (MyWarpInv.nameSort.contains(inv)){
            return NAME_SORT;
        }
        if (MyWarpInv.playerSort.contains(inv)){
            return PLAYER_SORT;
        }
        return null;
    }
}
