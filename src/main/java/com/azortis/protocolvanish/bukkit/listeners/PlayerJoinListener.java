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
import com.azortis.protocolvanish.bukkit.VanishPlayer;
import com.azortis.protocolvanish.bukkit.settings.wrappers.MessageSettingsWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinListener implements Listener {

    private final ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;

    public PlayerJoinListener(ProtocolVanish plugin) {
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVisibilityManager().isVanished(player.getUniqueId())) {
            VanishPlayer vanishPlayer = plugin.getVanishPlayer(player.getUniqueId());
            if (vanishPlayer != null && vanishPlayer.getPlayerSettings().doNightVision())
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
            if (plugin.getSettingsManager().getMessageSettings().getHideRealJoinQuitMessages()) {
                event.setJoinMessage("");
                plugin.sendPlayerMessage(player, player, "joinedSilently");
            }
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (plugin.getPermissionManager().hasPermissionToSee(player, viewer) && messageSettings.getAnnounceVanishStateToAdmins() && player != viewer) {
                    plugin.sendPlayerMessage(viewer, player,"otherJoinedSilently");
                }
            }
        }
    }

}
