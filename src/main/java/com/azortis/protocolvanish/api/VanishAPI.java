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

package com.azortis.protocolvanish.api;

import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("all")
public class VanishAPI {

    private static ProtocolVanish plugin;

    public static Collection<UUID> getVanishedPlayers(){
        return plugin.getVisibilityManager().getVanishedPlayers();
    }

    public static boolean isVanished(UUID uuid){
        return plugin.getVisibilityManager().isVanished(uuid);
    }

    public static boolean isVanished(Player player){
        return isVanished(player.getUniqueId());
    }

    public static void vanishPlayer(UUID uuid){
        plugin.getVisibilityManager().setVanished(uuid, true);
    }

    public static void vanishPlayer(Player player){
        vanishPlayer(player.getUniqueId());
    }

    public static void showPlayer(UUID uuid){
        plugin.getVisibilityManager().setVanished(uuid, false);
    }

    public static void showPlayer(Player player){
        showPlayer(player.getUniqueId());
    }

    public static boolean hasPermissionToSee(Player hider, Player viewer){
        return plugin.getPermissionManager().hasPermissionToSee(hider, viewer);
    }

    public static void setPlugin(ProtocolVanish paramPlugin){
        plugin = paramPlugin;
    }
}
