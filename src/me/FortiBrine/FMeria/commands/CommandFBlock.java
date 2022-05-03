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

public class CommandFBlock implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFBlock(FMeria plugin) {
		this.plugin = plugin;
		messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("�� �� �����!");
			return true;
		}
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		Player p = (Player) sender;
		if (args.length!=1) {
			p.sendMessage("�7/fblock [�������]");
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
			p.sendMessage(messageConfig.getString("messsage.notOnline"));
			return true;
		}
		Player p2 = Bukkit.getPlayer(args[0]);
		int rank2 = plugin.getConfig().getInt(faction+".users."+p2.getName());
		if (rank2>=rank) {
			p.sendMessage("�c�� �� ������ �������� � �� ������� ������!");
			return true;
		}
		
		plugin.getConfig().set(faction+".fblock."+p2.getName(), 1);
		plugin.getConfig().set(faction+".users."+p2.getName(), null);
		plugin.saveConfig();
		plugin.reloadConfig();
		
		
		Set<String> players = new HashSet<String>();
		players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
		for (Player ps : Bukkit.getOnlinePlayers()) {
			if (players.contains(ps.getName())) {
				ps.sendMessage("�f"+plugin.getConfig().getString(faction+".name")+"�7 >>> �f"+p.getName()+" ������� � �� ������� "+p2.getName()+"!");
			}
		}

		return true;
	}

}