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
public class SettingsWrapper {

    protected SettingsManager parent;
    protected Map<String, Object> settingsMap;
    private String settingsPath;

    public SettingsWrapper(SettingsManager parent, Object settingsMap, String settingsPath){
        this.parent = parent;
        this.settingsMap = (Map<String, Object>) settingsMap;
        this.settingsPath = settingsPath;
    }

    protected String getString(String path){
        return (String) settingsMap.get(path);
    }

    protected void setString(String path, String value){
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected List<?> getList(String path){
        return (List<?>) settingsMap.get(path);
    }

    protected void setList(String path, List<?> value){
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected Map<String, Object> getSection(String path){
        return (Map<String, Object>) settingsMap.get(path);
    }

    protected void setSection(String path, Map<String, Object> value){
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected List<String> getStringList(String path){
        return (List<String>) settingsMap.get(path);
    }

    protected void setStringList(String path, List<String> value){
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    public void save(){
        Map<String, Object> parrentSettingsMap = parent.getSettingsMap();
        parrentSettingsMap.remove(settingsPath);
        parrentSettingsMap.put(settingsPath, settingsMap);
    }

}
