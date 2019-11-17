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
public class VisibilitySettingsWrapper extends SettingsWrapper{

    public VisibilitySettingsWrapper(SettingsManager parent, Object settingsMap){
        super(parent, settingsMap, "visibilitySettings");
    }

    public List<String> getEnabledPacketListeners(){
        return getStringList("enabledPacketListeners");
    }

    public void setEnabledPacketListeners(List<String> enabledPacketListeners){
        setStringList("enabledPacketListeners", enabledPacketListeners);
    }

    public boolean getAdjustOnlinePlayerCount(){
        Map<String, Object> externalVisibility = getSection("externalVisibility");
        return (Boolean) externalVisibility.get("adjustOnlinePlayerCount");
    }

    public void setAdjustOnlinePlayerCount(boolean adjustOnlinePlayerCount){
        Map<String, Object> externalVisibility = getSection("externalVisibility");
        externalVisibility.remove("adjustOnlinePlayerCount");
        externalVisibility.put("adjustOnlinePlayerCount", adjustOnlinePlayerCount);
        setSection("externalVisibility", externalVisibility);
    }

    public boolean getAdjustOnlinePlayerList(){
        Map<String, Object> externalVisibility = getSection("externalVisibility");
        return (Boolean) externalVisibility.get("adjustOnlinePlayerCount");
    }

    public void setAdjustOnlinePlayerList(boolean adjustOnlinePlayerList){
        Map<String, Object> externalVisibility = getSection("externalVisibility");
        externalVisibility.remove("adjustOnlinePlayerList");
        externalVisibility.put("adjustOnlinePlayerList", adjustOnlinePlayerList);
        setSection("externalVisibility", externalVisibility);
    }

}
