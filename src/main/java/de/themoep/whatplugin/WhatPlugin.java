package de.themoep.whatplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

/**
 * WhatPlugin
 * Copyright (C) 2022 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class WhatPlugin extends JavaPlugin {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            String commandStr = args[0].startsWith("/") ? args[0].substring(1) : args[0];

            // Try to get alias
            String[] aliased = getServer().getCommandAliases().get(commandStr);
            if (aliased == null) {
                aliased = getServer().getCommandAliases().get(commandStr.toLowerCase(Locale.ROOT));
            }
            if (aliased != null) {
                sender.sendMessage(ChatColor.GREEN + "Found alias " + ChatColor.YELLOW + commandStr + ChatColor.GREEN + ":");
                for (String alias : aliased) {
                    sender.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + alias);
                }
                return true;
            }

            // Try to get plugin command
            Command command = getCommandMap().getCommand(commandStr);
            if (command != null) {
                Plugin plugin = getPlugin(command);
                sender.sendMessage(ChatColor.GREEN + "Found command " + ChatColor.YELLOW + command.getName() + ChatColor.GREEN + " in "
                        + (plugin != null ? ("plugin " + ChatColor.YELLOW + plugin.getName()) : "server"));
                sender.sendMessage(ChatColor.GREEN + "Description: " + ChatColor.YELLOW + command.getDescription());
                sender.sendMessage(ChatColor.GREEN + "Permission: " + ChatColor.YELLOW + (command.getPermission() != null ? command.getPermission() : "None? That's strange!"));
                sender.sendMessage(ChatColor.GREEN + "Aliases: " + ChatColor.YELLOW + (command.getAliases().size() > 0 ? (Arrays.toString(command.getAliases().toArray())) : "None"));
                return true;
            }

            sender.sendMessage(ChatColor.RED + "No plugin or server command with the name " + ChatColor.YELLOW + commandStr + ChatColor.RED + " found?");
            return true;
        }
        return false;
    }

    private CommandMap getCommandMap() {
        try {
            return getServer().getCommandMap();
        } catch (NoSuchMethodError e) {
            try {
                Field commandMap = getServer().getClass().getField("commandMap");
                commandMap.setAccessible(true);
                return (CommandMap) commandMap.get(getServer());
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private Plugin getPlugin(Command command) {
        if (command instanceof PluginCommand) {
            return ((PluginCommand) command).getPlugin();
        }
        ClassLoader loader = command.getClass().getClassLoader();
        if (loader instanceof PluginClassLoader) {
            return ((PluginClassLoader) loader).getPlugin();
        }
        return null;
    }

}
