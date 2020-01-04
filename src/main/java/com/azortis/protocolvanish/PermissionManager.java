/*
 * Hides you completely from players on your servers by using packets!
 *     Copyright (C) 2019  Azortis
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azortis.protocolvanish;

import org.bukkit.entity.Player;

public class PermissionManager {

    private final ProtocolVanish plugin;

    PermissionManager(ProtocolVanish plugin) {
        this.plugin = plugin;
    }

    public boolean hasPermission(Player player, Permission permission) {
        if (player.isOp() || player.hasPermission("protocolvanish." + Permission.ADMIN)) return true;
        return player.hasPermission("protocolvanish." + permission);
    }

    public boolean hasPermissionToVanish(Player player) {
        if (player.isOp() || player.hasPermission("protocolvanish." + Permission.ADMIN)) return true;
        return Permission.USE.getPermissionLevel(plugin, player) > 0;
    }

    public boolean hasPermissionToSee(Player hider, Player viewer) {
        if (!plugin.getSettingsManager().getPermissionSettings().getEnableSeePermission()) return false;
        int hiderLevel = Permission.USE.getPermissionLevel(plugin, hider);
        int viewerLevel = Permission.SEE.getPermissionLevel(plugin, viewer);
        return viewerLevel >= hiderLevel;
    }

    public enum Permission {
        USE("use"),
        SEE("see"),
        CHANGE_NIGHT_VISION("bypass.nightvision"),
        CHANGE_DAMAGE("bypass.damage"),
        CHANGE_HUNGER("bypass.hunger"),
        CHANGE_CREATURE_TARGET("bypass.creaturetarget"),
        CHANGE_ITEM_PICKUP("bypass.itempickup"),
        FLY("fly"),
        KEEP_FLY("fly.keep"),
        ADMIN("admin");

        private String permissionNode;

        Permission(String permissionNode) {
            this.permissionNode = permissionNode;
        }

        private int getPermissionLevel(ProtocolVanish plugin, Player player) {
            if (permissionNode.equals("use") || permissionNode.equals("see")) {
                int maxLevel = plugin.getSettingsManager().getPermissionSettings().getMaxLevel();
                int level = player.hasPermission("protocolvanish." + this.permissionNode) ? 1 : 0;
                for (int i = 1; i <= maxLevel; i++) {
                    if (player.hasPermission("protocolvanish." + this.permissionNode + ".level." + i)) {
                        level = i;
                    }
                }
                if (level > 0 && !plugin.getSettingsManager().getPermissionSettings().getEnableLayeredPermissions())
                    return 1;
                return level;
            }
            return 1;
        }
    }

}
