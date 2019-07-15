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
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabCompletePacketListener extends PacketAdapter {

    private ProtocolVanish plugin;

    public TabCompletePacketListener(ProtocolVanish plugin){
        super(plugin, ListenerPriority.HIGH, PacketType.Play.Server.TAB_COMPLETE);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Suggestions suggestions = event.getPacket().getSpecificModifier(Suggestions.class).read(0);
        suggestions.getList().removeIf((Suggestion suggestion) -> {
            if(!suggestion.getText().contains("/")) {
                Player player = Bukkit.getPlayer(suggestion.getText());
                return(player != null && plugin.getVisibilityManager().getVanishedPlayer(player.getUniqueId()).isVanished(event.getPlayer()));
            }
            return false;
        });
        event.getPacket().getSpecificModifier(Suggestions.class).write(0, suggestions);
    }
}
