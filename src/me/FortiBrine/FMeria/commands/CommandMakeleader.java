package me.FortiBrine.FMeria.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandMakeleader implements CommandExecutor {

	private FMeria plugin;
	private File messages;
	public CommandMakeleader(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);

		if (!(sender instanceof Player)) {
			sender.sendMessage(messageConfig.getString("message.notPlayer"));
			return true;
		}
		Player p = (Player) sender;
		FileConfiguration config = plugin.getConfig();
		
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
		
		if (args.length < 1) {
			p.sendMessage(messageConfig.getString("message.makeleaderUsage"));
			return true;
		}
		
		String adminRankString = config.getString(faction+".adminRank");
		String leaderString = config.getString(faction+".leader");
		
		int adminRank;
		int leader;
		int rank = config.getInt(faction+".users."+p.getName());
		
		if (adminRankString == null) {
			p.sendMessage(messageConfig.getString("message.nonAdminRank"));
			return true;
		}
		if (leaderString == null) {
			p.sendMessage(messageConfig.getString("message.nonLeader"));
			return true;
		}
		
		adminRank = config.getInt(faction+".adminRank");
		leader = config.getInt(faction+".leader");
		
		if (rank < adminRank) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("-")) {			
			ConfigurationSection section = config.getConfigurationSection(faction+".users");
			for (String key : section.getKeys(false)) {
				int userRank = section.getInt(key);
				
				if (userRank == leader) {
					config.set(faction+".users."+key, null);
				}
			}
			
			plugin.saveConfig();
			plugin.reloadConfig();
			
			String message = messageConfig.getString("message.makeNonLeader")
					.replace("%faction", config.getString(faction+".name"));
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(message);
			}
			
			return true;
		}
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
		
		if (p.getName().equals(args[0])) {
			p.sendMessage(messageConfig.getString("message.playerAdminLeader"));
			return true;
		}
		
		if (!offlinePlayer.isOnline()) {
			p.sendMessage(messageConfig.getString("message.notOnline"));
			return true;
		}
		Player player = Bukkit.getPlayer(args[0]);
		String userFaction = null;
		for (String key : config.getKeys(false)) {
			ConfigurationSection cs = config.getConfigurationSection(key+".users");
			
			if (cs.getKeys(false).contains(args[0])) {
				userFaction = key;
				break;
			}
		}
		if (!faction.equals(userFaction)) {
			p.sendMessage(messageConfig.getString("message.factionNotEquals"));
			return true;
		}
		config.set(faction+".users."+args[0], leader);
		
		plugin.saveConfig();
		plugin.reloadConfig();
		
		String message = messageConfig.getString("message.makeLeader")
				.replace("%faction", config.getString(faction+".name"))
				.replace("%player", player.getDisplayName())
				.replace("%user", args[0]);
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			onlinePlayer.sendMessage(message);
		}
		
		return true;
	}
}
