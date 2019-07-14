package com.azortis.protocolvanish.command;

import com.azortis.azortislib.command.AlCommand;
import com.azortis.azortislib.command.CommandBuilder;
import com.azortis.azortislib.command.IAlCommandExecutor;
import com.azortis.azortislib.command.IAlTabCompleter;
import com.azortis.protocolvanish.ProtocolVanish;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VanishCommand implements IAlCommandExecutor, IAlTabCompleter {

    private ProtocolVanish plugin;

    public VanishCommand(ProtocolVanish plugin){
        this.plugin = plugin;
        AlCommand command = new CommandBuilder()
                .setName("vanish").build();
        command.setExecutor(this);
        plugin.getAzortisLib().getCommandManager().register("vanish", command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, AlCommand alCommand, String s, String[] strings) {
        Player p = (Player)commandSender;
        if(plugin.getVisibilityManager().isVanished(p.getUniqueId())){
            plugin.getVisibilityManager().setVanished(p.getUniqueId(), false);
        }else{
            plugin.getVisibilityManager().setVanished(p.getUniqueId(), true);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s, String[] strings, Location location) {
        return null;
    }
}
