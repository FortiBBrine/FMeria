package me.FortiBrine.FMeria.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.FortiBrine.FMeria.FMeria;

@SuppressWarnings("unused")
public class LawUtil {

	private File messages;
	private File lawFile;
	private FMeria plugin;
	public LawUtil(File lawFile, FMeria plugin) {
		this.lawFile = lawFile;
		this.plugin = plugin;
		this.messages = new File(plugin.getDataFolder()+File.separator+"messages.yml");
	}
	
	public void set(String path, Object o) {
		YamlConfiguration lawYaml = YamlConfiguration.loadConfiguration(this.lawFile);
		lawYaml.set(path, o);
		try {
			lawYaml.save(lawFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getInt(String path) {
		YamlConfiguration lawYaml = YamlConfiguration.loadConfiguration(this.lawFile);
		int result = lawYaml.getInt(path, 0);
		return result;
	}
	
	public String getString(String path) {
		YamlConfiguration lawYaml = YamlConfiguration.loadConfiguration(this.lawFile);
		String result = lawYaml.getString(path, "");
		return result;
	}
	
	public ConfigurationSection getConfigurationSection(String path) {
		YamlConfiguration lawYaml = YamlConfiguration.loadConfiguration(this.lawFile);
		return lawYaml.getConfigurationSection(path);
	}
	
	public Set<String> getKeys() {
		YamlConfiguration lawYaml = YamlConfiguration.loadConfiguration(this.lawFile);
		return lawYaml.getKeys(false);
	}

	public Set<String> getKeys(ConfigurationSection arg0) {
		if (arg0 != null) return arg0.getKeys(false);
		return new HashSet<String>();
	}

	public ItemStack generateCategory(int category) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
		
		ItemStack item = new ItemStack(Material.NAME_TAG);
		ItemMeta meta = item.getItemMeta();
		
		String message = this.getString(category+".name");
		String title = messageConfig.getString("message.categoryItem.title").replace("%categoryName", message).replace("%category", ""+category);
		List<String> lore = messageConfig.getStringList("message.categoryItem.lore");
		
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, lore.get(i).replace("%categoryName", message).replace("%category", ""+category));
		}
		
		meta.setLore(lore);
		meta.setDisplayName(title);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack generateLaw(int category, int law) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(this.messages);
	
		ItemStack item = new ItemStack(Material.NAME_TAG);
		ItemMeta meta = item.getItemMeta();
		
		String categoryName = this.getString(category+".name");
		String lawName = this.getString(category+".list."+law+".name");
		int lawTime = this.getInt(category+".list."+law+".time");
		
		String title = messageConfig.getString("message.lawItem.title");
		List<String> lore = messageConfig.getStringList("message.lawItem.lore");
		
		title = title.replace("%lawTime", ""+lawTime);
		title = title.replace("%lawName", lawName);
		title = title.replace("%categoryName", categoryName);
		title = title.replace("%law", ""+law);
		title = title.replace("%category", ""+category);
		
		for (int i = 0; i < lore.size(); i++) {
			String loreString = lore.get(i);
			loreString = loreString.replace("%lawTime", ""+lawTime);
			loreString = loreString.replace("%lawName", lawName);
			loreString = loreString.replace("%categoryName", categoryName);
			loreString = loreString.replace("%law", ""+law);
			loreString = loreString.replace("%category", ""+category);
			lore.set(i, loreString);
		}
		
		meta.setDisplayName(title);
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
}
