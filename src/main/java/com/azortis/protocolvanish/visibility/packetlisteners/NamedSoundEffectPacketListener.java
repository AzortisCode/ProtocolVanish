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
import com.azortis.protocolvanish.visibility.VanishPlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;

import java.util.UUID;

public class NamedSoundEffectPacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public NamedSoundEffectPacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.NAMED_SOUND_EFFECT);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(plugin.getSettingsManager().getVisibilitySettings().getEnabledPacketListeners().contains("NamedSound")) {
            if (event.getPacket().getSoundCategories().read(0) == EnumWrappers.SoundCategory.PLAYERS) {
                int x = event.getPacket().getIntegers().read(0) / 8;
                int y = event.getPacket().getIntegers().read(1) / 8;
                int z = event.getPacket().getIntegers().read(2) / 8;

                for (UUID uuid : plugin.getVisibilityManager().getOnlineVanishedPlayers()) {
                    VanishPlayer vanishPlayer = plugin.getVisibilityManager().getVanishPlayer(uuid);
                    if (vanishPlayer.isVanished(event.getPlayer()) &&
                            event.getPlayer().getWorld().equals(vanishPlayer.getPlayer().getWorld()) &&
                            vanishPlayer.getPlayer().getLocation().distanceSquared
                                    (new Location(vanishPlayer.getPlayer().getWorld(), x, y, z)) < 2.D)
                        event.setCancelled(true);
                }
            }
        }
    }
}
