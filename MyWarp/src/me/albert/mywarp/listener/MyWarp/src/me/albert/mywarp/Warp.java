package me.albert.mywarp;


import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

public interface Warp{

    Location getLocation();

    List<UUID> getVisitors();

    Long getLastvisit();

    Long getTimecreated();

    String getTexture();

    void setTexture(String value);

    void updateLastvisit();

    void addVisitor(UUID uuid);

    String getName();

    OfflinePlayer getOwner();

    void delete();

}
