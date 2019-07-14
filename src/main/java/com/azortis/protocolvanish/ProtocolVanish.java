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
