package me.FortiBrine.FMeria.commands;



import java.io.File;
import java.util.ArrayList;
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

public class CommandSetfname implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandSetfname(FMeria plugin) {
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
		
		if (args[0].equals("name")) {
			List<String> aargs = new ArrayList<String>();
			for (String s : args) aargs.add(s);
			aargs.remove(0);
			String name="";
			for (String nm : aargs) name+= (nm+" ");
			name = name.trim();
			if (name.length()>15) {
				p.sendMessage(messageConfig.getString("message.sizeFactionNameLimit"));
				return true;
			}
			plugin.getConfig().set(faction+".name", name);
			plugin.saveConfig();
			plugin.reloadConfig();
			p.sendMessage(messageConfig.getString("message.changeFactionName").replace("%faction", name));
			
			return true;
		}
		int rang = 1;
		try {
			rang = Integer.parseInt(args[0]);	
		} catch (NumberFormatException nfe) {
			p.sendMessage(messageConfig.getString("message.NumberFormatException"));
			return true;
		}
		if (rang<1 || rang>ranks.size()) {
			p.sendMessage(messageConfig.getString("message.rankLimit"));
			return true;
		}
		
		List<String> aargs = new ArrayList<String>();
		for (String s : args) aargs.add(s);
		aargs.remove(0);
		String name="";
		for (String nm : aargs) name+= (nm+" ");
		name = name.trim();
		if (name.length()>15) {
			p.sendMessage(messageConfig.getString("message.sizeRankNameLimit"));
			return true;
		}
		String oldrankname = ranks.get(rang - 1);
		String newrankname = name;
		ranks.set(rang - 1, newrankname);
		plugin.getConfig().set(faction+".ranks", ranks);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		String message = messageConfig.getString("message.сhangeRankName");
		
		message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
		message = message.replace("%user", p.getName());
		message = message.replace("%player", p.getDisplayName());		
		message = message.replace("%oldRank", oldrankname);
		message = message.replace("%newRank", newrankname);
		
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
