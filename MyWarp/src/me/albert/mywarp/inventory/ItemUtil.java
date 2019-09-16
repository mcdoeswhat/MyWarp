package me.albert.mywarp.inventory;

import me.albert.mywarp.Warp;
import me.albert.mywarp.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemUtil {

    private static ItemStack getSkull(){
        ItemStack skull;
        if (getVersion() > 12) {
            skull = new ItemStack(Material.valueOf("PLAYER_HEAD"));
        } else {
            skull = new ItemStack(Objects.requireNonNull(Material.getMaterial("SKULL_ITEM")), 1, (short)SkullType.PLAYER.ordinal());
        }

        return skull;
    }


    static ItemStack getIcon(Warp warp){
        ItemStack icon = getHead(getRandom());
        if (warp.getTexture() != null){
            icon = getHead(warp.getTexture());
        }
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(Messages.getMsg("inventory.items.warp_icon.name").replace("%name%",warp.getName()));
        List<String> lore = Messages.getList("inventory.items.warp_icon.lore");
        replaceChar(lore,"%name%",warp.getName());
        replaceChar(lore,"%visits%",String.valueOf(warp.getVisitors().size()));
        replaceChar(lore,"%owner%",String.valueOf(warp.getOwner().getName()));
        replaceChar(lore,"%world%",String.valueOf(warp.getLocation().getWorld().getName()));
        replaceChar(lore,"%X%",String.valueOf((int)warp.getLocation().getX()));
        replaceChar(lore,"%Y%",String.valueOf((int)warp.getLocation().getY()));
        replaceChar(lore,"%Z%",String.valueOf((int)warp.getLocation().getZ()));
        meta.setLore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    static String getHeadValue(String name){
        try {
            String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
            String uid = subString(result, "{\"id\":\"", "\",\"");
            String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
            String value = subString(signature, "\"value\":\"", "\"}]}");
            String decoded = new String(Base64.getDecoder().decode(value));
            String skinURL = subString(decoded, "\"url\":\"", "\"}}}");
            byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
            return new String(Base64.getEncoder().encode(skinByte));
        } catch (Exception ignored){ }
        return null;
    }

    private static String getRandom(){
        List<String> heads = Messages.getList("inventory.randomheads");
        Random random = new Random();
        int i = random.nextInt(heads.size());
        return heads.get(i);
    }


    private static String subString(String str, String strStart, String strEnd) {

        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);
        return str.substring(strStartIndex, strEndIndex).substring(strStart.length());
    }

    private static String getURLContent(String urlStr) {
        URL url;
        BufferedReader in = null;
        StringBuilder sb = new StringBuilder();
        try{
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8) );
            String str;
            while((str = in.readLine()) != null) {
                sb.append( str );
            }
        } catch (Exception ignored) { }
        finally{
            try{
                if(in!=null) {
                    in.close();
                }
            }catch(IOException ignored) { }
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    private static ItemStack getHead(String value) {
        ItemStack skull = getSkull();
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(skull,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    private static void replaceChar(List<String> list,String key,String s){
        for (int i = 0;i<list.size();i++){
            String str = list.get(i).replace(key,s);
            list.set(i,str);
        }
    }

   static ItemStack getItem(String key){
        ItemStack is = new ItemStack(Material.STONE);
        ItemMeta meta = is.getItemMeta();
        try {
            is.setType(Material.valueOf(Messages.getMsg("inventory.items."+key+".material")));
        } catch (Exception ignored){}
        meta.setDisplayName(Messages.getMsg("inventory.items."+key+".name"));
        meta.setLore(Messages.getList("inventory.items."+key+".lore"));
        is.setItemMeta(meta);
        return is;
    }

    static ItemStack getPrev(int page){
        if (page != 0){
            return getItem("prev_page");
        }
        return new ItemStack(Material.AIR);
    }

    static ItemStack getNext(int page){
        if (page < MyWarpInv.invSize-1){
            return getItem("next_page");
        }
        return new ItemStack(Material.AIR);
    }

    private static int getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        name = (name.substring(name.lastIndexOf('.') + 1) + ".").substring(3);
        return Integer.valueOf(name.substring(0, name.length() - 4));
    }
}
