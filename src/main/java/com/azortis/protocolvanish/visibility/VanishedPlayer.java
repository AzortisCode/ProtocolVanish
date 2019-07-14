package com.azortis.protocolvanish.visibility;

import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class VanishedPlayer {

    private Player player;
    private ProtocolVanish plugin;
    private Collection<Player> hiddenFromViewers = new ArrayList<>();

    VanishedPlayer(Player player, ProtocolVanish plugin){
        this.player = player;
        this.plugin = plugin;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isVanished(Player viewer){
        return hiddenFromViewers.contains(viewer);
    }

    /**
     * Set the player vanished from the viewer
     *
     * @param viewer The viewer
     * @param vanished If the player should be vanished
     * @return If the state has changed
     */
    public boolean setVanished(Player viewer, boolean vanished){
        if(viewer == player)return false;
        if(hiddenFromViewers.contains(viewer) && vanished)return false;
        if(!hiddenFromViewers.contains(viewer) && vanished){
            if(plugin.getPermissionManager().hasPermissionToSee(this.player, viewer)){
                return false;
            }
            hiddenFromViewers.add(viewer);
            return true;
        }else if(hiddenFromViewers.contains(viewer) && !vanished){
            hiddenFromViewers.remove(viewer);
            return true;
        }
        return false;
    }

}
