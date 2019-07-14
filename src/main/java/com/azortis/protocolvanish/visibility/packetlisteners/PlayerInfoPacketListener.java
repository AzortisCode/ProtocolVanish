package com.azortis.protocolvanish.visibility.packetlisteners;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.visibility.VanishedPlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerInfoPacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public PlayerInfoPacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Collection<UUID> onlineVanishedPlayers = plugin.getVisibilityManager().getOnlineVanishedPlayers();
        List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
        playerInfoDataList.removeIf((PlayerInfoData playerInfoData) -> {
            if(onlineVanishedPlayers.contains(playerInfoData.getProfile().getUUID())){
                VanishedPlayer vanishedPlayer = plugin.getVisibilityManager().getVanishedPlayer(playerInfoData.getProfile().getUUID());
                return vanishedPlayer.isHidden(event.getPlayer());
            }
            return false;
        });
        event.getPacket().getPlayerInfoDataLists().write(0, playerInfoDataList);
    }
}
