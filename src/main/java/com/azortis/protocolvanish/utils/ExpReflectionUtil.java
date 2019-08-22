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

package com.azortis.protocolvanish.utils;

import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExpReflectionUtil {

    private static Class craftHumanEntityClass;
    private static String expCooldownFieldName;

    public static void setExpPickup(Player player, boolean pickup){
        if(craftHumanEntityClass == null){
            craftHumanEntityClass = MinecraftReflection.getCraftBukkitClass("entity.CraftHumanEntity");
            String minecraftVersion = MinecraftReflection.getPackageVersion();
            switch (minecraftVersion){
                case "v1_13_R2":
                    expCooldownFieldName = "bJ";
                    break;
                case "v1_14_R1":
                    expCooldownFieldName = "bF";
                    break;
                default:
                    Bukkit.getLogger().warning("This MC version is not supported: " + minecraftVersion);
            }
        }
        try {
            Method getHandle = player.getClass().getSuperclass().getMethod("getHandle");
            Object entityHuman = getHandle.invoke(player);
            Field expCooldownField = MinecraftReflection.getEntityHumanClass().getDeclaredField(expCooldownFieldName);
            if(!pickup){
                expCooldownField.set(entityHuman, Integer.MAX_VALUE);
                return;
            }
            expCooldownField.set(entityHuman, 0);
        }catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex){
            ex.printStackTrace();
        }

    }

}
