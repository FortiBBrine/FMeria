package me.FortiBrine.FMeria.commands;



import java.io.File;
import java.util.HashSet;
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

public class CommandInvite implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandInvite(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Вы не игрок!");
			return true;
		}
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		Player p = (Player) sender;
		if (args.length!=1) {
			p.sendMessage("§7/invite [Никнейм]");
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
		if (rank<9) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		OfflinePlayer op2 = Bukkit.getOfflinePlayer(args[0]);
		if (!op2.isOnline()) {
			p.sendMessage(messageConfig.getString("message.notOnline"));
			return true;
		}
		Player p2 = Bukkit.getPlayer(args[0]);
		if (!((int) p.getLocation().distance(p2.getLocation()) < 5)) {
			p.sendMessage(messageConfig.getString("message.distance"));
			return true;
		}
		
		if ((plugin.getConfig().getConfigurationSection(faction+".fblock")!=null) && (plugin.getConfig().getConfigurationSection(faction+".fblock").getKeys(false).contains(p2.getName()))) {
			p.sendMessage("§cИгрок в чс вашей фракции!");
			return true;
		} 
		String faction2 = null;
		for (String css : plugin.getConfig().getKeys(false)) {
			ConfigurationSection cs = plugin.getConfig().getConfigurationSection(css);
			ConfigurationSection cs2 = cs.getConfigurationSection("users");

			if (cs2.getKeys(false).contains(p2.getName())) {

				faction2 = css;
				break;
			}
		}
		if (faction2!=null) {
			p.sendMessage("§cИгрок уже состоит во фракции!");
			return true;
		}
		Set<String> players = new HashSet<String>();
		players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (players.contains(ps.getName())) {
				ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §fИгрок "+p.getName()+" пригласил "+p2.getName()+" во фракцию!");
			}
		}
		p2.sendMessage("§7Вас пригласили в фракцию "+plugin.getConfig().getString(faction+".name")+"!§f У вас 60 секунд чтобы принять! /inv accept");
		plugin.invtime.put(p2, System.currentTimeMillis()+(60*1000));
		plugin.invfrac.put(p2, faction);
		return true;
	}

}
