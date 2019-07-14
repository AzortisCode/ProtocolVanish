package com.azortis.protocolvanish;

import org.bukkit.entity.Player;

public class PermissionManager {

    private ProtocolVanish plugin;

    public PermissionManager(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    public boolean hasPermission(Player player, Permission permission){
        return player.hasPermission("protocolvanish." + permission);
    }

    public boolean hasPermissionToVanish(Player player){
        return Permission.USE.getPermissionLevel(player) > 0;
    }

    public boolean hasPermissionToSee(Player hider, Player viewer) {
        int hiderLevel = Permission.USE.getPermissionLevel(hider);
        int viewerLevel = Permission.SEE.getPermissionLevel(viewer);
        return viewerLevel >= hiderLevel;
    }

    public enum Permission{
        USE("use"),
        SEE("see"),
        ADMIN("admin");

        private String permissionNode;

        Permission(String permissionNode){
            this.permissionNode = permissionNode;
        }

        private int getPermissionLevel(Player player){
            if(permissionNode.equals("use") || permissionNode.equals("see")){
                int maxLevel = 100;
                int level = player.hasPermission("protocolvanish." + this.permissionNode) ? 1 : 0;
                for (int i = 1; i <= maxLevel; i++) {
                    if(player.hasPermission("protocolvanish." + this.permissionNode + ".level." + i)){
                        level = i;
                    }
                }
                return level;
            }
            return 1;
        }
    }

}
