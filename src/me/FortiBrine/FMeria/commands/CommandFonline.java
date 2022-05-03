package me.FortiBrine.FMeria.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandFonline implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFonline(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Âû íå èãðîê!");
			return true;
		}
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
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
		for (Player p1 : Bukkit.getOnlinePlayers()) {

			if (plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false).contains(p1.getName())) {
				String rank=plugin.getConfig().getString(faction+".ranks."+plugin.getConfig().getString(faction+".users."+p1.getName()));
				int vigs=plugin.getConfig().getInt(faction+".vigs."+p1.getName());
				
				String res = "§c["+rank+"] §7"+p1.getName()+"§f ["+vigs+"/3]";
				p.sendMessage(res);
			}
		}

		return true;
	}

}
