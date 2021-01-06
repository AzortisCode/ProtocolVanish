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

import com.azortis.protocolvanish.bukkit.PermissionManager;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.settings.MessageSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ProtocolVanish plugin;

    public PlayerJoinListener(ProtocolVanish plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MessageSettings messageSettings = plugin.getSettingsManager().getSettings().getMessageSettings();
        if(plugin.getVisibilityManager().isVanished(player.getUniqueId())){
            if(messageSettings.getHideRealJoinQuitMessages()){
                event.setJoinMessage("");
                plugin.sendPlayerMessage(player, player, "joinedSilently", null);
            }
            for(Player viewer : Bukkit.getOnlinePlayers()){
                if(plugin.getPermissionManager().hasPermissionToSee(player, viewer) && messageSettings.getAnnounceVanishStateToAdmins() && player != viewer){
                    plugin.sendPlayerMessage(viewer, player,"otherJoinedSilently", null);
                }
            }
        }
        if(plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.ADMIN)
                && plugin.getSettingsManager().getSettings().getMessageSettings().getNotifyUpdates()){
            if(plugin.getUpdateChecker().isUpdateAvailable()){
                player.sendMessage(ChatColor.GREEN + "[ProtocolVanish] A new update is available(" + plugin.getUpdateChecker().getSpigotVersion() + ")");
                player.sendMessage(ChatColor.GREEN + "You can download it here: You can download it here: https://www.spigotmc.org/resources/69445/");
            }else if(plugin.getUpdateChecker().isUnreleased()){
                player.sendMessage(ChatColor.RED + "[ProtocolVanish] You're using an unreleased version(" + plugin.getPluginVersion().getVersionString() + "). Please proceed with caution.");
            }
        }
    }
}
