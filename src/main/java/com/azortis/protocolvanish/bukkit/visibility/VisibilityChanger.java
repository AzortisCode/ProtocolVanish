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

package com.azortis.protocolvanish.bukkit.visibility;

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.settings.MessageSettings;
import com.azortis.protocolvanish.bukkit.utils.ReflectionUtils;
import com.azortis.protocolvanish.common.player.VanishPlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class VisibilityChanger {

    private final ProtocolVanish plugin;
    private MessageSettings messageSettings;

    VisibilityChanger(ProtocolVanish plugin){
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getSettings().getMessageSettings();
    }

    void vanishPlayer(UUID uuid){
        Player hider = Bukkit.getPlayer(uuid);
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        if (vanishPlayer == null || hider == null) return;
        hider.setMetadata("vanished", new FixedMetadataValue(plugin, true));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (plugin.getVisibilityManager().setVanished(hider, viewer, true)) {
                sendPlayerInfoPacket(viewer, hider, true);
                sendEntityDestroyPacket(viewer, hider);
                viewer.hidePlayer(plugin, hider);
                if (messageSettings.getBroadCastFakeQuitOnVanish())
                    plugin.sendPlayerMessage(viewer, hider,"vanishMessage", null);
                break;
            } else if(viewer != hider){
                if (messageSettings.getAnnounceVanishStateToAdmins())
                    plugin.sendPlayerMessage(viewer, hider,"vanishMessageWithPerm", null);
                else if (!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers() && messageSettings.getBroadCastFakeQuitOnVanish())
                    plugin.sendPlayerMessage(viewer, hider, "vanishMessage", null);
            }
        }
    }

    void showPlayer(UUID uuid){
        Player hider = Bukkit.getPlayer(uuid);
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        if (vanishPlayer == null || hider == null) return;
        hider.setMetadata("vanished", new FixedMetadataValue(plugin, false));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (plugin.getVisibilityManager().setVanished(hider, viewer, false)) {
                viewer.showPlayer(plugin, hider);
                sendPlayerInfoPacket(viewer, hider, false);
                sendSpawnPlayerPacket(viewer, hider);
                if (messageSettings.getBroadCastFakeJoinOnReappear())
                    plugin.sendPlayerMessage(viewer, hider,"reappearMessage", null);
                break;
            } else if(viewer != hider){
                if (messageSettings.getAnnounceVanishStateToAdmins())
                    plugin.sendPlayerMessage(viewer, hider,"reappearMessageWithPerm", null);
                else if (!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers()  && messageSettings.getBroadCastFakeQuitOnVanish())
                    plugin.sendPlayerMessage(viewer, hider,"reappearMessage", null);
            }
        }
    }

    private void sendSpawnPlayerPacket(Player receiver, Player vanishedPlayer) {
        if (ProtocolLibrary.getProtocolManager().getEntityTrackers(vanishedPlayer).contains(receiver)) {
            ProtocolLibrary.getProtocolManager().updateEntity(vanishedPlayer, Collections.singletonList(receiver));
        }
    }

    private void sendEntityDestroyPacket(Player receiver, Player vanishedPlayer) {
        if (ProtocolLibrary.getProtocolManager().getEntityTrackers(vanishedPlayer).contains(receiver)) {
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
            plugin.getVisibilityManager().addPacketToBypassFilterList(receiver.getUniqueId(), vanishedPlayer.getUniqueId(), "entityDestroy");
            int[] entityId = new int[]{vanishedPlayer.getEntityId()};
            packetContainer.getIntegerArrays().write(0, entityId);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPlayerInfoPacket(Player receiver, Player vanishedPlayer, boolean vanished) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, vanished ? EnumWrappers.PlayerInfoAction.REMOVE_PLAYER : EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        if(vanished){
            plugin.getVisibilityManager().addPacketToBypassFilterList(receiver.getUniqueId(), vanishedPlayer.getUniqueId(), "playerInfo");
        }

        GameMode gameMode = vanishedPlayer.getGameMode();
        PlayerInfoData pid = new PlayerInfoData(WrappedGameProfile.fromPlayer(vanishedPlayer), ReflectionUtils.getPing(vanishedPlayer), EnumWrappers.NativeGameMode.fromBukkit(gameMode), WrappedChatComponent.fromText(vanishedPlayer.getPlayerListName()));
        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    void reload(){
        this.messageSettings = plugin.getSettingsManager().getSettings().getMessageSettings();
    }

}
