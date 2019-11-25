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
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("all")
public class StorageManager {

    private ProtocolVanish plugin;
    private IDatabase adapter;

    public StorageManager(ProtocolVanish plugin){
        this.plugin = plugin;
        /*if(plugin.getSettingsManager().getStorageSettings().getUseMySQL()){
            this.adapter = new MySQLAdapter();
        }else{
            this.adapter = new SQLiteAdapter(plugin);
        }*/
        this.adapter = new SQLiteAdapter(plugin);
    }

    public VanishPlayer getVanishPlayer(UUID uuid){
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        VanishPlayer vanishPlayer = adapter.getVanishPlayer(uuid);

        //Checks to prevent memory leaks.
        if(vanishPlayer == null)return null;
        if(!plugin.getPermissionManager().hasPermission(Bukkit.getPlayer(uuid),PermissionManager.Permission.USE)
                && /*!plugin.getSettingsManager().getStorageSettings().getUseMySQL()*/true){
            adapter.deleteVanishPlayer(vanishPlayer);
            return null;
        }else if(/*plugin.getSettingsManager().getStorageSettings().getUseMySQL()*/ false
                && !plugin.getPermissionManager().hasPermission(Bukkit.getPlayer(uuid), PermissionManager.Permission.USE))return null;

        //Apply default settings for the actual to be retrieved later.
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

    private VanishPlayer.PlayerSettings getPlayerSettings(UUID uuid){
        VanishPlayer.PlayerSettings playerSettings = adapter.getPlayerSettings(uuid);
        Player player = playerSettings.getParent().getPlayer();

        //Permission checks
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        PermissionManager permissionManager = plugin.getPermissionManager();
        boolean valid = true;
        if(playerSettings.doNightVision() != invisibilitySettings.getNightVisionEffect()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_NIGHT_VISION)){
            valid = false;
            playerSettings.setNightVision(invisibilitySettings.getNightVisionEffect());
        }
        if(playerSettings.getDisableDamage() != invisibilitySettings.getDisableDamage()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_DAMAGE)){
            valid = false;
            playerSettings.setDisableDamage(invisibilitySettings.getDisableDamage());
        }
        if(playerSettings.getDisableHunger() != invisibilitySettings.getDisableHunger()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_HUNGER)){
            valid = false;
            playerSettings.setDisableHunger(invisibilitySettings.getDisableHunger());
        }
        if(playerSettings.getDisableCreatureTarget() != invisibilitySettings.getDisableCreatureTarget()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_CREATURE_TARGET)){
            valid = false;
            playerSettings.setDisableCreatureTarget(invisibilitySettings.getDisableCreatureTarget());
        }
        if(playerSettings.getDisableItemPickUp() != invisibilitySettings.getDisableItemPickup()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_ITEM_PICKUP)){
            valid = false;
            playerSettings.setDisableItemPickUp(invisibilitySettings.getDisableItemPickup());
        }
        if(!valid)adapter.savePlayerSettings(playerSettings);

        //Check if it should send the message(applies if the player joined in vanish)
        if(playerSettings.getParent().isVanished())playerSettings.getParent().getPlayer()
                .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getSettingsManager()
                        .getMessageSettings().getMessage("loadingPlayerSettings")));
        return playerSettings;
    }

    public void saveVanishPlayer(VanishPlayer vanishPlayer){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> adapter.saveVanishPlayer(vanishPlayer));
    }

    public void updateServerInfo(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> adapter.updateServerInfo());
    }

}
