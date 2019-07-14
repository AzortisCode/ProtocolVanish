package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class VanishStateManager {

    private ProtocolVanish plugin;

    public VanishStateManager(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    public void vanishPlayer(UUID uuid){
        for (Player player : Bukkit.getOnlinePlayers()){
            if(plugin.getVisibilityManager().getVanishedPlayer(uuid).setVanished(player, true)){
                sendPlayerInfoPacket(player, Bukkit.getPlayer(uuid), true);
            }
        }
    }

    public void unVanishPlayer(UUID uuid){

    }

    private void sendEntityDestroyPacket(){

    }

    private void sendPlayerInfoPacket(Player receiver, Player vanishedPlayer, boolean vanished){
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, vanished ? EnumWrappers.PlayerInfoAction.REMOVE_PLAYER : EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        int ping = plugin.getAzortisLib().getCraftManager().getPlayer().getPing(vanishedPlayer);
        GameMode gameMode = vanishedPlayer.getGameMode();
        PlayerInfoData pid = new PlayerInfoData(WrappedGameProfile.fromPlayer(vanishedPlayer), ping, EnumWrappers.NativeGameMode.fromBukkit(gameMode), WrappedChatComponent.fromText(vanishedPlayer.getDisplayName()));
        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
        try{
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
