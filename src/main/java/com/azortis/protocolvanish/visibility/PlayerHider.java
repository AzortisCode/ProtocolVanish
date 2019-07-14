package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class PlayerHider {

    private ProtocolVanish plugin;

    public PlayerHider(ProtocolVanish plugin){

    }

    public void hidePlayer(UUID uuid){

    }

    public void showPlayer(UUID uuid){

    }

    private void sendEntityDestroyPacket(){

    }

    private void sendPlayerInfoPacket(Player receiver, Player hider, boolean hidden){
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, hidden ? EnumWrappers.PlayerInfoAction.REMOVE_PLAYER : EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        int ping = ProtocolVanish.al.getCraftManager().getPlayer().getPing(hider);
        GameMode gameMode = hider.getGameMode();
        PlayerInfoData pid = new PlayerInfoData(WrappedGameProfile.fromPlayer(hider), ping, EnumWrappers.NativeGameMode.fromBukkit(gameMode), WrappedChatComponent.fromText(hider.getDisplayName()));
        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
        try{
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer);
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }
}
