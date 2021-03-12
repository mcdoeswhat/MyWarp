package me.albert.mywarp.config;

import me.albert.mywarp.MyWarp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Messages {
    private static MyWarp instance = MyWarp.getInstance();
    private static FileConfiguration config = createConfig("Locale_"+instance.getConfig().getString("lang")+".yml");
    public static String getMsg(String key){
        String result;
        result = config.getString(key);
        if (result == null){
            Bukkit.getLogger().warning("[MyWarp] Config corrupted! key = "+key);
        }
        assert result != null;
        return ChatColor.translateAlternateColorCodes('&',result);
    }
    public static void reloadMsg(){
        config = createConfig("Locale_"+instance.getConfig().getString("lang")+".yml");
    }
    public static List<String> getList(String key){
        List<String> list;
        List<String> result = new ArrayList<>();
        list = config.getStringList(key);
        for (String str:list){
            String colored = ChatColor.translateAlternateColorCodes('&',str);
            result.add(colored);
        }
        return result;
    }

    public static boolean getBoolean(String key){
        return config.getBoolean(key);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static FileConfiguration createConfig(String file) {
        File ConfigFile = new File(instance.getDataFolder(), file);
        if (!ConfigFile.exists()) {
            ConfigFile.getParentFile().mkdirs();
            instance.saveResource(file, false);
        }
        FileConfiguration Config= new YamlConfiguration();
        try {
            Config.load(ConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return Config;
    }
}
