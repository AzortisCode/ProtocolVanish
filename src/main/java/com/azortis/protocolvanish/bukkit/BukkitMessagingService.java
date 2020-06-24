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

package com.azortis.protocolvanish.bukkit;

import com.azortis.protocolvanish.common.messaging.MessagingService;
import com.azortis.protocolvanish.common.messaging.MessagingSettings;
import com.azortis.protocolvanish.common.messaging.message.Message;
import com.azortis.protocolvanish.common.messaging.provider.MessagingProvider;
import com.azortis.protocolvanish.common.messaging.provider.SQLMessagingProvider;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class BukkitMessagingService implements MessagingService {

    private final ProtocolVanish plugin;
    private MessagingProvider provider;

    private final Collection<UUID> loadedUUIDs = new ArrayList<>();
    private final Collection<UUID> deniedUUIDs = new ArrayList<>();

    public BukkitMessagingService(ProtocolVanish plugin){
        this.plugin = plugin;
        MessagingSettings messagingSettings = plugin.getSettingsManager().getSettings().getBungeeSettings().getMessagingSettings();
        if(messagingSettings.getMessagingService().equalsIgnoreCase("sql")){
            provider = new SQLMessagingProvider(this, plugin.getDatabaseManager().getDriver());
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, provider.getRunnable(), 0L, 2L);
        }else if(messagingSettings.getMessagingService().equalsIgnoreCase("redis")){
            //provider = new RedisMessagingProvider(this, messagingSettings.getRedisSettings());
            plugin.getLogger().severe("Redis is currently not supported!");
        }else{
            plugin.getLogger().severe("Invalid messaging service!");
        }
    }

    @Override
    public synchronized void consumeMessage(String message) {
        String command = message.split(" ")[0];
        UUID playerUUID = UUID.fromString(message.split(" ")[1]);
        switch (command){
            case "setvanished":
                boolean vanished = Boolean.parseBoolean(message.split(" ")[2]);
                boolean fromBungee = Boolean.parseBoolean(message.split(" ")[3]);
                if(!vanished){
                    plugin.getVisibilityManager().getVanishedPlayers().remove(playerUUID);
                    if(fromBungee){
                        deniedUUIDs.add(playerUUID);
                        Bukkit.getScheduler().runTaskLater(plugin, ()-> deniedUUIDs.remove(playerUUID), 300L);
                    }
                }else{
                    plugin.getVisibilityManager().getVanishedPlayers().add(playerUUID);
                }
            case "load":
                Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
                   plugin.loadVanishPlayer(playerUUID);
                   Bukkit.getScheduler().runTask(plugin, ()-> setLoaded(playerUUID, true));
                });
            case "unload":
                plugin.unloadVanishPlayer(playerUUID);
                setLoaded(playerUUID, false);
            case "update":
                Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> plugin.updateVanishPlayer(playerUUID));
        }
    }

    @Override
    public void postMessage(Message message) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> provider.postMessage(message));
    }

    @Override
    public MessagingProvider getProvider() {
        return provider;
    }

    /**
     * Adds or removes entry for async join threads to know if a player is already loaded or not.
     * Should only be called from main thread.
     *
     * @param playerUUID The uuid of the loaded/unloaded player.
     * @param loaded if the Player is loaded or not.
     */
    public void setLoaded(UUID playerUUID, boolean loaded){
        if(loaded){
            loadedUUIDs.add(playerUUID);
        }else{
            loadedUUIDs.remove(playerUUID);
        }
    }

    /**
     * Checks if the vanishPlayer is loaded in cache.
     * Should not be called from main thread!
     *
     * @param playerUUID The uuid of the player.
     * @return if the vanishPlayer instance is loaded.
     */
    public synchronized boolean isLoaded(UUID playerUUID){
        return loadedUUIDs.contains(playerUUID);
    }

    /**
     * Check if the currently still in memory vanished player
     * should be removed from it because they don't have the required permission
     * on the BungeeCord.
     *
     * @param playerUUID The uuid of the player.
     * @return if the player is denied.
     */
    public synchronized boolean isDenied(UUID playerUUID){
        return deniedUUIDs.contains(playerUUID);
    }

}
