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

import com.azortis.azortislib.AzortisLib;
import com.azortis.protocolvanish.api.VanishAPI;
import com.azortis.protocolvanish.command.VanishCommand;
import com.azortis.protocolvanish.listeners.*;
import com.azortis.protocolvanish.settings.SettingsManager;
import com.azortis.protocolvanish.visibility.VisibilityManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ProtocolVanish extends JavaPlugin {

    private AzortisLib azortisLib;
    private Metrics metrics;
    private SettingsManager settingsManager;
    private PermissionManager permissionManager;
    private VisibilityManager visibilityManager;
    private StorageManager storageManager;

    private HashMap<UUID, VanishPlayer> vanishPlayerMap = new HashMap<>();

    @Override
    public void onEnable() {
        this.azortisLib = new AzortisLib(this, "ProtocolVanish", "§1[§9ProtocolVanish§1]§0");
        this.metrics = new Metrics(this);
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")){
            azortisLib.getLogger().severe("ProtocolLib isn't present, please install ProtocolLib! Shutting down...");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.settingsManager = new SettingsManager(this);
        this.permissionManager = new PermissionManager(this);
        this.visibilityManager = new VisibilityManager(this);
        this.storageManager = new StorageManager(this);

        new VanishCommand(this);
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
        azortisLib.close();
    }

    public AzortisLib getAzortisLib() {
        return azortisLib;
    }

    public Metrics getMetrics(){
        return metrics ;
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

    public VanishPlayer getVanishPlayer(UUID uuid){
        if(!vanishPlayerMap.containsKey(uuid))vanishPlayerMap.put(uuid, new VanishPlayer(Bukkit.getPlayer(uuid), false, this));
        return vanishPlayerMap.get(uuid);
    }

    public VanishPlayer getVanishPlayer(Player player){
        return getVanishPlayer(player.getUniqueId());
    }

}
