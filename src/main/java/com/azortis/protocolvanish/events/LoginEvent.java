package com.azortis.protocolvanish.events;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.visibility.VanishedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Collection;
import java.util.UUID;

public class LoginEvent implements Listener {

    private ProtocolVanish plugin;

    public LoginEvent(ProtocolVanish plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        Collection<UUID> onlineVanishedPlayer = plugin.getVisibilityManager().getOnlineVanishedPlayers();
        for (UUID uuid : onlineVanishedPlayer){
            VanishedPlayer vanishedPlayer = plugin.getVisibilityManager().getVanishedPlayer(uuid);
            vanishedPlayer.setVanished(player, true);
        }
    }

}
