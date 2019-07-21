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
import com.azortis.protocolvanish.settings.MessageSettingsWrapper;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

@SuppressWarnings("all")
public class VisibilityChanger {

    private ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;

    VisibilityChanger(ProtocolVanish plugin){
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
    }

    void vanishPlayer(UUID uuid){
        Bukkit.getPlayer(uuid).setMetadata("vanished", new FixedMetadataValue(plugin, true));
        for (Player player : Bukkit.getOnlinePlayers()){
            if(plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, true)){
                player.hidePlayer(plugin, Bukkit.getPlayer(uuid));
                sendPlayerInfoPacket(player, Bukkit.getPlayer(uuid), true);
                sendEntityDestroyPacket(player, Bukkit.getPlayer(uuid));
                if(messageSettings.getBroadCastFakeQuitOnVanish())player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("vanishMessage").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
            }else if(messageSettings.getAnnounceVanishStateToAdmins() && player != Bukkit.getPlayer(uuid)){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("vanishMessageWithPerm").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
            }
            if(!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers())player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("vanishMessage").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
        }
    }

    void showPlayer(UUID uuid){
        Bukkit.getPlayer(uuid).setMetadata("vanished", new FixedMetadataValue(plugin, false));
        for (Player player : Bukkit.getOnlinePlayers()){
            if(plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, false)){
                player.showPlayer(plugin, Bukkit.getPlayer(uuid));
                sendPlayerInfoPacket(player, Bukkit.getPlayer(uuid), false);
                sendSpawnPlayerPacket(player, Bukkit.getPlayer(uuid));
                if(messageSettings.getBroadCastFakeJoinOnReappear())player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("reappearMessage").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
            }else if(messageSettings.getAnnounceVanishStateToAdmins() && player != Bukkit.getPlayer(uuid)){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("reappearMessageWithPerm").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
            }
            if(!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers())player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("reappearMessage").replaceAll("\\{player}", Bukkit.getPlayer(uuid).getName())));
        }
    }

    private void sendSpawnPlayerPacket(Player receiver, Player vanishedPlayer){
        if(ProtocolLibrary.getProtocolManager().getEntityTrackers(vanishedPlayer).contains(receiver)){
            ProtocolLibrary.getProtocolManager().updateEntity(vanishedPlayer, Collections.singletonList(receiver));
        }
    }

    private void sendEntityDestroyPacket(Player receiver, Player vanishedPlayer){
        if(ProtocolLibrary.getProtocolManager().getEntityTrackers(vanishedPlayer).contains(receiver)) {
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

            int[] entityIds = new int[]{vanishedPlayer.getEntityId()};
            packetContainer.getIntegerArrays().write(0, entityIds);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPlayerInfoPacket(Player receiver, Player vanishedPlayer, boolean vanished){
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, vanished ? EnumWrappers.PlayerInfoAction.REMOVE_PLAYER : EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        int ping = plugin.getAzortisLib().getCraftManager().getPlayer().getPing(vanishedPlayer);
        GameMode gameMode = vanishedPlayer.getGameMode();
        PlayerInfoData pid = new PlayerInfoData(WrappedGameProfile.fromPlayer(vanishedPlayer), ping, EnumWrappers.NativeGameMode.fromBukkit(gameMode), WrappedChatComponent.fromText(vanishedPlayer.getPlayerListName()));
        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
        try{
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
