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
import com.azortis.protocolvanish.visibility.packetlisteners.PlayerInfoPacketListener;
import com.azortis.protocolvanish.visibility.packetlisteners.ServerListPacketListener;
import com.azortis.protocolvanish.visibility.packetlisteners.TabCompletePacketListener;
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
        protocolManager.addPacketListener(new TabCompletePacketListener(plugin));
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
