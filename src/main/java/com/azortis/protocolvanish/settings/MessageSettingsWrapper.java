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
public class MessageSettingsWrapper {

    private SettingsManager parent;
    private Map<String, Object> settingsMap;
    private Map<String, Object> messageMap;

    public MessageSettingsWrapper(SettingsManager parent, Object settingsMap, Object messageMap){
        this.parent = parent;
        this.settingsMap = (Map<String, Object>) settingsMap;
        this.messageMap = (Map<String, Object>) messageMap;
    }

    public String getMessage(String messagePath){
        return (String) messageMap.get(messagePath);
    }

    public void setMessage(String messagePath, String message){
        messageMap.remove(messagePath);
        messageMap.put(messagePath, message);
    }

    public boolean getBroadCastFakeQuitOnVanish(){
        return (Boolean) settingsMap.get("broadCastFakeQuitOnVanish");
    }

    public void setBroadCastFakeQuitOnVanish(boolean broadCastFakeQuitOnVanish){
        settingsMap.remove("broadCastFakeQuitOnVanish");
        settingsMap.put("broadCastFakeQuitOnVanish", broadCastFakeQuitOnVanish);
    }

    public boolean getBroadCastFakeJoinOnReappear(){
        return (Boolean) settingsMap.get("broadCastFakeJoinOnReappear");
    }

    public void setBroadCastJoinQuitOnReappear(boolean broadCastFakeJoinOnReappear){
        settingsMap.remove("broadCastFakeJoinOnReappear");
        settingsMap.put("broadCastFakeJoinOnReappear", broadCastFakeJoinOnReappear);
    }

    public boolean getAnnounceVanishStateToAdmins(){
        return (Boolean) settingsMap.get("announceVanishStateToAdmins");
    }

    public void setAnnounceVanishStateToAdmins(boolean announceVanishStateToAdmins){
        settingsMap.remove("announceVanishStateToAdmins");
        settingsMap.put("announceVanishStateToAdmins", announceVanishStateToAdmins);
    }

    public boolean getSendFakeJoinQuitMessagesOnlyToUsers(){
        return (Boolean) settingsMap.get("sendFakeJoinQuitMessagesOnlyToUsers");
    }

    public void setSendFakeJoinQuitMessagesOnlyToUsers(boolean sendFakeJoinQuitMessagesOnlyToUsers){
        settingsMap.remove("sendFakeJoinQuitMessagesOnlyToUsers");
        settingsMap.put("sendFakeJoinQuitMessagesOnlyToUsers", sendFakeJoinQuitMessagesOnlyToUsers);
    }

    public boolean getHideRealJoinQuitMessages(){
        return (Boolean) settingsMap.get("hideRealJoinQuitMessages");
    }

    public void setHideRealJoinQuitMessages(boolean hideRealJoinQuitMessages){
        settingsMap.remove("hideRealJoinQuitMessages");
        settingsMap.put("hideRealJoinQuitMessages", hideRealJoinQuitMessages);
    }

    public boolean getDisplayActionBar(){
        return (Boolean) settingsMap.get("displayActionBar");
    }

    public void setDisplayActionBar(boolean displayActionBar){
        settingsMap.remove("displayActionBar");
        settingsMap.put("displayActionBar", displayActionBar);
    }

    public void save(){
        Map<String, Object> parentSettingsMap = parent.getSettingsMap();
        parentSettingsMap.remove("messageSettings");
        parentSettingsMap.put("messageSettings", settingsMap);

        Map<String, Object> parentMessageMap = parent.getMessageMap();
        parentMessageMap.remove("messages");
        parentMessageMap.put("messages", messageMap);
    }

}
