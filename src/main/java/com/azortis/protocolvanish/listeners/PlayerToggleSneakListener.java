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

package com.azortis.protocolvanish.listeners;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.api.PlayerReappearEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerToggleSneakListener implements Listener {

    private ProtocolVanish plugin;
    private List<Player> pastSneaks = new ArrayList<>();
    private Map<Player, GameMode> pastGameModeMap = new HashMap<>();

    public PlayerToggleSneakListener(ProtocolVanish plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event){
        if(!event.isCancelled()){
            Player player = event.getPlayer();
            if(plugin.getVisibilityManager().isVanished(player) && plugin.getSettingsManager().getInvisibilitySettings().getSwitchGameMode()){
                if(!pastGameModeMap.containsKey(player)) {
                    if (pastSneaks.contains(player)) {
                        pastSneaks.remove(player);
                        pastGameModeMap.put(player, player.getGameMode());
                        player.setGameMode(GameMode.SPECTATOR);
                        plugin.sendPlayerMessage(player, player, "gameModeSwitched");
                    } else if (player.getGameMode() != GameMode.SPECTATOR) {
                        pastSneaks.add(player);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> pastSneaks.remove(player), 30L);
                    }
                }else{
                    if(pastSneaks.contains(player)){
                        pastSneaks.remove(player);
                        player.setGameMode(pastGameModeMap.get(player));
                        plugin.sendPlayerMessage(player, player, "gameModeSwitched");
                    } else if (player.getGameMode() == GameMode.SPECTATOR){
                        pastSneaks.add(player);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> pastSneaks.remove(player), 30L);
                    }else {
                        pastGameModeMap.remove(player); // Because they are no longer switching between the two game modes.
                    }
                }
            } else if(!plugin.getVisibilityManager().isVanished(player)){
                pastGameModeMap.remove(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerReappear(PlayerReappearEvent event){
        if(!event.isCancelled()){
            Player player = event.getPlayer();
            if(pastGameModeMap.containsKey(player) && player.getGameMode() == GameMode.SPECTATOR){
                player.setGameMode(pastGameModeMap.get(player));
            }
            pastGameModeMap.remove(player);
        }
    }

}
