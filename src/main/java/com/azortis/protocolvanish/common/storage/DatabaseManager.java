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

package com.azortis.protocolvanish.common.storage;

import com.azortis.protocolvanish.common.storage.drivers.SQLiteDriver;
import com.azortis.protocolvanish.common.storage.drivers.MariaDBDriver;

import java.io.File;

public class DatabaseManager {

    private Driver driver;

    public DatabaseManager(Object plugin, StorageSettings storageSettings, File dataFolder){
        if(storageSettings.getDriver().equalsIgnoreCase("MariaDB")){
            DriverLoader.loadDriver(plugin, dataFolder, "mariadb-driver-2.6.0.jar", "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/2.6.0/mariadb-java-client-2.6.0.jar");
            driver = new MariaDBDriver(storageSettings);
        }else if(storageSettings.getDriver().equalsIgnoreCase("SQLite")){
            DriverLoader.loadDriver(plugin, dataFolder, "sqlite-jdbc-3.32.3.jar", "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.32.3/sqlite-jdbc-3.32.3.jar");
            driver = new SQLiteDriver(dataFolder);
        }
    }

    public Driver getDriver() {
        return driver;
    }
}
