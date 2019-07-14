package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.visibility.packetlisteners.PlayerInfoPacketListener;
import com.azortis.protocolvanish.visibility.packetlisteners.ServerListPacketListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class VisibilityManager {

    private ProtocolVanish plugin;
    private VisibilityChanger visibilityChanger;

    private Collection<UUID> vanishedPlayers = new ArrayList<>();
    private HashMap<UUID, VanishedPlayer> vanishedPlayerMap = new HashMap<>();

    public VisibilityManager(ProtocolVanish plugin){
        this.plugin = plugin;
        this.visibilityChanger = new VisibilityChanger(plugin);
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerListPacketListener(plugin));
        protocolManager.addPacketListener(new PlayerInfoPacketListener(plugin));
    }

    public void setVanished(UUID uuid, boolean vanished){
        if(vanishedPlayers.contains(uuid) && vanished)return;
        if(vanished){
            vanishedPlayers.add(uuid);
            vanishedPlayerMap.put(uuid, new VanishedPlayer(Bukkit.getPlayer(uuid), plugin));
            visibilityChanger.vanishPlayer(uuid);
        }else{
            vanishedPlayers.remove(uuid);
            visibilityChanger.showPlayer(uuid);
            vanishedPlayerMap.remove(uuid);
        }
    }

    public boolean isVanished(UUID uuid){
        return vanishedPlayers.contains(uuid);
    }

    public Collection<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public Collection<UUID> getOnlineVanishedPlayers(){
        Collection<UUID> onlineVanishedPlayers = new ArrayList<>(vanishedPlayers);
        onlineVanishedPlayers.removeIf((UUID uuid) -> Bukkit.getPlayer(uuid) == null);
        return onlineVanishedPlayers;
    }

    public VanishedPlayer getVanishedPlayer(UUID uuid){
        return vanishedPlayerMap.get(uuid);
    }

}
