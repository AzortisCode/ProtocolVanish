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

import com.azortis.azortislib.database.IDatabase;
import com.azortis.azortislib.database.SQLiteSettings;

import java.sql.*;
import java.util.UUID;

public class StorageManager {

    private ProtocolVanish plugin;
    private IDatabase database;

    public StorageManager(ProtocolVanish plugin){
        this.plugin = plugin;
        SQLiteSettings sqLiteSettings = new SQLiteSettings("storage", plugin.getDataFolder().getPath());
        plugin.getAzortisLib().getDatabaseManager().useSQLite(sqLiteSettings);
        this.database = plugin.getAzortisLib().getDatabaseManager().getDatabase();
        try(Connection connection = database.getConnection()){
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS userSettings(uuid varchar(36), vanished bool, nightVision bool, hunger bool, damage bool, creatureTarget bool, itemPickup bool)");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Deprecated
    public void setVanished(UUID uuid){
        if(!exists(uuid)){
            createVanishPlayer(uuid, plugin.getVanishPlayer(uuid));
            return;
        }
        VanishPlayer vanishPlayer = plugin.getVanishPlayer(uuid);
        try(Connection connection = database.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE userSettings SET vanished=? WHERE uuid=?");
            preparedStatement.setBoolean(1, vanishPlayer.isVanished());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.execute();
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Deprecated
    public boolean isVanished(UUID uuid){
        boolean vanished;
        try(Connection connection = database.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM userSettings WHERE uuid=?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                vanished = result.getBoolean("vanished");
            }else {
                vanished = false;
            }
            result.close();
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
            vanished = false;
        }
        return vanished;
    }

    private void createVanishPlayer(UUID uuid, VanishPlayer vanishPlayer){
        try(Connection connection = database.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO userSettings VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBoolean(2, vanishPlayer.isVanished());
            preparedStatement.setBoolean(3, vanishPlayer.doNightVision());
            preparedStatement.setBoolean(4, vanishPlayer.doDamage());
            preparedStatement.setBoolean(5, vanishPlayer.doHunger());
            preparedStatement.setBoolean(6, vanishPlayer.doCreatureTarget());
            preparedStatement.setBoolean(7, vanishPlayer.doItemPickUp());
            preparedStatement.execute();
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean exists(UUID uuid){
        boolean exists;
        try(Connection connection = database.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM userSettings WHERE uuid=?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet result = preparedStatement.executeQuery();
            exists = result.next();
        }catch (SQLException e){
            e.printStackTrace();
            exists = false;
        }
        return exists;
    }

}
