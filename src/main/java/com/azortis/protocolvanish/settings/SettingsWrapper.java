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

    public SettingsWrapper(SettingsManager parent, Object settingsMap, String settingsPath) {
        this.parent = parent;
        this.settingsMap = (Map<String, Object>) settingsMap;
        this.settingsPath = settingsPath;
    }

    protected String getString(String path, SettingsSection section) {
        if (section != null) return (String) section.sectionMap.get(path);
        return (String) settingsMap.get(path);
    }

    protected void setString(String path, String value, SettingsSection section) {
        if (section != null) {
            section.sectionMap.remove(path);
            section.sectionMap.put(path, value);
            section.save();
            return;
        }
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    public int getInteger(String path, SettingsSection section) {
        if (section != null) return ((Double) section.sectionMap.get(path)).intValue();
        return ((Double) settingsMap.get(path)).intValue();
    }

    public void setInteger(String path, int value, SettingsSection section) {
        if (section != null) {
            section.sectionMap.remove(path);
            section.sectionMap.put(path, value);
            section.save();
            return;
        }
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    public boolean getBoolean(String path, SettingsSection section) {
        if (section != null) return (Boolean) section.sectionMap.get(path);
        return (Boolean) settingsMap.get(path);
    }

    public void setBoolean(String path, boolean value, SettingsSection section) {
        if (section != null) {
            section.sectionMap.remove(path);
            section.sectionMap.put(path, value);
            section.save();
            return;
        }
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected List<?> getList(String path, SettingsSection section) {
        if (section != null) return (List<?>) section.sectionMap.get(path);
        return (List<?>) settingsMap.get(path);
    }

    protected void setList(String path, List<?> value, SettingsSection section) {
        if (section != null) {
            section.sectionMap.remove(path);
            section.sectionMap.put(path, value);
            section.save();
            return;
        }
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected List<String> getStringList(String path, SettingsSection section) {
        if (section != null) return (List<String>) section.sectionMap.get(path);
        return (List<String>) settingsMap.get(path);
    }

    protected void setStringList(String path, List<String> value, SettingsSection section) {
        if (section != null) {
            section.sectionMap.remove(path);
            section.sectionMap.put(path, value);
            section.save();
            return;
        }
        settingsMap.remove(path);
        settingsMap.put(path, value);
    }

    protected SettingsSection getSection(String path) {
        return new SettingsSection(path, settingsMap, null);
    }

    public void save() {
        Map<String, Object> parrentSettingsMap = parent.getSettingsMap();
        parrentSettingsMap.remove(settingsPath);
        parrentSettingsMap.put(settingsPath, settingsMap);
    }

    protected class SettingsSection {

        private String path;
        private Map<String, Object> sectionMap;
        private SettingsSection parent;

        protected SettingsSection(String path, Map<String, Object> settingsMap, SettingsSection parent) {
            this.path = path;
            this.sectionMap = (Map<String, Object>) settingsMap.get(path);
            if(parent != null)this.parent = parent;
        }

        public SettingsSection getSubSection(String path){
            return new SettingsSection(path, sectionMap, this);
        }

        protected void save() {
            if(parent != null){
                parent.sectionMap.remove(path);
                parent.sectionMap.put(path, this.sectionMap);
                parent.save();
                return;
            }
            settingsMap.remove(path);
            settingsMap.put(path, sectionMap);
        }
    }

}
