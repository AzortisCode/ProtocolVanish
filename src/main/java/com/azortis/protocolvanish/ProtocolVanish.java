package com.azortis.protocolvanish;

import com.azortis.azortislib.AzortisLib;
import com.azortis.protocolvanish.visibility.VisibilityManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProtocolVanish extends JavaPlugin {

    public static AzortisLib al;

    private VisibilityManager visibilityManager;

    @Override
    public void onEnable() {
        al = new AzortisLib(this, "ProtocolVanish", "§1[§9ProtocolVanish§1]§7");
        this.visibilityManager = new VisibilityManager(this);
    }

    @Override
    public void onDisable() {

    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
