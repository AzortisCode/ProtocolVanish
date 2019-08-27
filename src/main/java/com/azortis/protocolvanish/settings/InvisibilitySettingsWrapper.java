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

import java.util.Map;

@SuppressWarnings("all")
public class InvisibilitySettingsWrapper {

    private SettingsManager parent;
    private Map<String, Object> settingsMap;

    public InvisibilitySettingsWrapper(SettingsManager parent, Object settingsMap){
        this.parent = parent;
        this.settingsMap = (Map<String, Object>) settingsMap;
    }

    public boolean getNightVisionEffect(){
        return (Boolean) settingsMap.get("nightVisionEffect");
    }

    public void setNightVisionEffect(boolean nightVisionEffect){
        settingsMap.remove("nightVisionEffect");
        settingsMap.put("nightVisionEffect", nightVisionEffect);
    }

    public boolean getDisableDamage(){
        return (Boolean) settingsMap.get("disableDamage");
    }

    public void setDisableDamage(boolean disableDamage){
        settingsMap.remove("disableDamage");
        settingsMap.put("disableDamage", disableDamage);
    }

    public boolean getDisableHunger(){
        return (Boolean) settingsMap.get("disableHunger");
    }

    public void setDisableHunger(boolean disableHunger){
        settingsMap.remove("disableHunger");
        settingsMap.put("disableHunger", disableHunger);
    }

    public boolean getDisableCreatureTarget(){
        return (Boolean) settingsMap.get("disableCreatureTarget");
    }

    public void setCreatureTarget(boolean disableCreatureTarget){
        settingsMap.remove("disableCreatureTarget");
        settingsMap.put("disableCreatureTarget", disableCreatureTarget);
    }

    public boolean getDisableItemPickup(){ return (Boolean) settingsMap.get("disableItemPickup"); }

    public void setDisableItemPickup(boolean disableItemPickup){
        settingsMap.remove("disableItemPickup");
        settingsMap.put("disableItemPickup", disableItemPickup);
    }

    /*public boolean getDisableExpPickup(){
        return (Boolean) settingsMap.get("disableExpPickup");
    }

    public void setDisableExpPickup(boolean disableExpPickup){
        settingsMap.remove("disableExpPickup");
        settingsMap.put("disableExpPickup", disableExpPickup);
    }*/

    public void save(){
        Map<String, Object> parrentSettingsMap = parent.getSettingsMap();
        parrentSettingsMap.remove("invisibilitySettings");
        parrentSettingsMap.put("invisibilitySettings", settingsMap);
    }

}
