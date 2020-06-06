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

package com.azortis.protocolvanish.bungee.settings;

import com.azortis.protocolvanish.common.messaging.MessagingSettings;
import com.azortis.protocolvanish.common.settings.PermissionSettings;
import com.azortis.protocolvanish.common.storage.StorageSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class ProxySettings implements Serializable {

    private final transient Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private StorageSettings storageSettings;
    private MessagingSettings messagingSettings;
    private PermissionSettings permissionSettings;
    private FeatureSettings featureSettings;
    private String fileVersion;

    public StorageSettings getStorageSettings() {
        return storageSettings;
    }

    public MessagingSettings getMessagingSettings() {
        return messagingSettings;
    }

    public PermissionSettings getPermissionSettings() {
        return permissionSettings;
    }

    public FeatureSettings getFeatureSettings() {
        return featureSettings;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
