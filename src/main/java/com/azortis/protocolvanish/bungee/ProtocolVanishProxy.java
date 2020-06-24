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

import com.azortis.protocolvanish.bungee.listeners.PlayerDisconnectListener;
import com.azortis.protocolvanish.bungee.listeners.PostLoginListener;
import com.azortis.protocolvanish.bungee.listeners.ProxyPingListener;
import com.azortis.protocolvanish.bungee.listeners.TabCompleteResponseListener;
import com.azortis.protocolvanish.bungee.settings.SettingsManager;
import com.azortis.protocolvanish.common.PluginVersion;
import com.azortis.protocolvanish.common.UpdateChecker;
import com.azortis.protocolvanish.common.storage.DatabaseManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class ProtocolVanishProxy extends Plugin {

    private PluginVersion pluginVersion;
    private UpdateChecker updateChecker;
    private SettingsManager settingsManager;
    private DatabaseManager databaseManager;
    private BungeeMessagingService messagingService;
    private PermissionManager permissionManager;

    private final transient Collection<UUID> vanishedPlayers = new ArrayList<>();
    private final transient Collection<UUID> loadedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        this.pluginVersion = PluginVersion.getVersionFromString(this.getDescription().getVersion());
        updateChecker = new UpdateChecker(pluginVersion);
        if(updateChecker.hasFailed()){
            getLogger().severe("Failed to check for updates!");
        }else if (updateChecker.isUpdateAvailable()){
            getLogger().info("A new version(v" + updateChecker.getSpigotVersion().getVersionString() + ") is available on spigot!");
            getLogger().info("You can download it here: https://www.spigotmc.org/resources/77011/");;
        }else if(updateChecker.isUnreleased()){
            getLogger().warning("You're using an unreleased version(v" + pluginVersion.getVersionString() + "). Please proceed with caution.");
        }
        this.settingsManager = new SettingsManager(this);
        this.databaseManager = new DatabaseManager(this, settingsManager.getProxySettings().getStorageSettings(), this.getDataFolder());
        this.vanishedPlayers.addAll(this.databaseManager.getDriver().getVanishedUUIDs());
        this.messagingService = new BungeeMessagingService(this);
        this.permissionManager = new PermissionManager(this);

        //Listeners
        new PlayerDisconnectListener(this);
        new PostLoginListener(this);
        new ProxyPingListener(this);
        new TabCompleteResponseListener(this);
    }

    @Override
    public void onDisable() {
        this.messagingService.getProvider().clearMessages();
    }

    public PluginVersion getPluginVersion() {
        return pluginVersion;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public BungeeMessagingService getMessagingService() {
        return messagingService;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public Collection<UUID> getVanishedPlayers(){
        return vanishedPlayers;
    }

    public Collection<UUID> getLoadedPlayers() {
        return loadedPlayers;
    }

}
