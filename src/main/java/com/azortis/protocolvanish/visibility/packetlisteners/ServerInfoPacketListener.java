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

package com.azortis.protocolvanish.visibility.packetlisteners;

import com.azortis.protocolvanish.ProtocolVanish;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.Bukkit;

import java.util.*;

public class ServerInfoPacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public ServerInfoPacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.HIGH, PacketType.Status.Server.SERVER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (plugin.getSettingsManager().getVisibilitySettings().getEnabledPacketListeners().contains("ServerInfo")) {
            WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);
            Collection<UUID> onlineVanishedPlayers = plugin.getVisibilityManager().getVanishedPlayers();
            if(plugin.getSettingsManager().getVisibilitySettings().getAdjustOnlinePlayerCount()) {
                int vanishedPlayerCount = onlineVanishedPlayers.size();
                serverPing.setPlayersOnline(Bukkit.getOnlinePlayers().size() - vanishedPlayerCount);
            }
            if(plugin.getSettingsManager().getVisibilitySettings().getAdjustOnlinePlayerList()) {
                List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(serverPing.getPlayers());
                wrappedGameProfiles.removeIf((WrappedGameProfile wrappedGameProfile) -> onlineVanishedPlayers.contains(wrappedGameProfile.getUUID()));
                serverPing.setPlayers(wrappedGameProfiles);
            }
        }
    }
}
