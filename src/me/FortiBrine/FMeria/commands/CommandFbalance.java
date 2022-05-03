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

import me.FortiBrine.FMeria.EconomyManager;
import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings("unused")
public class CommandFbalance implements CommandExecutor {
	
	private FMeria plugin;
	private File messages;
	public CommandFbalance(FMeria plugin) {
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
		if (args.length<3) {
			p.sendMessage("§7Баланс:§c %money%".replace("%money%", ""+plugin.getConfig().getInt(faction+".money")));
			p.sendMessage("/fbalance money [pay/take] [Количество]");
			return true;
		}
		int rank = plugin.getConfig().getInt(faction+".users."+p.getName());
		if (rank<9) {
			p.sendMessage(messageConfig.getString("message.nonRank"));
			return true;
		}
		if (args[0].equals("money")) {
			if (args[1].equals("pay")) {
				int amount;
				try {
					amount = Integer.parseInt(args[2]);
				} catch (NumberFormatException nfe) {
					p.sendMessage("§4Ошибка:§c Введите число!");
					return true;
				}	
				
				boolean success = (boolean) EconomyManager.takeMoney(p, amount);
				if (success!=true) {
					p.sendMessage("§4Ошибка:§c У вас недостаточно денег!");
					return true;
				}
				int balance = plugin.getConfig().getInt(faction+".money");
				balance+=amount;
				plugin.getConfig().set(faction+".money", balance);
				plugin.saveConfig();
				plugin.reloadConfig();
				Set<String> players = new HashSet<String>();
				players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
				for (Player ps : Bukkit.getOnlinePlayers()) {
					if (players.contains(ps.getName())) {
						ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §fИгрок "+p.getName()+" пополнил баланс фракции на "+amount+"!");
					}
				}
				return true;
			}
			if (args[1].equals("take")) {
				int amount;
				try {
					amount = Integer.parseInt(args[2]);
				} catch (NumberFormatException nfe) {
					p.sendMessage("§4Ошибка:§c Введите число!");
					return true;
				}	
				
				int balance = plugin.getConfig().getInt(faction+".money");
				if (balance<amount) {
					p.sendMessage("§4Ошибка:§c На балансе фракции недостаточно денег!");
					return true;
				}
				balance-=amount;
				EconomyManager.giveMoney(p, amount);
				plugin.getConfig().set(faction+".money", balance);
				plugin.saveConfig();
				plugin.reloadConfig();
				Set<String> players = new HashSet<String>();
				players=plugin.getConfig().getConfigurationSection(faction+".users").getKeys(false);
				for (Player ps : Bukkit.getOnlinePlayers()) {
					if (players.contains(ps.getName())) {
						ps.sendMessage("§f"+plugin.getConfig().getString(faction+".name")+"§7 >>> §fИгрок "+p.getName()+" снял с баланс фракции "+amount+"!");
					}
				}
				return true;
			}
			p.sendMessage("§7Баланс:§c %money%".replace("%money%", ""+plugin.getConfig().getInt(faction+".money")));
			p.sendMessage("/fbalance money [pay/take] [Количество]");
			return true;
		}
		
		p.sendMessage("§7Баланс:§c %money%".replace("%money%", ""+plugin.getConfig().getInt(faction+".money")));
		p.sendMessage("/fbalance money [pay/take] [Количество]");
		return true;
	}

}
