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

package com.azortis.protocolvanish.bukkit.storage;

import com.azortis.protocolvanish.bukkit.PermissionManager;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.VanishPlayer;
import com.azortis.protocolvanish.bukkit.settings.old.wrappers.InvisibilitySettingsWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class StorageManager {

    private final ProtocolVanish plugin;
    private final IDatabase adapter;

    public StorageManager(ProtocolVanish plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loading storage...");
        plugin.getLogger().info("Using SQLite..."); //Because we haven't yet added MySQL checks.
        this.adapter = new SQLiteAdapter(plugin);
    }

    public VanishPlayer getVanishPlayer(Player player) {
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        VanishPlayer vanishPlayer = adapter.getVanishPlayer(player);

        if (vanishPlayer == null) return null;
        if (!plugin.getPermissionManager().hasPermissionToVanish(player)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> adapter.deleteVanishPlayer(vanishPlayer));
            return null;
        }

        //Apply default settings for the actual to be retrieved later.
        vanishPlayer.setPlayerSettings(new VanishPlayer.PlayerSettings(
                invisibilitySettings.getNightVisionEffect(),
                invisibilitySettings.getDisableDamage(),
                invisibilitySettings.getDisableHunger(),
                invisibilitySettings.getDisableCreatureTarget(),
                invisibilitySettings.getDisableItemPickup()));
        /*if (vanishPlayer.isVanished()) player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getSettingsManager().getMessageSettings().getMessage("loadingPlayerSettings")));*/
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> vanishPlayer.setPlayerSettings(getPlayerSettings(player)));
        return vanishPlayer;
    }

    private VanishPlayer.PlayerSettings getPlayerSettings(Player player) {
        VanishPlayer.PlayerSettings playerSettings = adapter.getPlayerSettings(player);

        //Permission checks
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        PermissionManager permissionManager = plugin.getPermissionManager();
        boolean valid = true;
        if (playerSettings.doNightVision() != invisibilitySettings.getNightVisionEffect()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_NIGHT_VISION)) {
            valid = false;
            playerSettings.setNightVision(invisibilitySettings.getNightVisionEffect());
        }
        if (playerSettings.getDisableDamage() != invisibilitySettings.getDisableDamage()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_DAMAGE)) {
            valid = false;
            playerSettings.setDisableDamage(invisibilitySettings.getDisableDamage());
        }
        if (playerSettings.getDisableHunger() != invisibilitySettings.getDisableHunger()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_HUNGER)) {
            valid = false;
            playerSettings.setDisableHunger(invisibilitySettings.getDisableHunger());
        }
        if (playerSettings.getDisableCreatureTarget() != invisibilitySettings.getDisableCreatureTarget()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_CREATURE_TARGET)) {
            valid = false;
            playerSettings.setDisableCreatureTarget(invisibilitySettings.getDisableCreatureTarget());
        }
        if (playerSettings.getDisableItemPickUp() != invisibilitySettings.getDisableItemPickup()
                && !permissionManager.hasPermission(player, PermissionManager.Permission.CHANGE_ITEM_PICKUP)) {
            valid = false;
            playerSettings.setDisableItemPickUp(invisibilitySettings.getDisableItemPickup());
        }
        if (!valid) adapter.savePlayerSettings(playerSettings);

        //Check if it should send the message(applies if the player joined in vanish)
        /*if (playerSettings.getParent().isVanished()) player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                plugin.getSettingsManager().getMessageSettings().getMessage("loadedPlayerSettings")));*/
        return playerSettings;
    }

    public void saveVanishPlayer(VanishPlayer vanishPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> adapter.saveVanishPlayer(vanishPlayer));
    }

    public void savePlayerSettings(VanishPlayer.PlayerSettings playerSettings) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> adapter.savePlayerSettings(playerSettings));
    }

    public void updateServerInfo() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> adapter.updateServerInfo());
    }

    public VanishPlayer createVanishPlayer(Player player) {
        VanishPlayer vanishPlayer = new VanishPlayer(player, false);
        InvisibilitySettingsWrapper invisibilitySettings = plugin.getSettingsManager().getInvisibilitySettings();
        vanishPlayer.setPlayerSettings(new VanishPlayer.PlayerSettings(
                invisibilitySettings.getNightVisionEffect(),
                invisibilitySettings.getDisableDamage(),
                invisibilitySettings.getDisableHunger(),
                invisibilitySettings.getDisableCreatureTarget(),
                invisibilitySettings.getDisableItemPickup()));
        vanishPlayer.getPlayerSettings().setParent(vanishPlayer);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> adapter.createVanishPlayer(vanishPlayer));
        return vanishPlayer;
    }

}
