package me.FortiBrine.FMeria.commands;



import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandFunBlock implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFunBlock(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
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
		if (args.length!=1) {
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
		int needRank = ranks.size() - 2;
		if (rank<needRank) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		
		boolean offline = false;
		OfflinePlayer op2 = Bukkit.getOfflinePlayer(args[0]);
		offline = op2.isOnline() ? false : true;
		
		Player p2 = Bukkit.getPlayer(args[0]);
		
		plugin.getConfig().set(faction+".fblock."+(offline ? op2.getName() : p2.getName()), null);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		String message = messageConfig.getString("message.funblock");
		
		message = message.replace("%faction", plugin.getConfig().getString(faction+".name"));
		message = message.replace("%user1", p.getName());
		message = message.replace("%user2", offline ? op2.getName() : p2.getName());
		message = message.replace("%player1", p.getDisplayName());
		message = message.replace("%player2", offline ? op2.getName() : p2.getDisplayName());
		
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
