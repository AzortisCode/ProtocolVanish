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
import org.bukkit.entity.Player;

import java.util.*;

public class GeneralEntityPacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public GeneralEntityPacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.NAMED_ENTITY_SPAWN,
                PacketType.Play.Server.ENTITY_DESTROY, PacketType.Play.Server.ANIMATION,
                PacketType.Play.Server.BLOCK_BREAK_ANIMATION, PacketType.Play.Server.ENTITY_STATUS,
                PacketType.Play.Server.ENTITY, PacketType.Play.Server.REL_ENTITY_MOVE,
                PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
                PacketType.Play.Server.ENTITY_HEAD_ROTATION, PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.ATTACH_ENTITY, PacketType.Play.Server.ENTITY_VELOCITY,
                PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.COLLECT,
                PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.ENTITY_EFFECT,
                PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(event.getPacket().getType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN){
            UUID playerUUID = event.getPacket().getUUIDs().read(0);
            if(plugin.getVisibilityManager().getOnlineVanishedPlayers().contains(playerUUID)
                    && plugin.getVisibilityManager().getVanishedPlayer(playerUUID).isVanished(event.getPlayer()))
                event.setCancelled(true);
        }else if(event.getPacket().getType() == PacketType.Play.Server.ENTITY_DESTROY){
            int[] entityIds = event.getPacket().getIntegerArrays().read(0);
            List<Integer> entityIdList = new ArrayList<>();
            for(int entityId : entityIds){
                if(plugin.getVisibilityManager().isVanished(plugin.getVisibilityManager().getPlayerFromEntityID
                        (entityId, event.getPlayer().getWorld()).getUniqueId()) &&
                        !plugin.getVisibilityManager().getVanishedPlayer
                                (plugin.getVisibilityManager().getPlayerFromEntityID(entityId, event.getPlayer().getWorld())
                                        .getUniqueId()).isVanished(event.getPlayer()))entityIdList.add(entityId);
            }
            if(entityIdList.size() >= 1){
                event.getPacket().getIntegerArrays().write(0, entityIdList.stream().mapToInt(i->i).toArray());
                return;
            }
            event.setCancelled(true);
        }else{
            int entityId;
            if(event.getPacket().getType() == PacketType.Play.Server.COLLECT){
                entityId = event.getPacket().getIntegers().read(1);
            }else entityId = event.getPacket().getIntegers().read(0);
            Player player = plugin.getVisibilityManager().getPlayerFromEntityID(entityId, event.getPlayer().getWorld());
            if(player != null
                    && plugin.getVisibilityManager().isVanished(player.getUniqueId())
                    && plugin.getVisibilityManager().getVanishedPlayer(player.getUniqueId()).isVanished(event.getPlayer()))
                event.setCancelled(true);
        }
    }
}
