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

package com.azortis.protocolvanish.bukkit.listeners;

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class PlayerLoginListener implements Listener {

    private final ProtocolVanish plugin;

    public PlayerLoginListener(ProtocolVanish plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        Player player = event.getPlayer();
        if(plugin.getPermissionManager().hasPermissionToVanish(player)){
            if(plugin.getVisibilityManager().isVanished(player)){
                plugin.getVisibilityManager().joinVanished(player);
                player.setMetadata("vanished", new FixedMetadataValue(plugin, true));
                for (Player viewer : Bukkit.getOnlinePlayers())
                    plugin.getVisibilityManager().setVanished(player, viewer, true);
            }else if(!plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> plugin.loadVanishPlayer(player.getUniqueId()));
            }
        }else if(plugin.getVisibilityManager().isVanished(player) && !plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()){
            // Because its not bungee synced, we should just remove every trace of this player in the plugin.
            // If its bungee, because we have not made the visibility manager hide them from others, it is not counted as vanished.
            plugin.getVisibilityManager().getVanishedPlayers().remove(player.getUniqueId());
            plugin.unloadVanishPlayer(player.getUniqueId());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> plugin.getDatabaseManager().getDriver().deleteVanishPlayer(player.getUniqueId()));
        }
        for (UUID uuid : plugin.getVisibilityManager().getVanishedPlayers()) {
            plugin.getVisibilityManager().setVanished(Bukkit.getPlayer(uuid), player, true);
        }
    }

}
