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

package com.azortis.protocolvanish;

import com.azortis.protocolvanish.api.VanishAPI;
import com.azortis.protocolvanish.listeners.*;
import com.azortis.protocolvanish.settings.SettingsManager;
import com.azortis.protocolvanish.storage.StorageManager;
import com.azortis.protocolvanish.visibility.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ProtocolVanish extends JavaPlugin {

    private Metrics metrics;
    private SettingsManager settingsManager;
    private PermissionManager permissionManager;
    private VisibilityManager visibilityManager;
    private StorageManager storageManager;

    private HashMap<UUID, VanishPlayer> vanishPlayerMap = new HashMap<>();

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this);
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.getLogger().severe("ProtocolLib isn't present, please install ProtocolLib! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.settingsManager = new SettingsManager(this);
        this.storageManager = new StorageManager(this);
        this.permissionManager = new PermissionManager(this);
        this.visibilityManager = new VisibilityManager(this);

//        new VanishCommand(this);
        new PlayerLoginListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new EntityDamageListener(this);
        new FoodLevelChangeListener(this);
        new EntityTargetLivingEntityListener(this);
        new EntityPickupItemListener(this);

        VanishAPI.setPlugin(this);
    }

    @Override
    public void onDisable() {

    }

    public Metrics getMetrics() {
        return metrics;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * Gets the vanishPlayer of a specific player.
     *
     * @param uuid The {@link UUID} of the player
     * @return The {@link VanishPlayer} of the player, null if have no permission.
     */
    public VanishPlayer getVanishPlayer(UUID uuid) {
        if (!vanishPlayerMap.containsKey(uuid)) {
            VanishPlayer vanishPlayer = storageManager.getVanishPlayer(uuid);
            if (vanishPlayer != null) {
                vanishPlayerMap.put(uuid, storageManager.getVanishPlayer(uuid));
                if (vanishPlayer.isVanished()) {
                    visibilityManager.getVanishedPlayers().add(uuid);
                    for (Player player : Bukkit.getOnlinePlayers())
                        visibilityManager.setVanished(vanishPlayer.getPlayer(), player, true);
                }
            } else return null;
        }
        return vanishPlayerMap.get(uuid);
    }

    public VanishPlayer getVanishPlayer(Player player) {
        return getVanishPlayer(player.getUniqueId());
    }

    public HashMap<UUID, VanishPlayer> getVanishPlayerMap() {
        return vanishPlayerMap;
    }

}
