package me.FortiBrine.FMeria.commands;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings("unused")
public class CommandSetfhome implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandSetfhome(FMeria plugin) {
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
			p.sendMessage("§7/setfhome [yes]");
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
		if (rank<11) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		if (!args[0].equals("yes")) {
			p.sendMessage("§7/setfhome [yes]");
			return true;
		}
		Location loc = p.getLocation();
		World w = loc.getWorld();
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		plugin.getConfig().set(faction+".fhome.x", x);
		plugin.getConfig().set(faction+".fhome.world", w.getName());
		plugin.getConfig().set(faction+".fhome.y", y);
		plugin.getConfig().set(faction+".fhome.z", z);
		plugin.saveConfig();
		plugin.reloadConfig();
		p.sendMessage("§4Информация:§fУспешно!");
		
		return true;
	}

}
