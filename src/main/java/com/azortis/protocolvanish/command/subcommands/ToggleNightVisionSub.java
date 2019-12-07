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
import com.azortis.protocolvanish.settings.MessageSettingsWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ToggleNightVisionSub implements ISubCommandExecutor {

    private ProtocolVanish plugin;

    public ToggleNightVisionSub(ProtocolVanish plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onSubCommand(CommandSender commandSender, SubCommand subCommand, String s, String[] strings) {
        if(commandSender instanceof ConsoleCommandSender){
            commandSender.sendMessage("This command cannot be run from console!");
            return false;
        }else if(commandSender instanceof Player){
            Player player = (Player)commandSender;
            MessageSettingsWrapper messageSettings = plugin.getSettingsManager().getMessageSettings();
            if(plugin.getPermissionManager().hasPermission(player, PermissionManager.Permission.CHANGE_NIGHT_VISION)){
                VanishPlayer vanishPlayer = plugin.getVanishPlayer(player);
                if(vanishPlayer == null)vanishPlayer = plugin.getStorageManager().createVanishPlayer(player);
                if(vanishPlayer.getPlayerSettings().doNightVision()){
                    vanishPlayer.getPlayerSettings().setNightVision(false);
                    if(vanishPlayer.isVanished())player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("disabledNightVision")));
                }else {
                    vanishPlayer.getPlayerSettings().setNightVision(true);
                    if(vanishPlayer.isVanished())player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("enabledNightVision")));
                }
                plugin.getStorageManager().savePlayerSettings(vanishPlayer.getPlayerSettings());
                return true;
            }else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSettings.getMessage("noPermission")));
                return false;
            }
        }
        return false;
    }
}
