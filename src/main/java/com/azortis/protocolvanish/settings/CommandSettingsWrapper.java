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

package com.azortis.protocolvanish.settings;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class CommandSettingsWrapper extends SettingsWrapper {

    public CommandSettingsWrapper(SettingsManager parent, Object settingsMap) {
        super(parent, settingsMap, "commandSettings");
    }

    public String getName() {
        return getString("name", null);
    }

    public void setName(String name) {
        setString("name", name, null);
    }

    public String getDescription() {
        return getString("description", null);
    }

    public void setDescription(String description) {
        setString("description", description, null);
    }

    public String getUsage() {
        return getString("usage", null);
    }

    public void setUsage(String usage) {
        setString("usage", usage, null);
    }

    public List<String> getAliases() {
        return getStringList("aliases", null);
    }

    public void setAliases(List<String> aliases) {
        setStringList("aliases", aliases, null);
    }

}
