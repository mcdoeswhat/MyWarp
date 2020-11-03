package me.albert.mywarp.listener;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.inventory.MyHolder;
import me.albert.mywarp.inventory.MyWarpInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {
    @EventHandler
    public void onOpen(InventoryOpenEvent e){
        if (InvType.getType(e.getInventory()) != null && MyWarp.getInstance().getConfig().getBoolean("save-opened-inv")){
            MyWarpInv.tempInv.put(e.getPlayer().getUniqueId(),e.getInventory());
        }

    }
    @EventHandler
    public void onClick(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        if (e.getInventory().getHolder() instanceof MyHolder){
            e.setCancelled(true);
        }
        if (InvType.getType(e.getClickedInventory()) != null){
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR
            || !e.getCurrentItem().getItemMeta().hasDisplayName()){
                return;
            }
            InvType type = InvType.getType(e.getInventory());
            assert type != null;
            Player p = (Player) e.getWhoClicked();
            if (e.getSlot() == inv.getSize()-5){
                switch (type){
                    case VISITORS_SORT:
                        int page = MyWarpInv.visitorsSort.indexOf(inv);
                        p.openInventory(MyWarpInv.createdSort.get(page));
                        return;
                    case CREATED_SORT:
                        page = MyWarpInv.createdSort.indexOf(inv);
                        p.openInventory(MyWarpInv.nameSort.get(page));
                        return;
                    case NAME_SORT:
                        page = MyWarpInv.nameSort.indexOf(inv);
                        p.openInventory(MyWarpInv.playerSort.get(page));
                        return;
                    case PLAYER_SORT:
                        page = MyWarpInv.playerSort.indexOf(inv);
                        p.openInventory(MyWarpInv.visitorsSort.get(page));
                        return;
                }
                return;
            }
            if (e.getSlot() == inv.getSize()-9){
                switch (type){
                    case VISITORS_SORT:
                        int page = MyWarpInv.visitorsSort.indexOf(inv)-1;
                        p.openInventory(MyWarpInv.visitorsSort.get(page));
                        return;
                    case CREATED_SORT:
                        page = MyWarpInv.createdSort.indexOf(inv)-1;
                        p.openInventory(MyWarpInv.createdSort.get(page));
                        return;
                    case NAME_SORT:
                        page = MyWarpInv.nameSort.indexOf(inv)-1;
                        p.openInventory(MyWarpInv.nameSort.get(page));
                        return;
                    case PLAYER_SORT:
                        page = MyWarpInv.playerSort.indexOf(inv)-1;
                        p.openInventory(MyWarpInv.playerSort.get(page));
                        return;
                }
                return;
            }
            if (e.getSlot() == inv.getSize()-1){
                switch (type){
                    case VISITORS_SORT:
                        int page = MyWarpInv.visitorsSort.indexOf(inv)+1;
                        p.openInventory(MyWarpInv.visitorsSort.get(page));
                        return;
                    case CREATED_SORT:
                        page = MyWarpInv.createdSort.indexOf(inv)+1;
                        p.openInventory(MyWarpInv.createdSort.get(page));
                        return;
                    case NAME_SORT:
                        page = MyWarpInv.nameSort.indexOf(inv)+1;
                        p.openInventory(MyWarpInv.nameSort.get(page));
                        return;
                    case PLAYER_SORT:
                        page = MyWarpInv.playerSort.indexOf(inv)+1;
                        p.openInventory(MyWarpInv.playerSort.get(page));
                        return;
                }
            }
            switch (type) {
                case CREATED_SORT:
                    Warp warp = MyWarpInv.createdWarps.get(MyWarpInv.createdSort.indexOf(inv) * 45 + e.getSlot());
                    p.performCommand("mwarp " + warp.getName());
                    p.closeInventory();
                    return;
                case VISITORS_SORT:
                    warp = MyWarpInv.visitorsWarps.get(MyWarpInv.visitorsSort.indexOf(inv) * 45 + e.getSlot());
                    p.performCommand("mwarp " + warp.getName());
                    p.closeInventory();
                    return;
                case NAME_SORT:
                    int index = MyWarpInv.nameSort.indexOf(inv) * 45 + e.getSlot();
                    warp = WarpUtil.warps.get(index);
                    p.performCommand("mwarp " + warp.getName());
                    p.closeInventory();
                    return;
                case PLAYER_SORT:
                    warp = MyWarpInv.playerWarps.get(MyWarpInv.playerSort.indexOf(inv)*45+e.getSlot());
                    p.performCommand("mwarp "+warp.getName());
                    p.closeInventory();
            }

        }

    }
}
