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

package com.azortis.protocolvanish.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PermissionManager {

    private final ProtocolVanishProxy plugin;

    public PermissionManager(ProtocolVanishProxy plugin){
        this.plugin = plugin;
    }

    public boolean hasPermission(ProxiedPlayer player, Permission permission) {
        if (player.hasPermission("protocolvanish." + Permission.ADMIN)) return true;
        return player.hasPermission("protocolvanish." + permission);
    }

    public boolean hasPermissionToVanish(ProxiedPlayer player) {
        if (player.hasPermission("protocolvanish." + Permission.ADMIN)) return true;
        return Permission.USE.getPermissionLevel(plugin, player) > 0;
    }

    public boolean hasPermissionToSee(ProxiedPlayer hider, ProxiedPlayer viewer) {
        if (!plugin.getSettingsManager().getProxySettings().getPermissionSettings().useSeePermission()) return false;
        int hiderLevel = Permission.USE.getPermissionLevel(plugin, hider);
        int viewerLevel = Permission.SEE.getPermissionLevel(plugin, viewer);
        return viewerLevel >= hiderLevel;
    }

    public enum Permission{
        USE("use"),
        SEE("see"),
        ADMIN("admin");

        private final String permissionNode;

        Permission(String permissionNode){
            this.permissionNode = permissionNode;
        }

        private int getPermissionLevel(ProtocolVanishProxy plugin, ProxiedPlayer player) {
            if (permissionNode.equals("use") || permissionNode.equals("see")) {
                int maxLevel = plugin.getSettingsManager().getProxySettings().getPermissionSettings().getMaxLevel();
                int level = player.hasPermission("protocolvanish." + this.permissionNode) ? 1 : 0;
                for (int i = 1; i <= maxLevel; i++) {
                    if (player.hasPermission("protocolvanish." + this.permissionNode + ".level." + i)) {
                        level = i;
                    }
                }
                if (level > 0 && !plugin.getSettingsManager().getProxySettings().getPermissionSettings().getUseLayeredPermissions())
                    return 1;
                return level;
            }
            return 0;
        }

    }

}
