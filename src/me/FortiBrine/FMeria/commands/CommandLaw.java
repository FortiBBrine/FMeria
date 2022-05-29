package me.FortiBrine.FMeria.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.FortiBrine.FMeria.FMeria;

public class CommandLaw implements CommandExecutor {

	private FMeria plugin;
	private List<String> subcommands = new ArrayList<String>();
	private File messages;
	public CommandLaw(FMeria plugin) {
		this.plugin = plugin;
		this.subcommands.add("set");
		this.subcommands.add("setcategory");
		this.subcommands.add("delcategory");
		this.subcommands.add("del");
		this.subcommands.add("settime");
		this.subcommands.add("list");
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
		
		if (args.length<1 || (!this.subcommands.contains(args[0]))) {
			for (String s : messageConfig.getStringList("message.lawUsage")) {
				p.sendMessage(s);
			}
			return true;
		}
		if (args[0].equals("list")) {
			Inventory inv = plugin.lawInv;
			p.closeInventory();
			p.openInventory(inv);
				
			plugin.lawInventory.put(p, inv);
			plugin.lawCmd.put(p, 0);
				
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
		if (plugin.getConfig().getBoolean(faction+".meria", false)==false) {
			p.sendMessage(messageConfig.getString("message.nonMeriaFaction"));
			return true;
		}
		
		int rank = plugin.getConfig().getInt(faction+".users."+p.getName());
		int needRank = plugin.getConfig().getInt(faction+".meriaRank");
		if (rank < needRank) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		
		if (args[0].equals("setcategory")) {
			if (args.length < 3) {
				for (String s : messageConfig.getStringList("message.lawUsage")) {
					p.sendMessage(s);
				}
				return true;
			}

			int ID = 0;
			try {
				ID = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			List<String> argsArray = new ArrayList<String>();
			for (String s : args) {
				argsArray.add(s);
			}
			argsArray.remove(0);
			argsArray.remove(0);
			String msg = "";
			
			for (String s : argsArray) {
				msg += s + " ";
			}
			msg = msg.trim();
			plugin.lawUtil.set(ID+".name", msg);
			p.sendMessage(messageConfig.getString("message.successChangeCategory"));
			plugin.loadLawInventory();
			return true;
		} else if (args[0].equals("delcategory")) {
			if (args.length < 2) {
				for (String s : messageConfig.getStringList("message.lawUsage")) {
					p.sendMessage(s);
				}
				return true;
			}

			int ID = 0;
			try {
				ID = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			
			plugin.lawUtil.set(""+ID, null);
			p.sendMessage(messageConfig.getString("message.successDeleteCategory"));
			plugin.loadLawInventory();
			return true;
		} else if (args[0].equals("set")) {
			if (args.length < 4) {
				for (String s : messageConfig.getStringList("message.lawUsage")) {
					p.sendMessage(s);
				}
				return true;
			}
			int IDc = 0;
			try {
				IDc = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			
			if (!plugin.lawUtil.getKeys().contains(args[1])) {
				p.sendMessage(messageConfig.getString("message.dontFindCategory"));
				return true;
			}
			
			int IDlaw = 0;
			try {
				IDlaw = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			List<String> argsArray = new ArrayList<String>();
			for (String s : args) {
				argsArray.add(s);
			}
			argsArray.remove(0);
			argsArray.remove(0);
			argsArray.remove(0);
			String msg = "";
			
			for (String s : argsArray) {
				msg += s + " ";
			}
			msg = msg.trim();
			
			plugin.lawUtil.set(IDc+".list."+IDlaw+".name", msg);
			p.sendMessage(messageConfig.getString("message.successSetLawName"));
			plugin.loadLawInventory();
			return true;
		} else if (args[0].equals("del")) {
			if (args.length < 3) {
				for (String s : messageConfig.getStringList("message.lawUsage")) {
					p.sendMessage(s);
				}
				return true;
			}
			int IDc = 0;
			try {
				IDc = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			
			if (!plugin.lawUtil.getKeys().contains(args[1])) {
				p.sendMessage(messageConfig.getString("message.dontFindCategory"));
				return true;
			}
			
			int IDlaw = 0;
			try {
				IDlaw = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			
			plugin.lawUtil.set(IDc+".list."+IDlaw, null);
			p.sendMessage(messageConfig.getString("message.successDelLawName"));
			plugin.loadLawInventory();
			return true;
		} else if (args[0].equals("settime")) {
			if (args.length < 4) {
				for (String s : messageConfig.getStringList("message.lawUsage")) {
					p.sendMessage(s);
				}
				return true;
			}
			int IDc = 0;
			try {
				IDc = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			
			if (!plugin.lawUtil.getKeys().contains(args[1])) {
				p.sendMessage(messageConfig.getString("message.dontFindCategory"));
				return true;
			}
			
			int IDlaw = 0;
			try {
				IDlaw = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			int time = 0;
			try {
				time = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				p.sendMessage(messageConfig.getString("message.NumberFormatException"));
				return true;
			}
			plugin.lawUtil.set(IDc+".list."+IDlaw+".time", time);
			p.sendMessage(messageConfig.getString("message.successSetLawTime"));
			plugin.loadLawInventory();
			return true;
		}
		
		return true;
	}
}
