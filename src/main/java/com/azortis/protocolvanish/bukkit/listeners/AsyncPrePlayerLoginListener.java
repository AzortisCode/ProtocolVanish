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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class AsyncPrePlayerLoginListener implements Listener {

    private final ProtocolVanish plugin;

    public AsyncPrePlayerLoginListener(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
        if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            UUID playerUUID = event.getUniqueId();
            if (plugin.getVisibilityManager().isVanished(playerUUID)) {
                if (plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()) {
                    boolean shouldContinue = true;
                    while (shouldContinue) {
                        if (plugin.getMessagingService().isLoaded(playerUUID)) {
                            shouldContinue = false;
                        } else if (plugin.getMessagingService().isDenied(playerUUID)) {
                            shouldContinue = false;
                        }
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if(plugin.getVanishPlayer(playerUUID) == null){
                        plugin.loadVanishPlayer(playerUUID);
                    }
                }
            }
        }
    }

}
