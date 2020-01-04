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

package com.azortis.protocolvanish.settings.wrappers;

import com.azortis.protocolvanish.settings.SettingsManager;
import com.azortis.protocolvanish.settings.SettingsWrapper;

@SuppressWarnings("all")
public class InvisibilitySettingsWrapper extends SettingsWrapper {

    public InvisibilitySettingsWrapper(SettingsManager parent, Object settingsMap) {
        super(parent, settingsMap, "invisibilitySettings");
    }

    public boolean getNightVisionEffect() {
        return getBoolean("nightVisionEffect", null);
    }

    public void setNightVisionEffect(boolean nightVisionEffect) {
        setBoolean("nightVisionEffect", nightVisionEffect, null);
    }

    public boolean getDisableDamage() {
        return getBoolean("disableDamage", null);
    }

    public void setDisableDamage(boolean disableDamage) {
        setBoolean("disableDamage", disableDamage, null);
    }

    public boolean getDisableHunger() {
        return getBoolean("disableHunger", null);
    }

    public void setDisableHunger(boolean disableHunger) {
        setBoolean("disableHunger", disableHunger, null);
    }

    public boolean getDisableCreatureTarget() {
        return getBoolean("disableCreatureTarget", null);
    }

    public void setCreatureTarget(boolean disableCreatureTarget) {
        setBoolean("disableCreatureTarget", disableCreatureTarget, null);
    }

    public boolean getDisableItemPickup() {
        return getBoolean("disableItemPickup", null);
    }

    public void setDisableItemPickup(boolean disableItemPickup) {
        setBoolean("disableItemPickup", disableItemPickup, null);
    }

    public boolean getSleepingIgnored(){
        return getBoolean("sleepingIgnored", null);
    }

    public void setSleepingIgnored(boolean sleepingIgnored){
        setBoolean("sleepingIgnored", sleepingIgnored, null);
    }

    public boolean getSwitchGameMode(){
        return getBoolean("switchGameMode", null);
    }

    public void setSwitchGameMode(boolean switchGameMode){
        setBoolean("switchGameMode", switchGameMode, null);
    }

    public boolean getAllowFlight(){
        return getBoolean("allowFlight", null);
    }

    public void setAllowFlight(boolean allowFlight){
        setBoolean("allowFlight", allowFlight, null);
    }

    public boolean getKeepFlight(){
        return getBoolean("keepFlight", null);
    }

    public void setKeepFlight(boolean keepFlight){
        setBoolean("keepFlight", keepFlight, null);
    }

}
