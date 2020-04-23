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

package com.azortis.protocolvanish.common;

public enum PluginVersion {
    v0_4_3("0.4.3", 43, "1", "1", "0"),
    v0_4_4("0.4.4", 44, "1", "1", "0"),
    v0_4_5("0.4.5", 45, "1", "1", "0"),
    v0_5("0.5", 50, "2", "2", "0"),
    v1_0("1.0", 100, "3", "3", "1");

    private final String versionString;
    private final int versionNumber;
    private final String settingsFileVersion;
    private final String messageFileVersion;
    private final String proxySettingsFileVersion;

    PluginVersion(String versionString, int versionNumber, String settingsFileVersion, String messageFileVersion, String proxySettingsFileVersion){
        this.versionString = versionString;
        this.versionNumber = versionNumber;
        this.settingsFileVersion = settingsFileVersion;
        this.messageFileVersion = messageFileVersion;
        this.proxySettingsFileVersion = proxySettingsFileVersion;
    }

    public boolean isNewerThen(PluginVersion pluginVersion){
        return this.versionNumber > pluginVersion.versionNumber;
    }

    public boolean isSame(PluginVersion pluginVersion){
        return this.versionNumber == pluginVersion.versionNumber;
    }

    public String getVersionString(){
        return versionString;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getSettingsFileVersion() {
        return settingsFileVersion;
    }

    public String getMessageFileVersion() {
        return messageFileVersion;
    }

    public String getProxySettingsFileVersion() {
        return proxySettingsFileVersion;
    }

    public static PluginVersion getVersionFromString(String versionString){
        String enumVersionString = "v" + versionString.replace(".", "_");
        return PluginVersion.valueOf(enumVersionString);
    }

}
