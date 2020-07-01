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

package com.azortis.protocolvanish.bukkit.visibility;

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.visibility.packetlisteners.*;
import com.azortis.protocolvanish.common.VanishPlayer;
import com.azortis.protocolvanish.common.messaging.message.VanishMessage;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class VisibilityManager {

    private final ProtocolVanish plugin;
    private final VisibilityChanger visibilityChanger;

    private final Collection<UUID> vanishedPlayers = new ArrayList<>();
    private final HashMap<Player, Collection<Player>> vanishedFromMap = new HashMap<>();

    private final HashMap<UUID, Collection<String>> bypassFilterPackets = new HashMap<>(); // For the entityDestroy packet.

    public VisibilityManager(ProtocolVanish plugin){
        this.plugin = plugin;
        this.visibilityChanger = new VisibilityChanger(plugin);
        this.vanishedPlayers.addAll(plugin.getDatabaseManager().getDriver().getVanishedUUIDs());
        validateSettings();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new ServerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new PlayerInfoPacketListener(plugin));
        protocolManager.addPacketListener(new TabCompletePacketListener(plugin));
        protocolManager.addPacketListener(new GeneralEntityPacketListener(plugin));
        protocolManager.addPacketListener(new NamedSoundEffectPacketListener(plugin));
        protocolManager.addPacketListener(new WorldParticlesPacketListener(plugin));
        new ActionBarRunnable(plugin);
    }

    private void validateSettings(){
        if(plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()){
            plugin.getSettingsManager().getSettings().getVisibilitySettings().getEnabledPacketListeners().remove("TabComplete");
            // Will cause the client to crash on BungeeCord when you try to TabComplete. You rather should use BungeeCord its tab filter.

            if(plugin.getSettingsManager().getSettings().getVisibilitySettings().getEnabledPacketListeners().contains("ServerInfo")){
                plugin.getLogger().warning("You should remove ServerInfo from packet listeners, it might cause issues with BungeeCord");
                plugin.getLogger().warning("You can easily use the server info module from the BungeeCord version!");
            }
            plugin.getSettingsManager().saveSettings();
        }
    }

    /**
     * Make a player vanish or reappear.
     *
     * @param uuid     The {@link UUID} of player you wan't to vanish.
     * @param vanished If the player should vanish.
     */
    public void setVanished(UUID uuid, boolean vanished){
        if (vanishedPlayers.contains(uuid) && vanished) return;
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if(vanishPlayer == null || player == null)return;
        if(vanished){
            vanishedPlayers.add(uuid);
            vanishPlayer.setVanished(true);
            vanishedFromMap.put(player, new ArrayList<>());
            visibilityChanger.vanishPlayer(uuid);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
               if(plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()){
                   plugin.getMessagingService().postMessage(new VanishMessage(uuid, true, false));
               }
                plugin.getDatabaseManager().getDriver().saveVanishPlayer(vanishPlayer);
            });
        }else{
            vanishedPlayers.remove(uuid);
            vanishPlayer.setVanished(false);
            visibilityChanger.showPlayer(uuid);
            clearVanishedFrom(player);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
                if(plugin.getSettingsManager().getSettings().getBungeeSettings().isEnabled()){
                    plugin.getMessagingService().postMessage(new VanishMessage(uuid, false, false));
                }
                plugin.getDatabaseManager().getDriver().saveVanishPlayer(vanishPlayer);
            });
        }
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
     * Get the {@link Collection} of {@link UUID} of vanished players.
     * This is always in sync with database, for bungee support.
     *
     * @return A collection of vanished player's their {@link UUID}
     */
    public Collection<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    /**
     * Get the {@link Collection} of {@link UUID} of online vanished players.
     *
     * @return A collection of online vanished player's their {@link UUID}
     */
    public Collection<UUID> getOnlineVanishedPlayers(){
        Collection<UUID> onlineVanishedPlayers = new ArrayList<>();
        vanishedPlayers.forEach(uuid -> {
            if(Bukkit.getPlayer(uuid) != null)onlineVanishedPlayers.add(uuid);
        });
        return onlineVanishedPlayers;
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
        vanishedFromMap.put(player, new ArrayList<>());
    }

    /**
     * Called when a player leaves the server vanished.
     *
     * @param player The player that is vanished.
     */
    public void leaveVanished(Player player) {
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
     * Check is a certain entityId is vanished from a certain viewer in a certain world.
     *
     * @param entityId  The player that is hiding
     * @param viewer The player that is viewing
     * @return If the hider is vanished from the viewer.
     */
    public boolean isVanishedFrom(int entityId, World world, Player viewer){
        for (Player player : Bukkit.getOnlinePlayers()){
            if(player.getEntityId() == entityId && player.getWorld() == world){
                return isVanishedFrom(player, viewer);
            }
        }
        return false;
    }

    /**
     * Check is a certain player name is vanished from a certain viewer.
     *
     * @param hiderName  The name of the player hiding.
     * @param viewer The player that is viewing
     * @return If the hider is vanished from the viewer.
     */
    public boolean isVanishedFrom(String hiderName, Player viewer){
        if(viewer.getName().equals(hiderName))return false;
        for (Player player : Bukkit.getOnlinePlayers()){
            if(player.getName().equals(hiderName)){
                return isVanishedFrom(player, viewer);
            }
        }
        return false;
    }

    /**
     * Clears a {@link Player} from the vanishedFrom map
     *
     * @param player The player you want to remove from the map.
     */
    private void clearVanishedFrom(Player player) {
        if (!vanishedFromMap.containsKey(player)) vanishedFromMap.remove(player);
    }

    /**
     * Get a {@link Player} instance from an entityId
     *
     * @param entityId The entityId of supposed player.
     * @param world    The {@link World} the player is in.
     * @return The player instance, null if not found.
     */
    public Player getPlayerFromEntityId(int entityId, World world) {
        if(entityId < 0)return null;
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

    /**
     * For internal purposes only!
     */
    public void reload(){
        validateSettings();
        visibilityChanger.reload();
    }

}
