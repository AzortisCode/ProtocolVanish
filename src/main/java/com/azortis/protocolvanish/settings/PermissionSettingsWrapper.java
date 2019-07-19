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
import java.util.Map;

@SuppressWarnings("all")
public class PermissionSettingsWrapper {

    private SettingsManager parent;
    private Map<String, Object> settingsMap;

    public PermissionSettingsWrapper(SettingsManager parent, Object settingsMap){
        this.parent = parent;
        this.settingsMap = (Map<String, Object>) settingsMap;
    }

    public boolean getEnableSeePermission(){
        return (Boolean) settingsMap.get("enableSeePermission");
    }

    public void setEnableSeePermission(boolean enableSeePermission){
        settingsMap.remove("enableSeePermission");
        settingsMap.put("enableSeePermission", enableSeePermission);
    }

    public boolean getEnableLayeredPermissions(){
        return (Boolean) settingsMap.get("enableLayeredPermissions");
    }

    public void setEnableLayeredPermissions(boolean enableLayeredPermissions){
        settingsMap.remove("enableLayeredPermissions");
        settingsMap.put("enableLayeredPermissions", enableLayeredPermissions);
    }

    public int getMaxLevel(){
        Number maxLevel = (Number) settingsMap.get("maxLevel");
        return maxLevel.intValue();
    }

    public void setMaxLevel(int maxLevel){
        settingsMap.remove("maxLevel");
        settingsMap.put("maxLevel", maxLevel);
    }

    public void save(){
        Map<String, Object> parrentSettingsMap = parent.getSettingsMap();
        parrentSettingsMap.remove("permissionSettings");
        parrentSettingsMap.put("permissionSettings", settingsMap);
    }

}
