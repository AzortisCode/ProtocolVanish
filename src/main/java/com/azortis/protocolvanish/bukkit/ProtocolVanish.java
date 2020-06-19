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

import com.azortis.protocolvanish.bukkit.api.VanishAPI;
import com.azortis.protocolvanish.bukkit.listeners.*;
import com.azortis.protocolvanish.bukkit.command.VanishCommand;
import com.azortis.protocolvanish.bukkit.settings.SettingsManager;
import com.azortis.protocolvanish.bukkit.visibility.VisibilityManager;
import com.azortis.protocolvanish.common.PlayerSettings;
import com.azortis.protocolvanish.common.VanishPlayer;
import com.azortis.protocolvanish.common.storage.DatabaseManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ProtocolVanish extends JavaPlugin {

    private Metrics metrics;

    private SettingsManager settingsManager;
    private DatabaseManager databaseManager;
    private BukkitMessagingService messagingService;
    private PermissionManager permissionManager;
    private VisibilityManager visibilityManager;
    private UpdateChecker updateChecker;
    private VanishCommand vanishCommand;

    private final HashMap<UUID, VanishPlayer> vanishPlayerMap = new HashMap<>();

    @Override
    public void onEnable() {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.getLogger().severe("ProtocolLib isn't present, please install ProtocolLib! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.getLogger().severe("PlaceholderAPI isn't present, please install PlaceholderAPI! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.updateChecker = new UpdateChecker(this);
        this.metrics = new Metrics(this);
        this.settingsManager = new SettingsManager(this);
        this.databaseManager = new DatabaseManager(this, settingsManager.getSettings().getStorageSettings(), this.getDataFolder());
        if(settingsManager.getSettings().getBungeeSettings().isEnabled())this.messagingService = new BukkitMessagingService(this);
        this.permissionManager = new PermissionManager(this);
        this.visibilityManager = new VisibilityManager(this);

        this.getLogger().info("Registering events...");
        new PlayerLoginListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);

        VanishAPI.setPlugin(this);
    }

    @Override
    public void onDisable() {

    }

    public void reload(){
        settingsManager.reloadSettings();
        settingsManager.reloadMessages();

    }

    public UpdateChecker getUpdateChecker(){
        return updateChecker;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public BukkitMessagingService getMessagingService() {
        return messagingService;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public VanishPlayer getVanishPlayer(UUID uuid){
        return vanishPlayerMap.get(uuid);
    }

    /**
     * Loads the vanishPlayer from the database driver.
     * Should never be called on main thread!
     *
     * @param uuid The uuid of the player.
     */
    public synchronized void loadVanishPlayer(UUID uuid){
        VanishPlayer vanishPlayer = databaseManager.getDriver().getVanishPlayer(uuid);
        if(vanishPlayer == null){
            createVanishPlayer(uuid);
        }else{
            Bukkit.getScheduler().runTask(this, ()-> vanishPlayerMap.put(uuid, vanishPlayer));
        }
    }

    public synchronized void createVanishPlayer(UUID uuid){
        VanishPlayer vanishPlayer = new VanishPlayer(uuid, false, new PlayerSettings());
        databaseManager.getDriver().createVanishPlayer(vanishPlayer);
        Bukkit.getScheduler().runTask(this, ()-> vanishPlayerMap.put(uuid, vanishPlayer));
    }

    /**
     * Updates the VanishPlayer instance from the database
     * Should never be called on main thread!
     *
     * @param uuid The uuid of the player.
     */
    public synchronized void updateVanishPlayer(UUID uuid){
        VanishPlayer vanishPlayer = databaseManager.getDriver().getVanishPlayer(uuid);
        Bukkit.getScheduler().runTask(this, ()-> {
           vanishPlayerMap.remove(uuid);
           vanishPlayerMap.put(uuid, vanishPlayer);
        });
    }

    /**
     * Unloads the vanishPlayer from cache,
     * Should always be called from main thread!
     *
     * @param uuid The uuid of the player.
     */
    public void unloadVanishPlayer(UUID uuid){
        vanishPlayerMap.remove(uuid);
    }

    public void sendPlayerMessage(Player receiver, Player placeholderPlayer, String messagePath, HashMap<String, String> commandPlaceholders){
        String rawMessage = settingsManager.getMessages().getMessage(messagePath);
        String message = PlaceholderAPI.setPlaceholders(placeholderPlayer, rawMessage);
        if(commandPlaceholders != null){
            for (String commandPlaceholder : commandPlaceholders.keySet()){
                message = message.replaceAll(commandPlaceholder, commandPlaceholders.get(commandPlaceholder));
            }
        }
        if(message.startsWith("[JSON]")){
            String jsonString = message.replaceFirst("[JSON]", "").trim();
            BaseComponent[] baseComponents = ComponentSerializer.parse(jsonString);
            receiver.spigot().sendMessage(baseComponents);
        }
        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
