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
import com.azortis.protocolvanish.VanishPlayer;
import com.azortis.protocolvanish.api.PlayerReappearEvent;
import com.azortis.protocolvanish.api.PlayerVanishEvent;
import com.azortis.protocolvanish.visibility.packetlisteners.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class VisibilityManager {

    private final ProtocolVanish plugin;
    private VisibilityChanger visibilityChanger;

    private Collection<UUID> vanishedPlayers = new ArrayList<>();
    private HashMap<Player, Collection<Player>> vanishedFromMap = new HashMap<>();

    private final HashMap<UUID, Collection<String>> bypassFilterPackets = new HashMap<>(); // For the entityDestroy packet.

    public VisibilityManager(ProtocolVanish plugin) {
        this.plugin = plugin;
        this.visibilityChanger = new VisibilityChanger(plugin);
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new PlayerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new TabCompletePacketListener(plugin));
        protocolManager.addPacketListener(new GeneralEntityPacketListener(plugin));
        protocolManager.addPacketListener(new NamedSoundEffectPacketListener(plugin));
        protocolManager.addPacketListener(new WorldParticlesPacketListener(plugin));
        new ActionBarRunnable(plugin);
    }

    /**
     * Make a player vanish or reappear.
     *
     * @param uuid     The {@link UUID} of player you wan't to vanish.
     * @param vanished If the player should vanish.
     */
    public void setVanished(UUID uuid, boolean vanished) {
        if (vanishedPlayers.contains(uuid) && vanished) return;
        if (vanished) {
            PlayerVanishEvent playerVanishEvent = new PlayerVanishEvent(Bukkit.getPlayer(uuid));
            Bukkit.getServer().getPluginManager().callEvent(playerVanishEvent);
            if (!playerVanishEvent.isCancelled()) {
                VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
                if (vanishPlayer == null) return; //TODO Log that a player can not vanish if player has no permission.
                vanishedPlayers.add(uuid);
                vanishPlayer.setVanished(true);
                plugin.getStorageManager().saveVanishPlayer(vanishPlayer);
                vanishedFromMap.put(vanishPlayer.getPlayer(), new ArrayList<>());
                visibilityChanger.vanishPlayer(uuid);
                plugin.getStorageManager().updateServerInfo();
            }
        } else {
            PlayerReappearEvent playerReappearEvent = new PlayerReappearEvent(Bukkit.getPlayer(uuid));
            Bukkit.getServer().getPluginManager().callEvent(playerReappearEvent);
            if (!playerReappearEvent.isCancelled()) {
                VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
                if (vanishPlayer == null) return;
                vanishedPlayers.remove(uuid);
                vanishPlayer.setVanished(false);
                plugin.getStorageManager().saveVanishPlayer(vanishPlayer);
                visibilityChanger.showPlayer(uuid);
                clearVanishedFrom(vanishPlayer.getPlayer());
                plugin.getStorageManager().updateServerInfo();
            }
        }
    }

    /**
     * Set the player vanished from the viewer.
     *
     * @param hider    The {@link Player} that vanishes.
     * @param viewer   The {@link Player} that views.
     * @param vanished If the hider should be vanished from the viewer.
     * @return If the state has changed.
     */
    public boolean setVanished(Player hider, Player viewer, boolean vanished) {
        if (hider == null || viewer == null) return false;
        if (viewer == hider) return false;
        if (vanishedFromMap.get(hider).contains(viewer) && vanished) return false;
        if (!vanishedFromMap.get(hider).contains(viewer) && vanished) {
            if (plugin.getPermissionManager().hasPermissionToSee(hider, viewer)) return false;
            vanishedFromMap.get(hider).add(viewer);
            return true;
        } else if (vanishedFromMap.get(hider).contains(viewer) && !vanished) {
            vanishedFromMap.get(hider).remove(viewer);
            return true;
        }
        return false;
    }

    /**
     * Called when a player joins the server vanished.
     *
     * @param player The player that is vanished.
     */
    public void joinVanished(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        vanishedFromMap.put(player, new ArrayList<>());
    }

    /**
     * Called when a player leaves the server vanished.
     *
     * @param player The player that is vanished.
     */
    public void leaveVanished(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        clearVanishedFrom(player);
    }

    /**
     * Check is a certain {@link Player} is vanished from a certain viewer.
     *
     * @param hider  The player that is hiding
     * @param viewer The player that is viewing
     * @return If the hider is vanished from the viewer.
     */
    public boolean isVanishedFrom(Player hider, Player viewer) {
        if (vanishedFromMap.containsKey(hider)) return vanishedFromMap.get(hider).contains(viewer);
        return false;
    }

    /**
     * Clears a {@link Player} from the vanishedFrom map
     *
     * @param player The player you want to remove from the map.
     */
    public void clearVanishedFrom(Player player) {
        if (!vanishedFromMap.containsKey(player)) vanishedFromMap.remove(player);
    }

    /**
     * Check if a certain player is vanished
     *
     * @param uuid The {@link UUID} of the player.
     * @return if the player is vanished.
     */
    public boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    /**
     * Check if a certain player is vanished
     *
     * @param player the {@link Player} instance.
     * @return if the player is vanished.
     */
    public boolean isVanished(Player player) {
        return isVanished(player.getUniqueId());
    }

    /**
     * Get the {@link Collection} of {@link UUID} of online vanished players.
     *
     * @return A collection of vanished player's their {@link UUID}
     */
    public Collection<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    /**
     * Get a {@link Player} instance from an entityId
     *
     * @param entityId The entityId of supposed player.
     * @param world    The {@link World} the player is in.
     * @return The player instance, null if not found.
     */
    public Player getPlayerFromEntityId(int entityId, World world) {
        Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityId);

        if (entity instanceof Player) {
            return (Player) entity;
        }
        return null;
    }

    /**
     * Adds an packet to the bypassFilter list.
     *
     * @param receiverUUID The UUID of the {@link Player} that receives the packet.
     * @param vanishedUUID The UUID of the {@link Player} that the packet is about.
     * @param packetType The type of the packet.
     */
    public void addPacketToBypassFilterList(UUID receiverUUID, UUID vanishedUUID, String packetType){
        if(!bypassFilterPackets.containsKey(receiverUUID)) bypassFilterPackets.put(receiverUUID, new ArrayList<>());
        bypassFilterPackets.get(receiverUUID).add(vanishedUUID.toString() + " " + packetType);
    }

    /**
     * Removes a packet from the bypassFilter list.
     *
     * @param receiverUUID The UUID of the {@link Player} that receives the packet.
     * @param vanishedUUID The UUID of the {@link Player} that the packet is about.
     * @param packetType The type of the packet.
     */
    public void removePacketFromBypassFilterList(UUID receiverUUID, UUID vanishedUUID, String packetType){
        bypassFilterPackets.get(receiverUUID).remove(vanishedUUID.toString() + " " + packetType);
        if(bypassFilterPackets.get(receiverUUID).isEmpty())bypassFilterPackets.remove(receiverUUID);
    }

    /**
     * Check if a packet should be filtered.
     *
     * @param receiverUUID The UUID of the {@link Player} that receives the packet.
     * @param vanishedUUID The UUID of the {@link Player} that the packet is about.
     * @param packetType The type of the packet.
     * @return If the packet shouldn't be filtered
     */
    public boolean bypassFilter(UUID receiverUUID, UUID vanishedUUID, String packetType){
        if(!bypassFilterPackets.containsKey(receiverUUID))return false;
        return bypassFilterPackets.get(receiverUUID).contains(vanishedUUID.toString() + " " + packetType);
    }

}
