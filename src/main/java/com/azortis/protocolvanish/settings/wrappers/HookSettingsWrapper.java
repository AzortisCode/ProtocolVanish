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

public class HookSettingsWrapper extends SettingsWrapper {

    public HookSettingsWrapper(SettingsManager parent, Object settingsMap){
        super(parent, settingsMap, "hookSettings");
    }

    public boolean isHookEnabled(String hookName){
        SettingsSection hookSection = getSection(hookName);
        return getBoolean("enabled", hookSection);
    }

    public void setHookEnabled(String hookName, boolean enabled){
        SettingsSection hookSection = getSection(hookName);
        setBoolean("enabled", enabled, hookSection);
    }

}
