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

import com.azortis.protocolvanish.Metrics;
import com.azortis.protocolvanish.PermissionManager;
import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.VanishPlayer;
import com.azortis.protocolvanish.api.PlayerReappearEvent;
import com.azortis.protocolvanish.api.PlayerVanishEvent;
import com.azortis.protocolvanish.settings.VisibilitySettingsWrapper;
import com.azortis.protocolvanish.visibility.packetlisteners.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class VisibilityManager {

    private ProtocolVanish plugin;
    private VisibilityChanger visibilityChanger;

    private Collection<UUID> vanishedPlayers = new ArrayList<>();
    private HashMap<Player, Collection<Player>> vanishedFromMap = new HashMap<>();

    public VisibilityManager(ProtocolVanish plugin){
        this.plugin = plugin;
        this.visibilityChanger = new VisibilityChanger(plugin);
        validateSettings();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new PlayerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new TabCompletePacketListener(plugin));
        protocolManager.addPacketListener(new GeneralEntityPacketListener(plugin));
        protocolManager.addPacketListener(new NamedSoundEffectPacketListener(plugin));
        protocolManager.addPacketListener(new WorldParticlesPacketListener(plugin));
        new ActionBarRunnable(plugin);
        plugin.getMetrics().addCustomChart(new Metrics.SingleLineChart("players_in_vanish", ()-> vanishedPlayers.size()));
    }

    private void validateSettings(){
        boolean valid = true;
        VisibilitySettingsWrapper visibilitySettings = plugin.getSettingsManager().getVisibilitySettings();
        List<String> enabledPacketListeners = visibilitySettings.getEnabledPacketListeners();
        if(!enabledPacketListeners.contains("GeneralEntity") && enabledPacketListeners.contains("PlayerInfo")){
            enabledPacketListeners.add("GeneralEntity");
            valid = false;
        }
        if(!enabledPacketListeners.contains("ServerInfo") && (visibilitySettings.getAdjustOnlinePlayerCount() || visibilitySettings.getAdjustOnlinePlayerList())){
            enabledPacketListeners.add("ServerInfo");
            valid = false;
        }
        if((!visibilitySettings.getAdjustOnlinePlayerList() || !visibilitySettings.getAdjustOnlinePlayerCount()) && enabledPacketListeners.contains("ServerInfo")){
            enabledPacketListeners.remove("ServerInfo");
            valid = false;
        }
        if(!enabledPacketListeners.contains("PlayerInfo") && enabledPacketListeners.contains("TabComplete")){
            enabledPacketListeners.remove("TabComplete");
            valid = false;
        }
        if(!valid){
            plugin.getAzortisLib().getLogger().warning("You're invisibility settings are invalid, changing some values...");
            visibilitySettings.setEnabledPacketListeners(enabledPacketListeners);
            visibilitySettings.save();
            plugin.getSettingsManager().saveSettingsFile();
        }
    }

    /**
     * Make a player vanish or reappear.
     *
     * @param uuid The uuid of the {@link Player} you wan't to vanish.
     * @param vanished If the player should vanish.
     */
    public void setVanished(UUID uuid, boolean vanished){
        if(vanishedPlayers.contains(uuid) && vanished)return;
        if(vanished){
            PlayerVanishEvent playerVanishEvent = new PlayerVanishEvent(Bukkit.getPlayer(uuid));
            Bukkit.getServer().getPluginManager().callEvent(playerVanishEvent);
            if(!playerVanishEvent.isCancelled()) {
                vanishedPlayers.add(uuid);
                plugin.getVanishPlayer(uuid).setVanish(true);
                vanishedFromMap.put(Bukkit.getPlayer(uuid), new ArrayList<>());
                //plugin.getStorageManager().setVanished(uuid);
                visibilityChanger.vanishPlayer(uuid);
            }
        }else{
            PlayerReappearEvent playerReappearEvent = new PlayerReappearEvent(Bukkit.getPlayer(uuid));
            Bukkit.getServer().getPluginManager().callEvent(playerReappearEvent);
            if(!playerReappearEvent.isCancelled()) {
                vanishedPlayers.remove(uuid);
                plugin.getVanishPlayer(uuid).setVanish(false);
                //plugin.getStorageManager().setVanished(uuid);
                visibilityChanger.showPlayer(uuid);
                clearVanishedFrom(Bukkit.getPlayer(uuid));
            }
        }
    }

    /**
     * Set the player vanished from the viewer.
     *
     * @param hider The {@link Player} that vanishes.
     * @param viewer The {@link Player} that views.
     * @param vanished If the hider should be vanished from the viewer.
     * @return If the state has changed.
     */
    public boolean setVanished(Player hider, Player viewer, boolean vanished){
        if(viewer == hider)return false;
        if(vanishedFromMap.get(hider).contains(viewer) && vanished)return false;
        if(!vanishedFromMap.get(hider).contains(viewer) && vanished){
            if(plugin.getPermissionManager().hasPermissionToSee(hider, viewer))return false;
            vanishedFromMap.get(hider).add(viewer);
            return true;
        }else if(vanishedFromMap.get(hider).contains(viewer) && !vanished){
            vanishedFromMap.get(hider).remove(viewer);
            return true;
        }
        return false;
    }

    /**
     * Check is a certain {@link Player} is vanished from a certain viewer.
     *
     * @param hider The player that is hiding
     * @param viewer The player that is viewing
     * @return If the hider is vanished from the viewer.
     */
    public boolean isVanishedFrom(Player hider, Player viewer){
        return vanishedFromMap.get(hider).contains(viewer);
    }

    /**
     * Clears a {@link Player} from the vanishedFrom map
     *
     * @param player The player you want to remove from the map.
     */
    public void clearVanishedFrom(Player player){
        if(!vanishedFromMap.containsKey(player))vanishedFromMap.remove(player);
    }

    /**
     * Check if a certain player is vanished
     *
     * @param uuid The {@link UUID} of the player.
     * @return if the player is vanished.
     */
    public boolean isVanished(UUID uuid){
        if(!vanishedPlayers.contains(uuid)){
            VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
            if(vanishPlayer != null && vanishPlayer.isVanished())vanishedPlayers.add(uuid);
        }
        return vanishedPlayers.contains(uuid);
    }

    /**
     * Check if a certain player is vanished
     *
     * @param player the {@link Player} instance.
     * @return if the player is vanished.
     */
    public boolean isVanished(Player player){
        return isVanished(player.getUniqueId());
    }

    /**
     * Get the {@link Collection} of vanished players.
     *
     * @return A collection of vanished player's their {@link UUID}
     */
    public Collection<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    /**
     * Get the {@link Collection} of online vanished players.
     *
     * @return A collection of online vanished player's their {@link UUID}
     */
    public Collection<UUID> getOnlineVanishedPlayers(){
        Collection<UUID> onlineVanishedPlayers = new ArrayList<>(vanishedPlayers);
        onlineVanishedPlayers.removeIf((UUID uuid) -> Bukkit.getPlayer(uuid) == null);
        return onlineVanishedPlayers;
    }

    /**
     * Get a {@link Player} instance from an entityId
     *
     * @param entityId The entityId of supposed player.
     * @param world The {@link World} the player is in.
     * @return The player instance, null if not found.
     */
    public Player getPlayerFromEntityID(int entityId, World world){
        Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityId);

        if(entity instanceof Player){
            return (Player)entity;
        }
        return null;
    }

}
