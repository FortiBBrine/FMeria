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

public class CommandSetsalary implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandSetsalary(FMeria plugin) {
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
		if (args.length<2) {
			p.sendMessage("§7/setsalary [Ранг] [Количество]");
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
		if (rank<10) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		int salary;
		try {
			salary = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			p.sendMessage("§4Ошибка:§c Введите число!");
			return true;
		}
		int rang;
		try {
			rang = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			p.sendMessage("§4Ошибка:§c Введите число!");
			return true;
		}
		if (rang>11 || rang<1) {
			p.sendMessage("§4Ошибка:§f Нет возможности отредактировать данный ранг!");
			return true;
		}
		plugin.getConfig().set(faction+".salary."+rang, salary);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		
		String rankname = plugin.getConfig().getString(faction+".ranks."+rang);
		Set<String> players = new HashSet<String>();
		players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (players.contains(ps.getName())) {
				ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §f"+p.getName()+" установил зарплату для "+rankname+" в размере: "+salary+"!");
			}
		}
		return true;
	}

}
