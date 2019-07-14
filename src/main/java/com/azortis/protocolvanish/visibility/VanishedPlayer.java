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

    public boolean isHidden(Player viewer){
        return hiddenFromViewers.contains(viewer);
    }

    /**
     * Set the player hidden from the viewer
     *
     * @param viewer The viewer
     * @param hidden If the player should be hidden
     * @return If the state has changed
     */
    public boolean setHidden(Player viewer, boolean hidden){
        if(hiddenFromViewers.contains(viewer) && hidden)return false;
        if(!hiddenFromViewers.contains(viewer) && hidden){
            if(plugin.getPermissionManager().hasPermissionToSee(this.player, viewer)){
                return false;
            }
            hiddenFromViewers.add(viewer);
            return true;
        }else if(hiddenFromViewers.contains(viewer) && !hidden){
            hiddenFromViewers.remove(viewer);
            return true;
        }
        return false;
    }

}
