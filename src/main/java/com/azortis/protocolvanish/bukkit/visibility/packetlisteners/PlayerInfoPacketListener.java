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

package com.azortis.protocolvanish.bukkit.visibility.packetlisteners;

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
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
            Player receiver = event.getPlayer();
            Collection<UUID> vanishedPlayers = plugin.getVisibilityManager().getVanishedPlayers();
            List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
            playerInfoDataList.removeIf((PlayerInfoData playerInfoData) -> {
                UUID playerInfoUUID = playerInfoData.getProfile().getUUID();
                if (receiver != Bukkit.getPlayer(playerInfoUUID)
                        && vanishedPlayers.contains(playerInfoUUID)) {
                    if(plugin.getVisibilityManager().bypassFilter(receiver.getUniqueId(), playerInfoUUID, "playerInfo")){
                        plugin.getVisibilityManager().removePacketFromBypassFilterList(receiver.getUniqueId(), playerInfoUUID, "playerInfo");
                        return false;
                    }
                    Player vanishedPlayer = Bukkit.getPlayer(playerInfoUUID);
                    return plugin.getVisibilityManager().isVanishedFrom(vanishedPlayer, event.getPlayer());
                }
                return false;
            });
            event.getPacket().getPlayerInfoDataLists().write(0, playerInfoDataList);
        }
    }

}
