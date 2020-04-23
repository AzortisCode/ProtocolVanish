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

package com.azortis.protocolvanish.bukkit.hooks;

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.api.PlayerReappearEvent;
import com.azortis.protocolvanish.bukkit.api.PlayerVanishEvent;
import com.azortis.protocolvanish.bukkit.settings.SettingsWrapper;
import com.azortis.protocolvanish.bukkit.settings.wrappers.HookSettingsWrapper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.dynmap.bukkit.DynmapPlugin;

public class DynmapHook implements PluginHook, Listener {

    private transient final ProtocolVanish plugin;
    private boolean enabled;

    public DynmapHook(ProtocolVanish plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        enableHook();
    }

    @Override
    public String getName() {
        return "Dynmap";
    }

    @Override
    public void enableHook() {
        if(Bukkit.getPluginManager().isPluginEnabled("dynmap") && plugin.getSettingsManager().getHookSettings().isHookEnabled("dynmapHook")){
            this.enabled = true;
        }
    }

    @Override
    public void disableHook() {
        this.enabled = false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerVanish(PlayerVanishEvent event){
        if(enabled){
            Player player = event.getPlayer();
            DynmapPlugin dynmapPlugin = (DynmapPlugin)Bukkit.getPluginManager().getPlugin("dynmap");
            HookSettingsWrapper hookSettings = plugin.getSettingsManager().getHookSettings();
            SettingsWrapper.SettingsSection settingsSection = hookSettings.getSection("dynmapHook");

            if(dynmapPlugin != null){
                if(hookSettings.getBoolean("hideVanishPlayersOnMap", settingsSection))dynmapPlugin.setPlayerVisiblity(player, false);
                if(hookSettings.getBoolean("broadCastFakeQuitOnVanish", settingsSection)){
                    dynmapPlugin.sendBroadcastToWeb("", PlaceholderAPI.setPlaceholders(player, plugin.getSettingsManager().getMessageSettings().getMessage("dynmapVanishMessage")));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerReappear(PlayerReappearEvent event){
        if(enabled){
            Player player = event.getPlayer();
            DynmapPlugin dynmapPlugin = (DynmapPlugin)Bukkit.getPluginManager().getPlugin("dynmap");
            HookSettingsWrapper hookSettings = plugin.getSettingsManager().getHookSettings();
            SettingsWrapper.SettingsSection settingsSection = hookSettings.getSection("dynmapHook");

            if(dynmapPlugin != null){
                if(hookSettings.getBoolean("hideVanishPlayersOnMap", settingsSection))dynmapPlugin.setPlayerVisiblity(player, true);
                if(hookSettings.getBoolean("broadCastFakeJoinOnReappear", settingsSection)){
                    dynmapPlugin.sendBroadcastToWeb("", PlaceholderAPI.setPlaceholders(player, plugin.getSettingsManager().getMessageSettings().getMessage("dynmapReappearMessage")));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(enabled){
            Player player = event.getPlayer();
            DynmapPlugin dynmapPlugin = (DynmapPlugin)Bukkit.getPluginManager().getPlugin("dynmap");
            HookSettingsWrapper hookSettings = plugin.getSettingsManager().getHookSettings();
            SettingsWrapper.SettingsSection settingsSection = hookSettings.getSection("dynmapHook");

            if(dynmapPlugin != null){
                if(plugin.getVisibilityManager().isVanished(player) && hookSettings.getBoolean("hideVanishPlayersOnMap", settingsSection))
                    dynmapPlugin.setPlayerVisiblity(player, true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(enabled){
            Player player = event.getPlayer();
            DynmapPlugin dynmapPlugin = (DynmapPlugin)Bukkit.getPluginManager().getPlugin("dynmap");
            HookSettingsWrapper hookSettings = plugin.getSettingsManager().getHookSettings();
            SettingsWrapper.SettingsSection settingsSection = hookSettings.getSection("dynmapHook");

            if(dynmapPlugin != null){
                if(plugin.getVisibilityManager().isVanished(player) && hookSettings.getBoolean("hideVanishPlayersOnMap", settingsSection))
                    dynmapPlugin.setPlayerVisiblity(player, true);
            }
        }
    }

}
