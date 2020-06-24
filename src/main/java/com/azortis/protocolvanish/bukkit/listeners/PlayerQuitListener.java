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
import com.azortis.protocolvanish.bukkit.settings.MessageSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final ProtocolVanish plugin;

    public PlayerQuitListener(ProtocolVanish plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MessageSettings messageSettings = plugin.getSettingsManager().getSettings().getMessageSettings();
        if(plugin.getVisibilityManager().isVanished(player)){
            plugin.getVisibilityManager().leaveVanished(player);
            player.setMetadata("vanished", new FixedMetadataValue(plugin, false));
            if (messageSettings.getHideRealJoinQuitMessages()) {
                event.setQuitMessage("");
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (plugin.getPermissionManager().hasPermissionToSee(player, viewer) && messageSettings.getAnnounceVanishStateToAdmins() && player != viewer) {
                        plugin.sendPlayerMessage(viewer, player,"otherLeftSilently", null);
                    }
                }
            }
        }
        for (UUID uuid : plugin.getVisibilityManager().getVanishedPlayers()) {
            plugin.getVisibilityManager().setVanished(Bukkit.getPlayer(uuid), player, false);
        }
        // Only on spigot only mode, as in BungeeMode it should always have the vanishPlayer cached to make joining more seamless.
        if(!plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled() && plugin.getVanishPlayer(player.getUniqueId()) != null){
            plugin.unloadVanishPlayer(player.getUniqueId());
            if(!plugin.getPermissionManager().hasPermissionToVanish(player)){
                // If they lost the permission during their play session.
                plugin.getVisibilityManager().getVanishedPlayers().remove(player.getUniqueId());
                Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> plugin.getDatabaseManager().getDriver().deleteVanishPlayer(player.getUniqueId()));
            }
        }
    }

}
