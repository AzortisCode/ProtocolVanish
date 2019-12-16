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
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerInfoPacketListener extends PacketAdapter {

    private final ProtocolVanish plugin;

    public PlayerInfoPacketListener(ProtocolVanish plugin) {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.PLAYER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (plugin.getSettingsManager().getVisibilitySettings().getEnabledPacketListeners().contains("PlayerInfo")) {
            Collection<UUID> vanishedPlayers = plugin.getVisibilityManager().getVanishedPlayers();
            List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
            playerInfoDataList.removeIf((PlayerInfoData playerInfoData) -> {
                if (vanishedPlayers.contains(playerInfoData.getProfile().getUUID())) {
                    Player vanishedPlayer = Bukkit.getPlayer(playerInfoData.getProfile().getUUID());
                    return plugin.getVisibilityManager().isVanishedFrom(vanishedPlayer, event.getPlayer())
                            && event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.REMOVE_PLAYER;
                }
                return false;
            });
            event.getPacket().getPlayerInfoDataLists().write(0, playerInfoDataList);
        }
    }
}
