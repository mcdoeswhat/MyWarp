package me.albert.mywarp;

import me.albert.mywarp.commands.*;
import me.albert.mywarp.config.Messages;
import me.albert.mywarp.hooks.Import;
import me.albert.mywarp.inventory.MyWarpInv;
import me.albert.mywarp.listener.InvType;
import me.albert.mywarp.listener.InventoryListener;
import me.albert.mywarp.tabcompleter.WarpTabCompleter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarp extends JavaPlugin {
    private static MyWarp instance;
    private static Economy econ;
    @Override
    public void onEnable(){
        instance = this;
        if (!setupEconomy()) {
            this.getLogger().severe("Vault not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        Messages.createConfig("Locale_en.yml");
        Messages.createConfig("Locale_zh.yml");
        WarpUtil.loadWarps();
        MyWarpInv.loadInventory();
        registerCommands();
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(),this);
        Import.loadHooks();
        Bukkit.getLogger().info("[MyWarp] Loaded");
    }


    private void registerCommands(){
        getCommand("setwarp").setExecutor(new SetWarp());
        getCommand("dewarp").setExecutor(new DeWarp());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("mywarp").setExecutor(new MyWarpCommand());
        getCommand("warp").setTabCompleter(new WarpTabCompleter());
        getCommand("warplist").setExecutor(new WarpList());

    }
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    @Override
    public void onDisable(){
        instance = null;
        for (Player p :Bukkit.getOnlinePlayers()){
            if (InvType.getType(p.getOpenInventory().getTopInventory()) != null){
                p.closeInventory();
            }
        }
        Bukkit.getLogger().info("[MyWarp] Unloaded");
    }
    public static Economy getEconomy() {return econ;}
    public static MyWarp getInstance(){
        return instance;
    }
}
