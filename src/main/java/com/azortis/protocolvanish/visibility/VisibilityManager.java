package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.visibility.packetlisteners.ServerListPacketListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class VisibilityManager {

    private Collection<UUID> vanishedPlayers = new ArrayList<>();

    public VisibilityManager(ProtocolVanish plugin){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerListPacketListener(plugin));
    }

    public Collection<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
}
