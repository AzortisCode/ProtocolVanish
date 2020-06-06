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

package com.azortis.protocolvanish.bungee.listeners;

import com.azortis.protocolvanish.bungee.ProtocolVanishProxy;
import com.azortis.protocolvanish.common.messaging.message.UnloadMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    private final ProtocolVanishProxy plugin;

    public PlayerDisconnectListener(ProtocolVanishProxy plugin){
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event){
        ProxiedPlayer player = event.getPlayer();
        if(plugin.getPermissionManager().hasPermissionToVanish(player)){
            plugin.getMessagingService().postMessage(new UnloadMessage(player.getUniqueId()));
        }
    }
}
