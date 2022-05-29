package me.FortiBrine.FMeria.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandSetrank implements CommandExecutor, TabCompleter {

	private File messages;
	private FMeria plugin;
	public CommandSetrank(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		
		if (args.length<3) {
			return false;
		}
		if (!s.hasPermission(plugin.getDescription().getPermissions().get(1))) {
			s.sendMessage(messageConfig.getString("message.setrankConfigPermission"));
			return true;
		}
		if (!plugin.getConfig().getKeys(false).contains(args[1])) {
			s.sendMessage(messageConfig.getString("message.factionNotExists"));
			return true;
		}
		int newrank;
		try {
			newrank = Integer.parseInt(args[2]);
		} catch (NumberFormatException nfe) {
			s.sendMessage(messageConfig.getString("message.NumberFormatException"));
			return true;
		}
		Player p = Bukkit.getPlayer(args[0]);
		String faction = null;
		for (String css : plugin.getConfig().getKeys(false)) {
			ConfigurationSection cs = plugin.getConfig().getConfigurationSection(css);
			ConfigurationSection cs2 = cs.getConfigurationSection("users");

			if (cs2.getKeys(false).contains(p.getName())) {

				faction = css;
				break;
			}
		}
		if (faction!=null) {
			plugin.getConfig().set(faction+".users."+p.getName(), null);
		}
		plugin.getConfig().set(args[1]+".users."+args[0], newrank);
		plugin.saveConfig();
		plugin.reloadConfig();
		s.sendMessage(messageConfig.getString("message.setrank"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		Set<String> factions = plugin.getConfig().getKeys(false);
		List<String> result = new ArrayList<>();
		if (args.length == 1) {
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				String name = player.getName();
				
				if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(name);
				}
			}

		}

		if (args.length == 2) {
			
			for (String faction : factions) {
				if (faction.toLowerCase().startsWith(args[1].toLowerCase())) {
					result.add(faction);
				}
			}

		}
		if (args.length == 3) {

			if (!factions.contains(args[1])) return result;
			int countRanks = plugin.getConfig().getStringList(args[1]+".ranks").size(); 
			int[] ranks = IntStream.rangeClosed(1, countRanks).toArray();
			
			for (int i : ranks) {
				result.add(""+i);
			}

		}
		return result;
	}

}
