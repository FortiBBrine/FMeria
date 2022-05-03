package me.FortiBrine.FMeria.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings("unused")
public class CommandInv implements CommandExecutor {
	
	private FMeria plugin;
	public CommandInv(FMeria plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Вы не игрок!");
			return true;
		}
		Player p = (Player) sender;
		if (args.length!=1) {
			p.sendMessage("§7/inv [accept/decline]");
			return true;
		}
		if ((!args[0].equals("accept")) && (!args[0].equals("decline"))) {
			p.sendMessage("§7/inv [accept/decline]");
			return true;
		}
		if ((!plugin.invfrac.containsKey(p)) || (!plugin.invtime.containsKey(p))) {
			p.sendMessage("§cУ вас нет приглашений во фракцию!");
			return true;
		}
		if (System.currentTimeMillis()>plugin.invtime.get(p)) {
			p.sendMessage("§cУ вас уже истекло приглашение во фракцию!");
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			return true;
		}
		if (args[0].equals("decline")) {
			String faction = plugin.invfrac.get(p);
			Set<String> players = new HashSet<String>();
			players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				if (players.contains(ps.getName())) {
					ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §fИгрок "+p.getName()+" отказался вступать во фракцию!");
				}
			}
			p.sendMessage("§cВы отказались от приглашения во фракцию!");
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			return true;
		}
		if (args[0].equals("accept")) {
			String faction = plugin.invfrac.get(p);
			Set<String> players = new HashSet<String>();
			players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
			for (Player ps : Bukkit.getOnlinePlayers()) {
				if (players.contains(ps.getName())) {
					ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §fИгрок "+p.getName()+" вступил во фракцию!");
				}
			}
			p.sendMessage("§cВы вступили во фракцию!");
			plugin.invfrac.remove(p);
			plugin.invtime.remove(p);
			plugin.getConfig().set(faction+".users."+p.getName(), 1);
			plugin.saveConfig();
			plugin.reloadConfig();
			return true;
		}
		
		return true;
	}

}
