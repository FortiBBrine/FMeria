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
		if (!(sender instanceof Player)) {
			sender.sendMessage("Вы не игрок!");
			return true;
		}
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		Player p = (Player) sender;
		if (args.length<2) {
			p.sendMessage("§7/setfname [Ранг/name] [Название]");
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
		
		if (args[0].equals("name")) {
			List<String> aargs = new ArrayList<String>();
			for (String s : args) aargs.add(s);
			aargs.remove(0);
			String name="";
			for (String nm : aargs) name+= (nm+" ");
			name = name.trim();
			if (name.length()>15) {
				p.sendMessage("§4Ошибка:§c Название фракции должно не иметь больше 15 символов.");
				return true;
			}
			plugin.getConfig().set(faction+".name", name);
			plugin.saveConfig();
			plugin.reloadConfig();
			p.sendMessage("§cФракция теперь имеет название - "+name);
			
			return true;
		}
		int rang = 1;
		try {
			rang = Integer.parseInt(args[0]);	
		} catch (NumberFormatException nfe) {
			p.sendMessage("§4Ошибка: §cВведите число!");
			return true;
		}
		if (rang<1 || rang>11) {
			p.sendMessage("§4Ошибка: §cВведите число!");
			return true;
		}
		
		List<String> aargs = new ArrayList<String>();
		for (String s : args) aargs.add(s);
		aargs.remove(0);
		String name="";
		for (String nm : aargs) name+= (nm+" ");
		name = name.trim();
		if (name.length()>15) {
			p.sendMessage("§4Ошибка:§c Название роли должно не иметь больше 15 символов.");
			return true;
		}
		String oldrankname = plugin.getConfig().getString(faction+".ranks."+rang);
		String newrankname = name;
		plugin.getConfig().set(faction+".ranks."+rang, newrankname);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		Set<String> players = new HashSet<String>();
		players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (players.contains(ps.getName())) {
				ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §f"+p.getName()+" сменил название роли "+oldrankname+" на "+newrankname+" !");
			}
		}
		return true;
	}

}
