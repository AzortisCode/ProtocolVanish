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

package com.azortis.protocolvanish.common.storage.drivers;

import com.azortis.protocolvanish.common.VanishPlayer;
import com.azortis.protocolvanish.common.storage.Driver;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public class H2Driver implements Driver {



    public H2Driver(File dataFolder) {

    }

    @Override
    public VanishPlayer getVanishPlayer(UUID uuid) {
        return null;
    }

    @Override
    public void saveVanishPlayer(VanishPlayer vanishPlayer) {

    }

    @Override
    public void deleteVanishPlayer(UUID uuid) {

    }

    @Override
    public void createVanishPlayer(VanishPlayer vanishPlayer) {

    }

    @Override
    public Collection<UUID> getVanishedUUIDs() {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public String getTablePrefix() {
        return null;
    }
}
