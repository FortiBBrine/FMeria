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
public class CommandFhome implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFhome(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);

		if (!(sender instanceof Player)) {
			sender.sendMessage(messageConfig.getString("message.notPlayer"));
			return true;
		}
		Player p = (Player) sender;
		
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
		if (plugin.getConfig().getString(faction+".fhome.x")==null || plugin.getConfig().getString(faction+".fhome.y")==null || plugin.getConfig().getString(faction+".fhome.z")==null || plugin.getConfig().getString(faction+".fhome.world")==null) {
			p.sendMessage(messageConfig.getString("message.hasnotFhome"));
			return true;
		}
		String world = plugin.getConfig().getString(faction+".fhome.world");
		int x = plugin.getConfig().getInt(faction+".fhome.x");
		int y = plugin.getConfig().getInt(faction+".fhome.y");
		int z = plugin.getConfig().getInt(faction+".fhome.z");
		
		Location loc = p.getLocation();
		loc.setX(x);
		loc.setY(y);
		loc.setZ(z);
		loc.setWorld(Bukkit.getWorld(world));
		p.teleport(loc);
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(plugin.messages);
		p.sendMessage(messages.getString("message.fhome"));
		
		return true;
	}

}
