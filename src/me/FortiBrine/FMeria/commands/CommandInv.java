package me.FortiBrine.FMeria.commands;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings("unused")
public class CommandInv implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandInv(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);

		if (!(sender instanceof Player)) {
			sender.sendMessage(messageConfig.getString("message.notPlayer"));
			return true;
		}
		Player p = (Player) sender;
		if (args.length!=1) {
			return false;
		}
		if ((!args[0].equals("accept")) && (!args[0].equals("decline"))) {
			return false;
		}
		if ((!plugin.invfrac.containsKey(p)) || (!plugin.invtime.containsKey(p))) {
			p.sendMessage(messageConfig.getString("message.hasnotInvites"));
			return true;
		}
		if (System.currentTimeMillis()>plugin.invtime.get(p)) {
			p.sendMessage(messageConfig.getString("message.hasnotInvites"));
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			return true;
		}
		if (args[0].equals("decline")) {
			String faction = plugin.invfrac.get(p);
			
			String message = messageConfig.getString("message.declineAll");
			
			message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
			message = message.replace("%user", p.getName());
			message = message.replace("%player", p.getDisplayName());
			
			Set<String> players = new HashSet<String>();
			players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				if (players.contains(ps.getName())) {
					ps.sendMessage(message);
				}
			}
			p.sendMessage(messageConfig.getString("message.declineInvite"));
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			return true;
		}
		if (args[0].equals("accept")) {
			String faction = plugin.invfrac.get(p);
			
			String message = messageConfig.getString("message.acceptAll");
			
			message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
			message = message.replace("%user", p.getName());
			message = message.replace("%player", p.getDisplayName());
			
			Set<String> players = new HashSet<String>();
			players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				if (players.contains(ps.getName())) {
					ps.sendMessage(message);
				}
			}
			p.sendMessage(messageConfig.getString("message.acceptInvite"));
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			plugin.getConfig().set(faction+".users."+p.getName(), 1);
			plugin.saveConfig();
			plugin.reloadConfig();
			return true;
		}
		
		return true;
	}

}
