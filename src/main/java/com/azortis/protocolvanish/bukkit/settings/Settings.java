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

package com.azortis.protocolvanish.bukkit.settings;

import com.azortis.protocolvanish.bukkit.command.CommandSettings;
import com.azortis.protocolvanish.bukkit.visibility.VisibilitySettings;
import com.azortis.protocolvanish.common.settings.PermissionSettings;
import com.azortis.protocolvanish.common.storage.StorageSettings;

import java.io.Serializable;

public class Settings implements Serializable {

    private BungeeSettings bungeeSettings;
    private StorageSettings storageSettings;
    private VisibilitySettings visibilitySettings;
    private MessageSettings messageSettings;
    private PermissionSettings permissionSettings;
    private CommandSettings commandSettings;
    private String fileVersion;

    public BungeeSettings getBungeeSettings() {
        return bungeeSettings;
    }

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    public VisibilitySettings getVisibilitySettings() {
        return visibilitySettings;
    }

    public MessageSettings getMessageSettings() {
        return messageSettings;
    }

    public PermissionSettings getPermissionSettings() {
        return permissionSettings;
    }

    public CommandSettings getCommandSettings() {
        return commandSettings;
    }

    public String getFileVersion() {
        return fileVersion;
    }

}
