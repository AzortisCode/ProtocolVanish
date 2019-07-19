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
import com.azortis.protocolvanish.visibility.VanishedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Collection;
import java.util.UUID;

public class LoginEvent implements Listener {

    private ProtocolVanish plugin;

    public LoginEvent(ProtocolVanish plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        if(plugin.getVisibilityManager().isVanished(player.getUniqueId())){
            player.setMetadata("vanished", new FixedMetadataValue(plugin, true));
        }
        Collection<UUID> onlineVanishedPlayer = plugin.getVisibilityManager().getOnlineVanishedPlayers();
        for (UUID uuid : onlineVanishedPlayer){
            VanishedPlayer vanishedPlayer = plugin.getVisibilityManager().getVanishedPlayer(uuid);
            vanishedPlayer.setVanished(player, true);
        }
    }

}
