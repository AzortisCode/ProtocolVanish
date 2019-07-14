package com.azortis.protocolvanish.visibility;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class VanishedPlayer {

    private Player player;
    private Collection<Player> hiddenFrom = new ArrayList<>();

    public VanishedPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isHidden(Player viewer){
        return hiddenFrom.contains(viewer);
    }

    public boolean setHidden(Player viewer, boolean hidden){
        return true;
    }

}
