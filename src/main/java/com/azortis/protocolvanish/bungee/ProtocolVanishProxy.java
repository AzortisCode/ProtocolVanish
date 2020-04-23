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

import com.azortis.protocolvanish.common.PluginVersion;
import com.azortis.protocolvanish.common.UpdateChecker;
import net.md_5.bungee.api.plugin.Plugin;

public final class ProtocolVanishProxy extends Plugin {

    private PluginVersion pluginVersion;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        this.pluginVersion = PluginVersion.getVersionFromString(this.getDescription().getVersion());
        UpdateChecker updateChecker = new UpdateChecker(pluginVersion);
        if(updateChecker.hasFailed()){
            getLogger().severe("Failed to check for updates!");
        }else if (updateChecker.isUpdateAvailable()){
            getLogger().info("A new version(v" + updateChecker.getSpigotVersion().getVersionString() + ") is available on spigot!");
            getLogger().info("You can download it here: https://www.spigotmc.org/resources/77011/");;
        }else if(updateChecker.isUnreleased()){
            getLogger().warning("You're using an unreleased version(v" + pluginVersion.getVersionString() + "). Please proceed with caution.");
        }

    }

    public PluginVersion getPluginVersion() {
        return pluginVersion;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
