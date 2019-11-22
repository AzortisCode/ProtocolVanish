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

package com.azortis.protocolvanish.storage;

import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.VanishPlayer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteAdapter implements IDatabase{

    private ProtocolVanish plugin;
    private String jdbcurl;

    SQLiteAdapter(ProtocolVanish plugin){
        this.plugin = plugin;
        File dbFile = new File(plugin.getDataFolder(), "storage.db");
        try{
            if(!dbFile.exists()){
                dbFile.createNewFile();
            }
        }catch (IOException ex){
            ex.printStackTrace();
            plugin.getAzortisLib().getLogger().severe("Cannot create database file, shutting down!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
        this.jdbcurl = "jdbc:sqlite:" + dbFile.getPath();
    }

    @Override
    public boolean hasDatabaseEntry(UUID uuid) {
        return false;
    }

    @Override
    public VanishPlayer getVanishPlayer(UUID uuid) {
        return null;
    }

    @Override
    public VanishPlayer.PlayerSettings getPlayerPreferences(UUID uuid) {
        return null;
    }

    private Connection createConnection(){
        try{
            return DriverManager.getConnection(jdbcurl);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

}
