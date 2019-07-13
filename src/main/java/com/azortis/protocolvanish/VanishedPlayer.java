package com.azortis.protocolvanish;

import org.bukkit.entity.Player;

public class VanishedPlayer {

    private Player player;

    public VanishedPlayer(Player player){
        this.player = player;
    }

    public Player getPlayer(){
        return player;
    }

}
