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

@SuppressWarnings("all")
public class SettingsManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Settings settings;
    private File settingsFile;

    public SettingsManager(ProtocolVanish plugin){
        if(!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        settingsFile = new File(plugin.getDataFolder(), "settings.json");
        if(!settingsFile.exists()) FileUtils.copy(plugin.getResource("settings.json"), settingsFile);
        try{
            settings = gson.fromJson(new FileReader(settingsFile), Settings.class);
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        if(!plugin.getUpdateChecker().getPluginVersion().getSettingsFileVersion().equals(settings.getFileVersion())){
            plugin.getLogger().severe("You're settingsfile is out of date! Please update otherwise it will break!");
        }
    }

}
