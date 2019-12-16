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
    private final int RESOURCE_ID = 69445;
    private String spigotVersion;
    private final String pluginVersion;
    private boolean updateAvailable;

    UpdateChecker(ProtocolVanish plugin){
        this.plugin = plugin;
        this.pluginVersion = plugin.getDescription().getVersion();
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getSpigotVersion() {
        return spigotVersion;
    }

    public void fetch(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(
                        "https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openConnection();
                connection.setRequestMethod("GET");
                spigotVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            }catch (Exception ex){
                plugin.getLogger().info("Failed to check for updates on spigot.");
            }
            if (spigotVersion == null || spigotVersion.isEmpty()) {
                return;
            }
            updateAvailable = spigotIsNewer();
            if (!updateAvailable) {
                return;
            }

            Bukkit.getScheduler().runTask(plugin, ()-> {
                plugin.getLogger().info("A new version(" + spigotVersion + ") is available on spigot!");
                plugin.getLogger().info("You can download it here: https://www.spigotmc.org/resources/protocolvanish.69445/");
                Bukkit.getPluginManager().registerEvents(this, plugin);
            });
        });
    }

    private boolean spigotIsNewer() {
        if (spigotVersion == null || spigotVersion.isEmpty()) {
            return false;
        }
        return !pluginVersion.equals(spigotVersion);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.ADMIN)){
            player.sendMessage(ChatColor.GREEN + "[ProtocolVanish] A new update is available(" + spigotVersion +")");
            player.sendMessage(ChatColor.GREEN + "You can download it here: You can download it here: https://www.spigotmc.org/resources/protocolvanish.69445/");
        }
    }

}
