package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.visibility.packetlisteners.ServerListPacketListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class VisibilityManager {

    private PlayerHider playerHider;

    private Collection<UUID> vanishedPlayers = new ArrayList<>();
    private HashMap<UUID, VanishedPlayer> vanishedPlayerMap = new HashMap<>();
    private PermissionChecker permissionChecker;

    public VisibilityManager(ProtocolVanish plugin){
        this.playerHider = new PlayerHider(plugin);
        this.permissionChecker = new PermissionChecker(plugin);
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerListPacketListener(plugin));
    }

    public void setVanished(UUID uuid, boolean vanished){
        if(vanishedPlayers.contains(uuid) && vanished)return;

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

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }
}
