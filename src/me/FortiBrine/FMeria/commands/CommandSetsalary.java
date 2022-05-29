package me.FortiBrine.FMeria.commands;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandSetsalary implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandSetsalary(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);

		if (!(sender instanceof Player)) {
			sender.sendMessage(messageConfig.getString("message.notPlayer"));
			return true;
		}
		Player p = (Player) sender;
		if (args.length<2) {
			return false;
		}
		
		String faction = null;
		for (String css : plugin.getConfig().getKeys(false)) {
			ConfigurationSection cs = plugin.getConfig().getConfigurationSection(css);
			ConfigurationSection cs2 = cs.getConfigurationSection("users");

			if (cs2.getKeys(false).contains(p.getName())) {

				faction = css;
				break;
			}
		}
		if (faction==null) {
			p.sendMessage(messageConfig.getString("message.nonFaction"));
			return true;
		}
		int rank = plugin.getConfig().getInt(faction+".users."+p.getName());
		List<String> ranks = plugin.getConfig().getStringList(faction+".ranks");
		int needRank = ranks.size() - 1;
		if (rank<needRank) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		int salary;
		try {
			salary = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			p.sendMessage(messageConfig.getString("message.NumberFormatException"));
			return true;
		}
		int rang;
		try {
			rang = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			p.sendMessage(messageConfig.getString("message.NumberFormatException"));
			return true;
		}
		if (rang>ranks.size() || rang<1) {
			p.sendMessage(messageConfig.getString("message.rankLimit"));
			return true;
		}
		plugin.getConfig().set(faction+".salary."+rang, salary);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		String message = messageConfig.getString("message.setsalary");
		
		message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
		message = message.replace("%rank", ranks.get(rang - 1));
		message = message.replace("%user", p.getName());
		message = message.replace("%player", p.getDisplayName());
		message = message.replace("%salary", ""+salary);
		
		Set<String> players = new HashSet<String>();
		players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (players.contains(ps.getName())) {
				ps.sendMessage(message);
			}
		}
		return true;
	}

}
