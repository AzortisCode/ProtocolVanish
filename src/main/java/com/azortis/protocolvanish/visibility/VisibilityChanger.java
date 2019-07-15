package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class VisibilityChanger {

    private ProtocolVanish plugin;

    VisibilityChanger(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    void vanishPlayer(UUID uuid){
        for (Player player : Bukkit.getOnlinePlayers()){
            if(plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, true)){
                sendPlayerInfoPacket(player, Bukkit.getPlayer(uuid), true);
                sendEntityDestroyPacket(player, Bukkit.getPlayer(uuid));
            }
        }
    }

    void showPlayer(UUID uuid){
        for (Player player : Bukkit.getOnlinePlayers()){
            if(plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, false)){
                sendPlayerInfoPacket(player, Bukkit.getPlayer(uuid), false);
                sendSpawnPlayerPacket(player, Bukkit.getPlayer(uuid));
            }
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
