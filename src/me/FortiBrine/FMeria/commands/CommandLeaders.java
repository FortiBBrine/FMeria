package me.FortiBrine.FMeria.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings({ "deprecation" })
public class CommandLeaders implements CommandExecutor {

	private FMeria plugin;
	private File messages;
	public CommandLeaders(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);

		Map<String, String> leaderNames = new HashMap<String, String>();
		
		for (String key : plugin.getConfig().getKeys(false)) {
			String leaderString = plugin.getConfig().getString(key+".leader");

			if (leaderString==null) {
				continue;
			}
			int leader = plugin.getConfig().getInt(key+".leader");
			
			ConfigurationSection section = plugin.getConfig().getConfigurationSection(key+".users");
			for (String Key : section.getKeys(false)) {
				int rank = section.getInt(Key);
				
				if (rank==leader) {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(Key);
					
					if (offlinePlayer.isOnline()) leaderNames.put(Key, key);
				}
			}
		}
		if (leaderNames.size() == 0) {
			List<String> message = messageConfig.getStringList("message.leadersCmd.noOnline");
			
			for (String msg : message) {
				s.sendMessage(msg);
			}
			return true;
		}
		
		int size = leaderNames.size();
		
		List<String> print1 = messageConfig.getStringList("message.leadersCmd.print1");
		for (int i = 0; i < print1.size(); i++) {
			print1.set(i, print1.get(i).replace("%size", ""+size));
		}
		List<String> print3 = messageConfig.getStringList("message.leadersCmd.print3");
		for (int i = 0; i < print3.size(); i++) {
			print3.set(i, print3.get(i).replace("%size", ""+size));
		}
		List<String> print2 = messageConfig.getStringList("message.leadersCmd.print2");
		List<String> result = new ArrayList<String>();
		
		for (String key : leaderNames.keySet()) {
			String faction = leaderNames.get(key);
			int rank = plugin.getConfig().getInt(faction+".users."+key);
			String rankname = plugin.getConfig().getString(faction+".ranks."+rank);
			
			Player player = Bukkit.getPlayer(key);
			
			for (String message : print2) {
				message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
				message = message.replace("%rank", rankname);
				message = message.replace("%user", player.getName());
				message = message.replace("%player", player.getDisplayName());
				result.add(message);
			}
		}
		
		for (String msg : print1) {
			s.sendMessage(msg);
		}
		for (String msg : result) {
			s.sendMessage(msg);
		}		
		for (String msg : print3) {
			s.sendMessage(msg);
		}

		return true;
	}
}
