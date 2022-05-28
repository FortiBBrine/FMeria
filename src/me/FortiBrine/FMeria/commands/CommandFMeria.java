package me.FortiBrine.FMeria.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import me.FortiBrine.FMeria.FMeria;

public class CommandFMeria implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFMeria(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		
		if (!sender.hasPermission(plugin.getDescription().getPermissions().get(0))) {
			sender.sendMessage(messageConfig.getString("message.reloadConfigPermission"));
			return true;
		}
		
		if (args.length < 1) {
			return false;
		}
		
		if (args[0].equals("reload")) {
			sender.sendMessage(messageConfig.getString("message.reloadConfig"));
			plugin.reloadConfig();
			return true;
		}
		
		return true;
	}

}
