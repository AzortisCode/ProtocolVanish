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

public class ServerListPacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public ServerListPacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrappedServerPing ping = event.getPacket().getServerPings().read(0);

        Collection<UUID> onlineVanishedPlayers = plugin.getVisibilityManager().getOnlineVanishedPlayers();
        // TODO add check to see if the online player count needs to be changed
        int vanishedPlayerCount = onlineVanishedPlayers.size();
        ping.setPlayersOnline(Bukkit.getOnlinePlayers().size() - vanishedPlayerCount);
        //TODO add check to see if the player list needs to be changed
        List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(ping.getPlayers());
        wrappedGameProfiles.removeIf((WrappedGameProfile wrappedGameProfile) -> onlineVanishedPlayers.contains(wrappedGameProfile.getUUID()));
        ping.setPlayers(wrappedGameProfiles);
    }
}
