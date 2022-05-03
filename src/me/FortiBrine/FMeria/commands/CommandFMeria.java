package me.FortiBrine.FMeria.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.FortiBrine.FMeria.FMeria;

public class CommandFMeria implements CommandExecutor {
	
	private FMeria plugin;
	public CommandFMeria(FMeria plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length==1 && args[0].equals("reload") && sender.hasPermission("fmeria.reload")) {
			plugin.reloadConfig();
			sender.sendMessage("Конфигурация перезагржуена!");
			return true;
		}
		
		return false;
	}

}
