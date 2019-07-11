package com.azortis.protocolvanish.command;

import com.azortis.azortislib.command.AlCommand;
import com.azortis.azortislib.command.IAlCommandExecutor;
import com.azortis.azortislib.command.IAlTabCompleter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VanishCommand implements IAlCommandExecutor, IAlTabCompleter {

    public VanishCommand(){

    }

    @Override
    public boolean onCommand(CommandSender commandSender, AlCommand alCommand, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String s, String[] strings, Location location) {
        return null;
    }
}
