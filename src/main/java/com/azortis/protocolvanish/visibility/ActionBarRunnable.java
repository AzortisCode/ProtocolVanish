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

package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionBarRunnable implements Runnable {

    private final ProtocolVanish plugin;

    public ActionBarRunnable(ProtocolVanish plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    @Override
    public void run() {
        if (plugin.getSettingsManager().getMessageSettings().getDisplayActionBar()) {
            for (UUID uuid : plugin.getVisibilityManager().getVanishedPlayers()) {
                if (Bukkit.getPlayer(uuid) != null && Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(uuid))) {
                    try {
                        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.CHAT);
                        packetContainer.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
                        packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText
                                (ChatColor.translateAlternateColorCodes('&',
                                        plugin.getSettingsManager().getMessageSettings().getMessage("actionBarMsg"))));
                        ProtocolLibrary.getProtocolManager().sendServerPacket(Bukkit.getPlayer(uuid), packetContainer);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
