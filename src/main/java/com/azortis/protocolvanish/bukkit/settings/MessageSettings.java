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

package com.azortis.protocolvanish.bukkit.settings;

public class MessageSettings {

    private boolean notifyUpdates;
    private boolean broadCastFakeQuitOnVanish;
    private boolean broadCastFakeJoinOnReappear;
    private boolean announceVanishStateToAdmins;
    private boolean sendFakeJoinQuitMessagesOnlyToUsers;
    private boolean hideRealJoinQuitMessages;
    private boolean displayActionBar;

    public boolean getNotifyUpdates() {
        return notifyUpdates;
    }

    public void setNotifyUpdates(boolean notifyUpdates) {
        this.notifyUpdates = notifyUpdates;
    }

    public boolean getBroadCastFakeQuitOnVanish() {
        return broadCastFakeQuitOnVanish;
    }

    public void setBroadCastFakeQuitOnVanish(boolean broadCastFakeQuitOnVanish) {
        this.broadCastFakeQuitOnVanish = broadCastFakeQuitOnVanish;
    }

    public boolean getBroadCastFakeJoinOnReappear() {
        return broadCastFakeJoinOnReappear;
    }

    public void setBroadCastFakeJoinOnReappear(boolean broadCastFakeJoinOnReappear) {
        this.broadCastFakeJoinOnReappear = broadCastFakeJoinOnReappear;
    }

    public boolean getAnnounceVanishStateToAdmins() {
        return announceVanishStateToAdmins;
    }

    public void setAnnounceVanishStateToAdmins(boolean announceVanishStateToAdmins) {
        this.announceVanishStateToAdmins = announceVanishStateToAdmins;
    }

    public boolean getSendFakeJoinQuitMessagesOnlyToUsers() {
        return sendFakeJoinQuitMessagesOnlyToUsers;
    }

    public void setSendFakeJoinQuitMessagesOnlyToUsers(boolean sendFakeJoinQuitMessagesOnlyToUsers) {
        this.sendFakeJoinQuitMessagesOnlyToUsers = sendFakeJoinQuitMessagesOnlyToUsers;
    }

    public boolean getHideRealJoinQuitMessages() {
        return hideRealJoinQuitMessages;
    }

    public void setHideRealJoinQuitMessages(boolean hideRealJoinQuitMessages) {
        this.hideRealJoinQuitMessages = hideRealJoinQuitMessages;
    }

    public boolean getDisplayActionBar() {
        return displayActionBar;
    }

    public void setDisplayActionBar(boolean displayActionBar) {
        this.displayActionBar = displayActionBar;
    }
}
