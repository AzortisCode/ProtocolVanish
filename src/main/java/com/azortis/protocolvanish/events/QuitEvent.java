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

package com.azortis.protocolvanish.events;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.settings.MessageSettingsWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class QuitEvent implements Listener {

    private ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;

    public QuitEvent(ProtocolVanish plugin){
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(plugin.getVisibilityManager().isVanished(player.getUniqueId())) {
            plugin.getVisibilityManager().getVanishedPlayer(player.getUniqueId()).clearHiddenFrom();
            player.setMetadata("vanished", new FixedMetadataValue(plugin, false));
            if(messageSettings.getHideRealJoinQuitMessages()){
                event.setQuitMessage("");
                for (Player viewer : Bukkit.getOnlinePlayers()){
                    if(plugin.getPermissionManager().hasPermissionToSee(player, viewer)  && messageSettings.getAnnounceVanishStateToAdmins() && player != viewer){
                        viewer.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("otherLeftSilently").replaceAll("\\{player}", player.getName())));
                    }
                }
            }
        }
        for(UUID uuid : plugin.getVisibilityManager().getOnlineVanishedPlayers()){
            plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, false);
        }
    }

}
