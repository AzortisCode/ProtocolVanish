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
import com.azortis.protocolvanish.common.player.PlayerSettings;
import com.azortis.protocolvanish.common.player.VanishPlayer;
import com.azortis.protocolvanish.common.messaging.message.LoadMessage;
import com.azortis.protocolvanish.common.messaging.message.VanishMessage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    private final ProtocolVanishProxy plugin;

    public PostLoginListener(ProtocolVanishProxy plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (plugin.getPermissionManager().hasPermissionToVanish(player)) {
            plugin.getLoadedPlayers().add(player.getUniqueId());
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                if (plugin.getDatabaseManager().getDriver().getVanishPlayer(player.getUniqueId()) == null) {
                    VanishPlayer vanishPlayer = new VanishPlayer(player.getUniqueId(), false, new PlayerSettings());
                    plugin.getDatabaseManager().getDriver().createVanishPlayer(vanishPlayer);
                }
                plugin.getMessagingService().postMessage(new LoadMessage(player.getUniqueId(), null));
            });
        } else {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                plugin.getMessagingService().postMessage(new VanishMessage(player.getUniqueId(), false, true));
                plugin.getDatabaseManager().getDriver().deleteVanishPlayer(player.getUniqueId());
            });
        }
    }

}
