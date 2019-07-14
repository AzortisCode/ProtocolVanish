package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.entity.Player;

public class PermissionChecker {

    private ProtocolVanish plugin;

    public PermissionChecker(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    public boolean hasPermissionToSee(Player hider, Player viewer){
        int hiderLevel = getPermissionLevel(hider, "use");
        int viewerLevel = getPermissionLevel(viewer, "see");
        return viewerLevel >= hiderLevel;
    }

    private int getPermissionLevel(Player player, String permission){
        int maxLevel = 100;
        int level = player.hasPermission("protocolvanish." + permission) ? 1 : 0;
        for (int i = 1; i <= maxLevel; i++) {
            if(player.hasPermission("protocolvanish." + permission + ".level." + i)){
                level = i;
            }
        }
        return level;
    }

}
