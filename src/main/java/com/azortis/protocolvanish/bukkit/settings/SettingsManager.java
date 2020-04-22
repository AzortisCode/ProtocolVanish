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

import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.azortis.protocolvanish.bukkit.settings.wrappers.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@SuppressWarnings("all")
public class SettingsManager {

    private final ProtocolVanish plugin;
    private File settingsFile;
    private File messageFile;

    private Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private Map<String, Object> settingsMap;
    private Map<String, Object> messageMap;
    private boolean filesUpToDate = true;

    public SettingsManager(ProtocolVanish plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("Loading settings...");
        if (plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        settingsFile = new File(plugin.getDataFolder(), "settings.json");
        messageFile = new File(plugin.getDataFolder(), "messages.json");
        if (!settingsFile.exists()){
            plugin.getLogger().info("Creating new settings file...");
            plugin.saveResource(settingsFile.getName(), false);
        }
        if (!messageFile.exists()){
            plugin.getLogger().info("Creating new message file...");
            plugin.saveResource(messageFile.getName(), false);
        }
        try {
            settingsMap = gson.fromJson(new FileReader(settingsFile), Map.class);
            messageMap = gson.fromJson(new FileReader(messageFile), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Check if settingsFile is up to date!
        String settingsFileVersion = (String) settingsMap.get("fileVersion");
        if(!plugin.getUpdateChecker().getPluginVersion().getSettingsFileVersion().equals(settingsFileVersion)){
            plugin.getLogger().severe("Settings file is outdated, Please update it!");
            filesUpToDate = false;
        }
        //Check if messageFile is up to date!
        String messageFileVersion = (String) settingsMap.get("fileVersion");
        if(!plugin.getUpdateChecker().getPluginVersion().getMessageFileVersion().equals(messageFileVersion)){
            plugin.getLogger().severe("Message file is outdated, Please update it!");
            filesUpToDate = false;
        }
        if(!filesUpToDate){
            plugin.getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public boolean areFilesUpToDate() {
        return filesUpToDate;
    }

    public void saveSettingsFile() {
        try {
            final String json = gson.toJson(settingsMap);
            settingsFile.delete();
            Files.write(settingsFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadSettingsFile() {
        try {
            settingsMap = gson.fromJson(new FileReader(settingsFile), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessageFile() {
        try {
            final String json = gson.toJson(messageMap);
            messageFile.delete();
            Files.write(messageFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadMessageFile() {
        try {
            messageMap = gson.fromJson(new FileReader(messageFile), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MessageSettingsWrapper getMessageSettings() {
        return new MessageSettingsWrapper(this, settingsMap.get("messageSettings"), messageMap.get("messages"));
    }

    public PermissionSettingsWrapper getPermissionSettings() {
        return new PermissionSettingsWrapper(this, settingsMap.get("permissionSettings"));
    }

    public InvisibilitySettingsWrapper getInvisibilitySettings() {
        return new InvisibilitySettingsWrapper(this, settingsMap.get("invisibilitySettings"));
    }

    public VisibilitySettingsWrapper getVisibilitySettings() {
        return new VisibilitySettingsWrapper(this, settingsMap.get("visibilitySettings"));
    }

    public CommandSettingsWrapper getCommandSettings() {
        return new CommandSettingsWrapper(this, settingsMap.get("commandSettings"));
    }

    public HookSettingsWrapper getHookSettings(){
        return new HookSettingsWrapper(this, settingsMap.get("hookSettings"));
    }

    /*public StorageSettingsWrapper getStorageSettings(){
        return new StorageSettingsWrapper(this, settingsMap.get("StorageSettings"));
    }*/

    public Map<String, Object> getSettingsMap() {
        return settingsMap;
    }

    public Map<String, Object> getMessageMap() {
        return messageMap;
    }

}
