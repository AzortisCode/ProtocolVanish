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

package com.azortis.protocolvanish.listeners;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.settings.InvisibilitySettingsWrapper;
import com.azortis.protocolvanish.settings.MessageSettingsWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;
    private InvisibilitySettingsWrapper invisibilitySettings;

    public PlayerQuitListener(ProtocolVanish plugin){
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
        this.invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(plugin.getVisibilityManager().isVanished(player.getUniqueId())) {
            plugin.getVisibilityManager().clearVanishedFrom(player);
            player.setMetadata("vanished", new FixedMetadataValue(plugin, false));
            if(invisibilitySettings.getNightVisionEffect())player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            //if(invisibilitySettings.getDisableExpPickup()) ExpReflectionUtil.setExpPickup(player, true);
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
            plugin.getVisibilityManager().setVanished(Bukkit.getPlayer(uuid), player, false);
        }
    }

}
