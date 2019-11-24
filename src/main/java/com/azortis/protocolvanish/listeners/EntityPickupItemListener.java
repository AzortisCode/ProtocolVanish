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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {

    private ProtocolVanish plugin;

    public EntityPickupItemListener(ProtocolVanish plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //TODO Rework event with new playerSettings system
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPickupItem(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(plugin.getVisibilityManager().isVanished(player.getUniqueId())
                    && plugin.getVanishPlayer(player.getUniqueId()).getPlayerSettings().getDisableItemPickUp())event.setCancelled(true);
        }
    }
}
