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
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WorldParticlesPacketListener extends PacketAdapter {

    private final ProtocolVanish plugin;

    public WorldParticlesPacketListener(ProtocolVanish plugin) {
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.WORLD_PARTICLES);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (plugin.getSettingsManager().getVisibilitySettings().getEnabledPacketListeners().contains("WorldParticle")) {
            if (event.getPacket().getParticles().read(0) == EnumWrappers.Particle.BLOCK_DUST) {
                float x = event.getPacket().getFloat().read(0);
                float y = event.getPacket().getFloat().read(1);
                float z = event.getPacket().getFloat().read(2);

                Player viewer = event.getPlayer();
                for (UUID uuid : plugin.getVisibilityManager().getVanishedPlayers()) {
                    Player vanishedPlayer = Bukkit.getPlayer(uuid);
                    if (vanishedPlayer != null && plugin.getVisibilityManager().isVanishedFrom(vanishedPlayer,
                            viewer) && viewer.getWorld().equals(vanishedPlayer.getWorld())
                            && vanishedPlayer.getLocation().distanceSquared
                            (new Location(vanishedPlayer.getWorld(), x, y, z)) < 3.0D)
                        event.setCancelled(true);
                }
            }
        }
    }
}
