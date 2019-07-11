package com.azortis.protocolvanish;

import com.azortis.azortislib.AzortisLib;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProtocolVanish extends JavaPlugin {

    public static AzortisLib al;

    @Override
    public void onEnable() {
        al = new AzortisLib(this, "ProtocolVanish", "§1[§9ProtocolVanish§1]§7");
    }

    @Override
    public void onDisable() {

    }

}
