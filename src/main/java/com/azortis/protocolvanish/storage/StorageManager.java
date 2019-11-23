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

package com.azortis.protocolvanish.storage;

import com.azortis.protocolvanish.PermissionManager;
import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.VanishPlayer;
import com.azortis.protocolvanish.settings.InvisibilitySettingsWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class StorageManager {

    private ProtocolVanish plugin;
    private IDatabase adapter;

    public StorageManager(ProtocolVanish plugin){
        this.plugin = plugin;
        if(plugin.getSettingsManager().getStorageSettings().getUseMySQL()){
            this.adapter = new MySQLAdapter();
        }else{
            this.adapter = new SQLiteAdapter(plugin);
        }
    }

    public VanishPlayer getVanishPlayer(UUID uuid){
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        VanishPlayer vanishPlayer = adapter.getVanishPlayer(uuid);
        if(!plugin.getPermissionManager().hasPermission(vanishPlayer.getPlayer(),PermissionManager.Permission.USE)){
            //TODO for bungee update, do not touch the player, server will treat it as it doesn't exist.
            adapter.deleteVanishPlayer(vanishPlayer);
            return null;
        }
        vanishPlayer.setPlayerSettings(new VanishPlayer.PlayerSettings(vanishPlayer,
                invisibilitySettings.getNightVisionEffect(),
                invisibilitySettings.getDisableDamage(),
                invisibilitySettings.getDisableHunger(),
                invisibilitySettings.getDisableCreatureTarget(),
                invisibilitySettings.getDisableItemPickup()));
        if(vanishPlayer.isVanished())vanishPlayer.getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getSettingsManager()
                        .getMessageSettings().getMessage("loadingPlayerSettings")));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> vanishPlayer.setPlayerSettings(getPlayerSettings(uuid)));
        return vanishPlayer;
    }

    //TODO Change settings to server default, if no bypass permission existing, and save it accordingly.
    private VanishPlayer.PlayerSettings getPlayerSettings(UUID uuid){
        VanishPlayer.PlayerSettings playerSettings = adapter.getPlayerSettings(uuid);
        if(playerSettings.getParent().isVanished())playerSettings.getParent().getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getSettingsManager()
                        .getMessageSettings().getMessage("loadingPlayerSettings")));
        return playerSettings;
    }

}
