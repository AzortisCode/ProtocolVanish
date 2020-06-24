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

import com.azortis.azortislib.utils.FileUtils;
import com.azortis.protocolvanish.bukkit.ProtocolVanish;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@SuppressWarnings("all")
public class SettingsManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Settings settings;
    private File settingsFile;

    private Messages messages;
    private File messagesFile;

    public SettingsManager(ProtocolVanish plugin){
        if(!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        settingsFile = new File(plugin.getDataFolder(), "settings.json");
        if(!settingsFile.exists()) FileUtils.copy(plugin.getResource("settings.json"), settingsFile);
        messagesFile = new File(plugin.getDataFolder(), "messages.json");
        if(!messagesFile.exists()) FileUtils.copy(plugin.getResource("messages.json"), messagesFile);
        try{
            settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
            messages = gson.fromJson(new FileReader(messagesFile), Messages.class);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        if(!plugin.getPluginVersion().getSettingsFileVersion().equals(settings.getFileVersion())){
            plugin.getLogger().severe("You're settingsfile is out of date! Please update otherwise it will break!");
        }
        if(!plugin.getPluginVersion().getMessageFileVersion().equals(messages.getFileVersion())){
            plugin.getLogger().severe("You're messagesfile is out of date! Please update otherwise it will break!");
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public void saveSettings(){
        try{
            final String json = gson.toJson(settings);
            settingsFile.delete();
            Files.write(settingsFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void reloadSettings(){
        try{
            settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public Messages getMessages() {
        return messages;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public void saveMessages(){
        try{
            final String json = gson.toJson(messages);
            messagesFile.delete();
            Files.write(messagesFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void reloadMessages(){
        try{
            messages = gson.fromJson(new FileReader(messagesFile), Messages.class);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

}
