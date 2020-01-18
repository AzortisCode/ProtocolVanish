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

package com.azortis.protocolvanish.hooks;

import com.azortis.protocolvanish.ProtocolVanish;

import java.util.HashMap;
import java.util.Map;

public class HookManager {

    private ProtocolVanish plugin;
    private Map<String, PluginHook> hookMap = new HashMap<>();

    public HookManager(ProtocolVanish plugin){
        this.plugin = plugin;
        plugin.getLogger().info("Loading hooks...");
        loadHooks();
    }

    public PluginHook getPluginHook(String name){
        if(hookMap.containsKey(name))return hookMap.get(name);
        return null;
    }

    private void loadHooks(){
        hookMap.put("Dynmap", new DynmapHook(plugin));
    }

}
