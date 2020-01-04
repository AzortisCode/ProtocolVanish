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

import com.azortis.protocolvanish.PermissionManager;
import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.VanishPlayer;
import com.azortis.protocolvanish.settings.wrappers.InvisibilitySettingsWrapper;
import com.azortis.protocolvanish.settings.wrappers.MessageSettingsWrapper;
import com.azortis.protocolvanish.utils.ReflectionUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("all")
public class VisibilityChanger {

    private final ProtocolVanish plugin;
    private MessageSettingsWrapper messageSettings;
    private InvisibilitySettingsWrapper invisibilitySettings;

    VisibilityChanger(ProtocolVanish plugin) {
        this.plugin = plugin;
        this.messageSettings = plugin.getSettingsManager().getMessageSettings();
        this.invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
    }

    void vanishPlayer(UUID uuid) {
        Player hider = Bukkit.getPlayer(uuid);
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        if (vanishPlayer == null) return;
        hider.setMetadata("vanished", new FixedMetadataValue(plugin, true));
        if (vanishPlayer.getPlayerSettings().doNightVision())
            hider.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
        if (vanishPlayer.getPlayerSettings().getDisableCreatureTarget()) {
            for (Mob mob : hider.getWorld().getEntitiesByClass(Mob.class)) {
                if (mob.getTarget() == hider) mob.setTarget(null);
            }
        }
        if(invisibilitySettings.getAllowFlight() && plugin.getPermissionManager().hasPermission(hider, PermissionManager.Permission.FLY)){
            hider.setAllowFlight(true);
        }
        if(invisibilitySettings.getSleepingIgnored()){
            hider.setSleepingIgnored(true);
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (plugin.getVisibilityManager().setVanished(hider, viewer, true)) {
                sendPlayerInfoPacket(viewer, hider, true);
                sendEntityDestroyPacket(viewer, hider);
                viewer.hidePlayer(plugin, hider);
                if (messageSettings.getBroadCastFakeQuitOnVanish())
                    plugin.sendPlayerMessage(viewer, hider,"vanishMessage");
            } else if (messageSettings.getAnnounceVanishStateToAdmins() && viewer != hider) {
                plugin.sendPlayerMessage(viewer, hider,"vanishMessageWithPerm");
            }
            if (!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers())
                plugin.sendPlayerMessage(viewer, hider, "vanishMessage");
        }
    }

    void showPlayer(UUID uuid) {
        Player hider = Bukkit.getPlayer(uuid);
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        if (vanishPlayer == null) return;
        hider.setMetadata("vanished", new FixedMetadataValue(plugin, false));
        if (vanishPlayer.getPlayerSettings().doNightVision()) hider.removePotionEffect(PotionEffectType.NIGHT_VISION);
        if(!invisibilitySettings.getKeepFlight() && hider.getGameMode() != GameMode.CREATIVE && !plugin.getPermissionManager().hasPermission(hider, PermissionManager.Permission.KEEP_FLY)){
            hider.setAllowFlight(false);
        }
        if(invisibilitySettings.getSleepingIgnored()){
            hider.setSleepingIgnored(false);
        }
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (plugin.getVisibilityManager().setVanished(hider, viewer, false)) {
                viewer.showPlayer(plugin, hider);
                sendPlayerInfoPacket(viewer, hider, false);
                sendSpawnPlayerPacket(viewer, hider);
                if (messageSettings.getBroadCastFakeJoinOnReappear())
                    plugin.sendPlayerMessage(viewer, hider,"reappearMessage");
            } else if (messageSettings.getAnnounceVanishStateToAdmins() && viewer != hider) {
                plugin.sendPlayerMessage(viewer, hider,"reappearMessageWithPerm");
            }
            if (!messageSettings.getSendFakeJoinQuitMessagesOnlyToUsers())
                plugin.sendPlayerMessage(viewer, hider,"reappearMessage");
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

}
