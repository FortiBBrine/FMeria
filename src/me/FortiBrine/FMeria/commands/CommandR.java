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

public class CommandR implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandR(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Вы не игрок!");
			return true;
		}
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		Player p = (Player) sender;
		if (args.length<1) {
			p.sendMessage("§7/r [сообщение]");
			return true;
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
		String msg = "";
		for (String i : args) msg+=(i+" ");
		msg.trim();
		YamlConfiguration msgs = YamlConfiguration.loadConfiguration(plugin.messages);
		String message = msgs.getString("message.r");
		message = message.replace("%faction%", plugin.getConfig().getString(faction+".name"));
		message = message.replace("%rank%", plugin.getConfig().getString(faction+".ranks."+rank));
		message = message.replace("%player%", p.getDisplayName());
		message = message.replace("%msg%", msg);
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
