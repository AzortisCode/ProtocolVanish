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

package com.azortis.protocolvanish.bukkit.settings.wrappers;

import com.azortis.protocolvanish.bukkit.settings.SettingsManager;
import com.azortis.protocolvanish.bukkit.settings.SettingsWrapper;

import java.util.Map;

@SuppressWarnings("all")
public class MessageSettingsWrapper extends SettingsWrapper {

    private Map<String, Object> messageMap;

    public MessageSettingsWrapper(SettingsManager parent, Object settingsMap, Object messageMap) {
        super(parent, settingsMap, "messageSettings");
        this.messageMap = (Map<String, Object>) messageMap;
    }

    public String getMessage(String messagePath) {
        return (String) messageMap.get(messagePath);
    }

    public void setMessage(String messagePath, String message) {
        messageMap.remove(messagePath);
        messageMap.put(messagePath, message);
    }

    public boolean getBroadCastFakeQuitOnVanish() {
        return getBoolean("broadCastFakeQuitOnVanish", null);
    }

    public void setBroadCastFakeQuitOnVanish(boolean broadCastFakeQuitOnVanish) {
        setBoolean("broadCastFakeQuitOnVanish", broadCastFakeQuitOnVanish, null);
    }

    public boolean getBroadCastFakeJoinOnReappear() {
        return getBoolean("broadCastFakeJoinOnReappear", null);
    }

    public void setBroadCastJoinQuitOnReappear(boolean broadCastFakeJoinOnReappear) {
        setBoolean("broadCastFakeJoinOnReappear", broadCastFakeJoinOnReappear, null);
    }

    public boolean getAnnounceVanishStateToAdmins() {
        return getBoolean("announceVanishStateToAdmins", null);
    }

    public void setAnnounceVanishStateToAdmins(boolean announceVanishStateToAdmins) {
        setBoolean("announceVanishStateToAdmins", announceVanishStateToAdmins, null);
    }

    public boolean getSendFakeJoinQuitMessagesOnlyToUsers() {
        return getBoolean("sendFakeJoinQuitMessagesOnlyToUsers", null);
    }

    public void setSendFakeJoinQuitMessagesOnlyToUsers(boolean sendFakeJoinQuitMessagesOnlyToUsers) {
        setBoolean("sendFakeJoinQuitMessagesOnlyToUsers", sendFakeJoinQuitMessagesOnlyToUsers, null);
    }

    public boolean getHideRealJoinQuitMessages() {
        return getBoolean("hideRealJoinQuitMessages", null);
    }

    public void setHideRealJoinQuitMessages(boolean hideRealJoinQuitMessages) {
        setBoolean("hideRealJoinQuitMessages", hideRealJoinQuitMessages, null);
    }

    public boolean getDisplayActionBar() {
        return getBoolean("displayActionBar", null);
    }

    public void setDisplayActionBar(boolean displayActionBar) {
        setBoolean("displayActionBar", displayActionBar, null);
    }

    @Override
    public void save() {
        super.save();
        Map<String, Object> parentMessageMap = parent.getMessageMap();
        parentMessageMap.remove("messages");
        parentMessageMap.put("messages", messageMap);
    }

}
