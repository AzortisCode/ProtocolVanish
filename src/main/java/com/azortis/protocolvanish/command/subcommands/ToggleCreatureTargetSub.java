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

package com.azortis.protocolvanish.command.subcommands;

import com.azortis.azortislib.command.SubCommand;
import com.azortis.azortislib.command.executors.ISubCommandExecutor;
import com.azortis.protocolvanish.PermissionManager;
import com.azortis.protocolvanish.ProtocolVanish;
import com.azortis.protocolvanish.VanishPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class ToggleCreatureTargetSub implements ISubCommandExecutor {

    private final ProtocolVanish plugin;

    public ToggleCreatureTargetSub(ProtocolVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onSubCommand(CommandSender commandSender, SubCommand subCommand, String s, String[] strings) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("This command cannot be run from console!");
            return false;
        } else if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (plugin.getSettingsManager().getCommandSettings().isSubCommandEnabled("toggleCreatureTarget")) {
                if (plugin.getPermissionManager().hasPermissionToVanish(player) && plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.CHANGE_CREATURE_TARGET)) {
                    VanishPlayer vanishPlayer = plugin.getVanishPlayer(player.getUniqueId());
                    if (vanishPlayer == null) vanishPlayer = plugin.createVanishPlayer(player);
                    if (vanishPlayer.getPlayerSettings().getDisableCreatureTarget()) {
                        vanishPlayer.getPlayerSettings().setDisableCreatureTarget(false);
                        plugin.sendPlayerMessage(player, "enabledCreatureTarget");
                    } else {
                        vanishPlayer.getPlayerSettings().setDisableCreatureTarget(true);
                        for (Mob mob : player.getWorld().getEntitiesByClass(Mob.class)) {
                            if (mob.getTarget() == player) mob.setTarget(null);
                        }
                        plugin.sendPlayerMessage(player, "disabledCreatureTarget");
                    }
                    plugin.getStorageManager().savePlayerSettings(vanishPlayer.getPlayerSettings());
                    return true;
                } else {
                    plugin.sendPlayerMessage(player, "noPermission");
                    return false;
                }
            } else {
                plugin.sendPlayerMessage(player, "invalidUsage");
            }
        }
        return false;
    }

}
