package me.FortiBrine.FMeria.commands;

import java.io.File;
import java.util.ArrayList;
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

public class CommandFonline implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFonline(FMeria plugin) {
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

		Set<String> keys = plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		List<Player> players = new ArrayList<>();
		List<String> ranks = plugin.getConfig().getStringList(faction+".ranks");
		
		for (Player player : Bukkit.getOnlinePlayers()) {

			if (keys.contains(player.getName())) {
				players.add(player);
			}
		}
		
		for (String s : messageConfig.getStringList("message.fonlineCmd.print1")) {
			s = s.replace("%size", ""+players.size());
			p.sendMessage(s);
		}
		
		for (Player player : players) {
			for (String s : messageConfig.getStringList("message.fonlineCmd.print2")) {
				int rank = plugin.getConfig().getInt(faction+".users."+player.getName());
				int vigs = plugin.getConfig().getInt(faction+".vigs."+player.getName());
				s = s.replace("%faction", plugin.getConfig().getString(faction+".name"));
				s = s.replace("%rank", ranks.get(rank - 1));
				s = s.replace("%user", player.getName());
				s = s.replace("%player", player.getDisplayName());
				s = s.replace("%vigs", ""+vigs);
				
				p.sendMessage(s);
			}
		}
		
		for (String s : messageConfig.getStringList("message.fonlineCmd.print3")) {
			s = s.replace("%size", ""+players.size());
			p.sendMessage(s);
		}

		return true;
	}

}
