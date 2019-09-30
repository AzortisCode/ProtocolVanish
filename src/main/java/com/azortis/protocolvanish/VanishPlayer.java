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

package com.azortis.protocolvanish;

import org.bukkit.entity.Player;

public class VanishPlayer {

    private Player player;
    private boolean vanished;
    private ProtocolVanish plugin;

    //Settings
    private boolean itemPickUp;

    public VanishPlayer(Player player, boolean vanished, ProtocolVanish plugin){
        this.player = player;
        this.vanished = vanished;
        this.plugin = plugin;

        //Apply default settings
        this.itemPickUp = !plugin.getSettingsManager().getInvisibilitySettings().getDisableItemPickup();
    }

    public Player getPlayer(){
        return player;
    }

    public boolean getVanishState(){
        return vanished;
    }

    public void setVanishState(boolean vanished){
        this.vanished = vanished;
    }

    public boolean getItemPickUp() {
        return itemPickUp;
    }

    public void setItemPickUp(boolean itemPickUp) {
        this.itemPickUp = itemPickUp;
    }
}
