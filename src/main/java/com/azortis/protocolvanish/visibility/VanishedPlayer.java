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

import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class VanishedPlayer {

    private Player player;
    private ProtocolVanish plugin;
    private Collection<Player> hiddenFrom = new ArrayList<>();

    VanishedPlayer(Player player, ProtocolVanish plugin){
        this.player = player;
        this.plugin = plugin;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean isVanished(Player viewer){
        return hiddenFrom.contains(viewer);
    }

    public Collection<Player> getHiddenFrom(){
        return hiddenFrom;
    }

    public void clearHiddenFrom(){
        hiddenFrom.clear();
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
        if(hiddenFrom.contains(viewer) && vanished)return false;
        if(!hiddenFrom.contains(viewer) && vanished){
            if(plugin.getPermissionManager().hasPermissionToSee(this.player, viewer)){
                return false;
            }
            hiddenFrom.add(viewer);
            return true;
        }else if(hiddenFrom.contains(viewer) && !vanished){
            hiddenFrom.remove(viewer);
            return true;
        }
        return false;
    }

}
