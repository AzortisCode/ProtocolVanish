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

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class CommandSettingsWrapper {

    private SettingsManager parent;
    private HashMap<String, Object> settingsMap;

    public CommandSettingsWrapper(SettingsManager parent, Object settingsMap){
        this.parent = parent;
        this.settingsMap = (HashMap<String, Object>) settingsMap;
    }

    public String getName(){
        return (String) settingsMap.get("name");
    }

    public void setName(String name){
        settingsMap.remove("name");
        settingsMap.put("name", name);
    }

    public String getDescription(){
        return (String) settingsMap.get("description");
    }

    public void setDescription(String description){
        settingsMap.remove("description");
        settingsMap.put("description", description);
    }

    public String getUsage(){
        return (String) settingsMap.get("usage");
    }

    public void setUsage(String usage){
        settingsMap.remove("usage");
        settingsMap.put("usage", usage);
    }

    public List<String> getAliases(){
        return (List<String>) settingsMap.get("aliases");
    }

    public void setAliases(List<String> aliases){
        settingsMap.remove("aliases");
        settingsMap.put("aliases", aliases);
    }

    public void save(){
        HashMap<String, Object> parrentSettingsMap = parent.getSettingsMap();
        parrentSettingsMap.remove("commandSettings");
        parrentSettingsMap.put("commandSettings", settingsMap);
    }

}
