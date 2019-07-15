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

package com.azortis.protocolvanish;

import com.azortis.azortislib.AzortisLib;
import com.azortis.protocolvanish.command.VanishCommand;
import com.azortis.protocolvanish.events.LoginEvent;
import com.azortis.protocolvanish.visibility.VisibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProtocolVanish extends JavaPlugin {

    private AzortisLib azortisLib;
    private PermissionManager permissionManager;
    private VisibilityManager visibilityManager;

    @Override
    public void onEnable() {
        this.azortisLib = new AzortisLib(this, "ProtocolVanish", "§1[§9ProtocolVanish§1]§7");
        this.permissionManager = new PermissionManager(this);
        this.visibilityManager = new VisibilityManager(this);

        new VanishCommand(this);
        new LoginEvent(this);
    }

    @Override
    public void onDisable() {

    }

    public AzortisLib getAzortisLib() {
        return azortisLib;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
