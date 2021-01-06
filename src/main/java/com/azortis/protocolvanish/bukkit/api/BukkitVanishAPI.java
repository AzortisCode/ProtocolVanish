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

package com.azortis.protocolvanish.bukkit.api;

import com.azortis.protocolvanish.api.VanishAPI;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class BukkitVanishAPI implements VanishAPI {

    private final ProtocolVanish plugin;

    public BukkitVanishAPI(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    @Override
    public Collection<UUID> getVanishedPlayers() {
        return plugin.getVisibilityManager().getVanishedPlayers();
    }

    @Override
    public Collection<UUID> getOnlineVanishedPlayers() {
        return plugin.getVisibilityManager().getOnlineVanishedPlayers();
    }

    @Override
    public boolean isVanished(UUID playerUUID) {
        return plugin.getVisibilityManager().isVanished(playerUUID);
    }

    @Override
    public void setVanished(UUID playerUUID, boolean vanished) {
        if(canVanish(playerUUID)){
            plugin.getVisibilityManager().setVanished(playerUUID, vanished);
        }
    }

    @Override
    public boolean canVanish(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)return false;
        return plugin.getPermissionManager().hasPermissionToVanish(player);
    }

    @Override
    public boolean canSee(UUID viewerUUID, UUID hiderUUID) {
        Player viewer = Bukkit.getPlayer(viewerUUID);
        Player hider = Bukkit.getPlayer(hiderUUID);
        if(viewer == null || hider == null)return false;
        return plugin.getPermissionManager().hasPermissionToSee(hider, viewer);
    }
}
