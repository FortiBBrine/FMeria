package me.FortiBrine.FMeria.placeholderapi;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.FortiBrine.FMeria.FMeria;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

@SuppressWarnings("unused")
public class Expansion extends PlaceholderExpansion {

    private FMeria plugin;

    public Expansion(FMeria plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }


    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "fmeria";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }
        System.out.println(identifier);
        // %fmeria_rank%
        if(identifier.equals("rank")){
    		String faction = null;
    		for (String css : plugin.getConfig().getKeys(false)) {
    			ConfigurationSection cs = plugin.getConfig().getConfigurationSection(css);
    			ConfigurationSection cs2 = cs.getConfigurationSection("users");

    			if (cs2.getKeys(false).contains(player.getName())) {

    				faction = css;
    				break;
    			}
    		}
    		if (faction==null) {
    			YamlConfiguration msgs = YamlConfiguration.loadConfiguration(plugin.messages);
    			return msgs.getString("message.noneRank");
    		}
    		List<String> ranks = plugin.getConfig().getStringList(faction+".ranks");
    		int rank = plugin.getConfig().getInt(faction+".users."+player.getName());
    		
    		//return plugin.getConfig().getString(faction+".ranks."+plugin.getConfig().getString(faction+".users."+player.getName()));
    		return ranks.get(rank - 1);
        }
        
        if (identifier.equals("name")) {
    		String faction = null;
    		for (String css : plugin.getConfig().getKeys(false)) {
    			ConfigurationSection cs = plugin.getConfig().getConfigurationSection(css);
    			ConfigurationSection cs2 = cs.getConfigurationSection("users");

    			if (cs2.getKeys(false).contains(player.getName())) {

    				faction = css;
    				break;
    			}
    		}
    		if (faction == null) {
    			YamlConfiguration msgs = YamlConfiguration.loadConfiguration(plugin.messages);
    			return msgs.getString("message.noneName");
    		}
    		return plugin.getConfig().getString(faction+".name");
    		
        }
        return null;
    }
}