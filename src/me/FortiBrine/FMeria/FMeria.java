package me.FortiBrine.FMeria;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.FortiBrine.FMeria.placeholderapi.Expansion;
import me.FortiBrine.FMeria.utils.LawUtil;
import me.FortiBrine.FMeria.utils.loadUtil;

import java.io.File;

public class FMeria extends JavaPlugin {
	
	public Map<Player, Long> invtime = new HashMap<Player, Long>();
	public Map<Player, String> invfrac = new HashMap<Player, String>();
	public File messages = new File(getDataFolder()+File.separator+"messages.yml");;
	public File lawFile = new File(getDataFolder()+File.separator+"law.yml");
	
	public LawUtil lawUtil = new LawUtil(lawFile, this);
	public Inventory lawInv;
	public Map<Integer, Inventory> lawCategoryInv;
	public Map<Player, Inventory> lawInventory = new HashMap<Player, Inventory>();
	public Map<ItemStack, Inventory> lawClickItem;
	public Map<Player, Integer> lawCmd = new HashMap<Player, Integer>();
	
	public void onEnable() {
		File config = new File(getDataFolder()+File.separator+"config.yml");
		if (!config.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		if (!messages.exists()) saveResource("messages.yml", false);
		if (!lawFile.exists()) saveResource("law.yml", false);
		YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(this.messages);
				
		EconomyManager.init();
		
		loadUtil.loadCommands(this);
		loadUtil.loadListeners(this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(
		        this,
		        () -> giveSalary(),
		        0L,  messagesConfig.getInt("value.time")*20L
		);
	
		getLogger().info("Plugin created by RP Studio!");
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Expansion(this).register();
        }
      
        loadLawInventory();
	}
	
	public void loadLawInventory() {
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(this.messages);
		lawInv = Bukkit.createInventory(null, 54, messages.getString("message.lawTitle"));
		lawCategoryInv = new HashMap<Integer, Inventory>();
		lawClickItem = new HashMap<ItemStack, Inventory>();
		Set<String> categoryList = this.lawUtil.getKeys();
		
		for (String category : categoryList) {
			int Category = Integer.parseInt(category);
			ItemStack categoryItem = this.lawUtil.generateCategory(Category);
			
			lawInv.addItem(categoryItem);
			
			Inventory inv = Bukkit.createInventory(null, 54, messages.getString("message.lawTitle"));
			ConfigurationSection categorySection = this.lawUtil.getConfigurationSection(category+".list");
			Set<String> lawList = this.lawUtil.getKeys(categorySection);
			
			for (String law : lawList) {
				int Law = Integer.parseInt(law);
				ItemStack lawItem = this.lawUtil.generateLaw(Category, Law);
				
				inv.addItem(lawItem);
			}
			lawCategoryInv.put(Category, inv);
			lawClickItem.put(categoryItem, inv);
		}

	}
	
	public void giveSalary() {
		YamlConfiguration messages = YamlConfiguration.loadConfiguration(this.messages);
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			String faction = null;
			for (String css : getConfig().getKeys(false)) {
				ConfigurationSection cs = getConfig().getConfigurationSection(css);
				ConfigurationSection cs2 = cs.getConfigurationSection("users");

				if (cs2.getKeys(false).contains(p.getName())) {

					faction = css;
					break;
				}
			}
			if (faction==null) {
				continue;
			}
			int salary = getConfig().getInt(faction+".salary."+getConfig().getString(faction+".users."+p.getName()));
			if (EconomyManager.giveMoney(p, salary)) {
				for (String i : messages.getStringList("message.payday")) {
					i = i.replace("%salary%", ""+salary);
					p.sendMessage(i);
				}
			}

		}
	}
	
}
