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

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PluginVersionTest {

    @Test
    public void getPluginVersionsFromString(){
        assertEquals(PluginVersion.v0_4_3, PluginVersion.getVersionFromString("0.4.3"), "0.4.3 should translate to PluginVersion.v0_4_3");
        assertEquals(PluginVersion.v0_4_4, PluginVersion.getVersionFromString("0.4.4"), "0.4.4 should translate to PluginVersion.v0_4_4");
        assertEquals(PluginVersion.v0_4_5, PluginVersion.getVersionFromString("0.4.5"), "0.4.5 should translate to PluginVersion.v0_4_4");
    }

    @Test
    public void isNewerThan(){
        assertTrue(PluginVersion.v0_4_4.isNewerThen(PluginVersion.v0_4_3), "0.4.4 should be newer then 0.4.3");
        assertTrue(PluginVersion.v0_4_5.isNewerThen(PluginVersion.v0_4_4), "0.4.5 should be newer then 0.4.4");
        assertFalse(PluginVersion.v0_4_3.isNewerThen(PluginVersion.v0_4_3), "0.4.3 shouldn't be newer then 0.4.4");
    }

    @Test
    public void getVersionNumber(){
        assertEquals(43, PluginVersion.v0_4_3.getVersionNumber(), "PluginVersion.v0_4_3 its version number should equal to 43");
        assertEquals(44, PluginVersion.v0_4_4.getVersionNumber(), "PluginVersion.v0_4_4 its version number should equal to 44");
        assertEquals(45, PluginVersion.v0_4_5.getVersionNumber(), "PluginVersion.v0_4_5 its version number should equal to 44");
    }

    @Test
    public void getVersionString(){
        assertEquals("0.4.3", PluginVersion.v0_4_3.getVersionString(), "PluginVersion.v0_4_3 its version string should equal to 0.4.3");
        assertEquals("0.4.4", PluginVersion.v0_4_4.getVersionString(), "PluginVersion.v0_4_4 its version string should equal to 0.4.4");
        assertEquals("0.4.5", PluginVersion.v0_4_5.getVersionString(), "PluginVersion.v0_4_5 its version string should equal to 0.4.5");
    }

}
