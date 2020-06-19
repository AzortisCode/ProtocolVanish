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

package com.azortis.protocolvanish.bungee;

import com.azortis.protocolvanish.common.messaging.MessagingService;
import com.azortis.protocolvanish.common.messaging.MessagingSettings;
import com.azortis.protocolvanish.common.messaging.message.Message;
import com.azortis.protocolvanish.common.messaging.message.UnloadMessage;
import com.azortis.protocolvanish.common.messaging.message.VanishMessage;
import com.azortis.protocolvanish.common.messaging.provider.MessagingProvider;
import com.azortis.protocolvanish.common.messaging.provider.SQLMessagingProvider;
import com.azortis.protocolvanish.common.storage.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeMessagingService implements MessagingService {

    private final ProtocolVanishProxy plugin;
    private MessagingProvider provider;

    public BungeeMessagingService(ProtocolVanishProxy plugin){
        this.plugin = plugin;
        MessagingSettings messagingSettings = plugin.getSettingsManager().getProxySettings().getMessagingSettings();
        if(messagingSettings.getMessagingService().equalsIgnoreCase("sql")){
            provider = new SQLMessagingProvider(this, plugin.getDatabaseManager().getDriver());
            plugin.getProxy().getScheduler().schedule(plugin, provider.getRunnable(), 0L, 100L, TimeUnit.MILLISECONDS);
        }else if(messagingSettings.getMessagingService().equalsIgnoreCase("redis")){
            //provider = new RedisMessagingProvider(this, messagingSettings.getRedisSettings());
            plugin.getLogger().severe("Redis is currently not supported!");
        }else{
            plugin.getLogger().severe("Invalid messaging service!");
        }
        plugin.getProxy().getScheduler().schedule(plugin, ()-> {
            Driver driver = plugin.getDatabaseManager().getDriver();
            try(Connection connection = driver.getConnection()){
                Statement statement = connection.createStatement();
                long timeStampsToRemove = System.currentTimeMillis() - 1800000;
                statement.executeUpdate("DELETE FROM " + driver.getTablePrefix() + "messages WHERE timeStamp < " + timeStampsToRemove);
                statement.close();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }, 0L, 1L, TimeUnit.HOURS);
    }

    @Override
    public synchronized void consumeMessage(String message) {
        if(message.startsWith("setvanished")){
            String[] messageArray = message.split(" ");
            UUID playerUUID = UUID.fromString(messageArray[1]);
            boolean vanished = Boolean.parseBoolean(messageArray[2]);
            if(vanished){
                if(plugin.getPermissionManager().hasPermissionToVanish(plugin.getProxy().getPlayer(playerUUID))){
                    plugin.getVanishedPlayers().add(playerUUID);
                }else{
                    plugin.getProxy().getScheduler().runAsync(plugin, ()-> {
                        provider.postMessage(new VanishMessage(playerUUID, false, true));
                        provider.postMessage(new UnloadMessage(playerUUID));
                    });
                }
            }else{
                plugin.getVanishedPlayers().remove(playerUUID);
            }
        }
    }

    @Override
    public void postMessage(Message message) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> provider.postMessage(message));
    }

    public MessagingProvider getProvider() {
        return provider;
    }
}
