package me.albert.mywarp.inventory;

import me.albert.mywarp.MyWarp;
import me.albert.mywarp.Warp;
import me.albert.mywarp.WarpUtil;
import me.albert.mywarp.commands.WarpList;
import me.albert.mywarp.config.Messages;
import me.albert.mywarp.listener.InvType;
import me.albert.mywarp.tasks.MyWarpTask;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.albert.mywarp.inventory.ItemUtil.getIcon;
import static me.albert.mywarp.inventory.ItemUtil.getNext;

public class MyWarpInv {
    private static int loadInvTask;
    public static AtomicBoolean isLoading = new AtomicBoolean(false);
    public static ArrayList<Inventory> visitorsSort = new ArrayList<>();
    public static ArrayList<Inventory> createdSort = new ArrayList<>();
    public static ArrayList<Inventory> nameSort = new ArrayList<>();
    public static ArrayList<Inventory> playerSort = new ArrayList<>();
    private static ArrayList<Warp> visitorsTemp = new ArrayList<>(WarpUtil.warps);
    private static ArrayList<Warp> createdTemp = new ArrayList<>(WarpUtil.warps);
    public static ArrayList<Warp> visitorsWarps = new ArrayList<>();
    public static ArrayList<Warp> createdWarps = new ArrayList<>();
    public static ArrayList<Warp> playerWarps = new ArrayList<>();
    public static HashMap<UUID,Inventory> tempInv = new HashMap<>();
    static int invSize = mm(WarpUtil.warps.size());
    private static ArrayList<Player> playersToOpen = new ArrayList<>();
    private static int mm(int a){
        return (a% 45 ==0)?a/45:(a/ 45 +1);
    }

    public static boolean isLoadingGUI(){
        return MyWarpTask.isImporting.get() || MyWarpTask.isPurging.get() || isLoading.get();
    }
    public static void loadInventory(){
        if (!Messages.getBoolean("enable-gui") || isLoadingGUI()){
            return;
        }
        Bukkit.getScheduler().cancelTask(loadInvTask);
        loadInvTask = Bukkit.getScheduler().runTaskAsynchronously(MyWarp.getInstance(), () -> {
            isLoading.set(true);
            invSize = mm(WarpUtil.warps.size());
            playersToOpen.clear();
            for (Player p : Bukkit.getOnlinePlayers()){
                Inventory inv = p.getOpenInventory().getTopInventory();
                if (InvType.getType(inv) != null){
                    playersToOpen.add(p);
                    p.closeInventory();
                }
            }
            visitorsSort.clear();
            visitorsWarps.clear();
            nameSort.clear();
            playerSort.clear();
            tempInv.clear();
            createdSort.clear();
            createdWarps.clear();
            playerWarps.clear();
            createdTemp = new ArrayList<>(WarpUtil.warps);
            visitorsTemp = new ArrayList<>(WarpUtil.warps);
            for (int i =0;i<invSize;i++){
                loadVisitorsSort(i);
                loadCreatedSort(i);
                loadNameSort(i);
                loadPlayersSort(i);
            }
            for (Player p : playersToOpen){
                if (WarpUtil.warps.size() == 0){
                    break;
                }
                p.openInventory(MyWarpInv.visitorsSort.get(0));
            }
            isLoading.set(false);
            for (OfflinePlayer p : WarpList.getWarpUsers()){
                String texture = ItemUtil.getHeadValue(p.getName());
                for (Warp warp : WarpList.getWarps(p)){
                    if (texture != null) {
                        warp.setTexture(texture);
                    }}}
        }).getTaskId();
    }
    private static Inventory warpInv(int page){
        String title = Messages.getMsg("inventory.title").replace("[0]",
                String.valueOf(page+1)).replace("[1]",String.valueOf(invSize));
        return Bukkit.createInventory(null,54,title);
    }

    private static void loadNameSort(int page){
        Inventory warpInv = warpInv(page);
        for (int i = page*45;i<page*45+45 && i<WarpUtil.warps.size();i++){
            Warp warp = WarpUtil.warps.get(i);
            LoadWarpIcon(warpInv, i, warp);
        }
        nameSort.add(getSortInv(page, warpInv,"name_sort"));
    }

    private static void loadPlayersSort(int page){
        loadPlayerWarps();
        Inventory warpInv = warpInv(page);
        for (int i = page*45;i<page*45+45 && i<WarpUtil.warps.size();i++){
            Warp warp = playerWarps.get(i);
            LoadWarpIcon(warpInv, i, warp);
        }
        playerSort.add(getSortInv(page, warpInv,"player_sort"));
    }

    private static void loadVisitorsSort(int page){
        Inventory warpInv = warpInv(page);
        for (int i = page*45;i<page*45+45 && i<WarpUtil.warps.size();i++){
            Warp warp = getMostVisited();
            LoadWarpIcon(warpInv, i, warp);
            visitorsWarps.add(warp);
        }
        visitorsSort.add(getSortInv(page, warpInv,"visitors_sort"));
    }

    private static Inventory getSortInv(int page, Inventory warpInv, String key) {
        ItemStack sortBy = ItemUtil.getItem(key);
        warpInv.setItem(warpInv.getSize()-9,ItemUtil.getPrev(page));
        warpInv.setItem(warpInv.getSize()-1,getNext(page));
        warpInv.setItem(warpInv.getSize()-5,sortBy);
        return warpInv;
    }

    private static void loadCreatedSort(int page){
        Inventory warpInv = warpInv(page);
        for (int i = page*45;i<page*45+45 && i<WarpUtil.warps.size();i++){
            Warp warp = getMostCreated();
            LoadWarpIcon(warpInv, i, warp);
            createdWarps.add(warp);
        }
        createdSort.add(getSortInv(page, warpInv, "created_sort"));
    }

    private static void LoadWarpIcon(Inventory warpInv, int i, Warp warp) {
        ItemStack warpItem = getIcon(warp);
        warpInv.setItem(i%45,warpItem);
    }

    private static Warp getMostCreated(){
        Warp warp = null;
        for (Warp w : createdTemp){
            if (warp == null || w.getTimecreated() > warp.getTimecreated()){
                warp = w;
            }
        }
        createdTemp.remove(warp);
        return warp;
    }
    private static void loadPlayerWarps(){
        for (OfflinePlayer p : WarpList.getWarpUsers()){
            playerWarps.addAll(WarpList.getWarps(p));
        }
    }

    private static Warp getMostVisited(){
        Warp warp = null;
        for (Warp w : visitorsTemp){
            if (warp == null || w.getVisitors().size() > warp.getVisitors().size()){
                warp = w;
            }
        }
        visitorsTemp.remove(warp);
        return warp;
    }
}
