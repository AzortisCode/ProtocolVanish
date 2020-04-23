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

import com.azortis.protocolvanish.common.PluginVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private final ProtocolVanish plugin;
    private PluginVersion spigotVersion;
    private final PluginVersion pluginVersion;
    private boolean updateAvailable;
    private boolean unreleased;

    UpdateChecker(ProtocolVanish plugin) {
        this.plugin = plugin;
        this.pluginVersion = PluginVersion.getVersionFromString(plugin.getDescription().getVersion());

        //Grab version
        plugin.getLogger().info("Checking for updates...");
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=69445").openConnection();
            connection.setRequestMethod("GET");
            spigotVersion = PluginVersion.getVersionFromString(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
        } catch (Exception ex) {
            plugin.getLogger().severe("Failed to check for updates on spigot.");
        }
        if(spigotVersion == null){
            plugin.getLogger().severe("Failed to check for updates on spigot.");
            return;
        }
        if (pluginVersion.isSame(spigotVersion)) return;

        this.updateAvailable = spigotVersion.isNewerThen(pluginVersion);
        if (updateAvailable) {
            plugin.getLogger().info("A new version(" + spigotVersion.getVersionString() + ") is available on spigot!");
            plugin.getLogger().info("You can download it here: https://www.spigotmc.org/resources/69445/");
            Bukkit.getPluginManager().registerEvents(this, plugin);
            return;
        }

        this.unreleased = pluginVersion.isNewerThen(spigotVersion);
        if (unreleased) {
            plugin.getLogger().warning("You're using an unreleased version(" + pluginVersion.getVersionString() + "). Please proceed with caution.");
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    public PluginVersion getSpigotVersion() {
        return spigotVersion;
    }

    public PluginVersion getPluginVersion(){
        return pluginVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public boolean isUnreleased() {
        return unreleased;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.ADMIN)) {
            if (updateAvailable) {
                player.sendMessage(ChatColor.GREEN + "[ProtocolVanish] A new update is available(" + spigotVersion.getVersionString() + ")");
                player.sendMessage(ChatColor.GREEN + "You can download it here: You can download it here: https://www.spigotmc.org/resources/69445/");
            } else if (unreleased) {
                player.sendMessage(ChatColor.RED + "[ProtocolVanish] You're using an unreleased version(" + pluginVersion.getVersionString() + "). Please proceed with caution.");
            }
        }
    }

}
