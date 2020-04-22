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

package com.azortis.protocolvanish.bukkit.settings.wrappers;

import com.azortis.protocolvanish.bukkit.settings.SettingsManager;
import com.azortis.protocolvanish.bukkit.settings.SettingsWrapper;

@SuppressWarnings("")
public class StorageSettingsWrapper extends SettingsWrapper {

    public StorageSettingsWrapper(SettingsManager parent, Object settingsMap) {
        super(parent, settingsMap, "storageSettings");
    }

    public boolean getUseMySQL() {
        return getBoolean("useMySQL", null);
    }

    public void setUseMySQL(boolean useMySQL) {
        setBoolean("useMySQL", useMySQL, null);
    }

    public String getHost() {
        return getString("host", null);
    }

    public void setHost(String host) {
        setString("host", host, null);
    }

    public String getPort() {
        return getString("port", null);
    }

    public void setPort(String port) {
        setString("host", port, null);
    }

    public String getDatabase() {
        return getString("database", null);
    }

    public void setDatabase(String database) {
        setString("database", database, null);
    }

    public String getUsername() {
        return getString("username", null);
    }

    public void setUsername(String username) {
        setString("username", username, null);
    }

    public String getPassword() {
        return getString("password", null);
    }

    public void setPassword(String password) {
        setString("password", password, null);
    }

    public String getTablePrefix() {
        return getString("tablePrefix", null);
    }

    public void setTablePrefix(String tablePrefix) {
        setString("tablePrefix", tablePrefix, null);
    }

}
