package me.FortiBrine.FMeria.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandSetrank implements CommandExecutor {
	
	private FMeria plugin;
	public CommandSetrank(FMeria plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (args.length<3) {
			s.sendMessage("§7/setrank [Никнейм] [Фракция] [Ранг] (Отладка)");
			return true;
		}
		if (!s.hasPermission("fmeria.reload")) {
			s.sendMessage("§4Ошибка:§f Вы не разработчик!");
			return true;
		}
		if (!plugin.getConfig().getKeys(false).contains(args[1])) {
			s.sendMessage("§4Ошибка:§f Такой фракции не существует!");
			return true;
		}
		int newrank;
		try {
			newrank = Integer.parseInt(args[2]);
		} catch (NumberFormatException nfe) {
			s.sendMessage("§4Ошибка:§f Введите число!");
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
		s.sendMessage("§4Информация:§f Успешно!");
		return true;
	}

}
