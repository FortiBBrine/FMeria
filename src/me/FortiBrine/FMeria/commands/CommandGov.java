package me.FortiBrine.FMeria.commands;



import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;

public class CommandGov implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandGov(FMeria plugin) {
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
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
			p.sendMessage("§7/gov [сообщение]");
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
			p.sendMessage("§cВаш ранг слишком маленький!");
			return true;
		}
		boolean gov = plugin.getConfig().getBoolean(faction+".gov");
		if (gov!=true) {
			p.sendMessage("§cДоступно только гос.сотрудникам!");
			return true;
		}
		String rankname = plugin.getConfig().getString(faction+".ranks."+plugin.getConfig().getString(faction+".users."+p.getName()));
		String msg = "";
		for (String i : args) msg+=(i+" ");
		msg = msg.trim();
		
		List<String> messages = messageConfig.getStringList("message.gov");
		
		for (int i = 0; i < messages.size(); i++) {
			messages.set(i, messages.get(i).replace("%faction", plugin.getConfig().getString(faction+".name")).replace("%rank", rankname).replace("%player", p.getDisplayName()).replace("%user", p.getName()).replace("%message", msg));
		}
		
		for (Player ps : Bukkit.getOnlinePlayers()) {
			for (String s : messages) {
				ps.sendMessage(s);
			}
		}

		return true;
	}

}
