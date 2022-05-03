package me.FortiBrine.FMeria.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.FortiBrine.FMeria.FMeria;

public class LawInventoryListener implements Listener {

	private FMeria plugin;
	public LawInventoryListener(FMeria plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();

		if (plugin.lawCmd.containsKey(player)) {
			plugin.lawCmd.remove(player);
		}
		if (plugin.lawInventory.containsKey(player)) {
			plugin.lawInventory.remove(player);
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		if (plugin.lawCmd.containsKey(player)) {
			plugin.lawCmd.remove(player);
		}
		if (plugin.lawInventory.containsKey(player)) {
			plugin.lawInventory.remove(player);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (!plugin.lawInventory.containsKey(p)) return;
		if (e.getClickedInventory()==null) return;
		if (!e.getClickedInventory().equals(plugin.lawInventory.get(p))) return;
		e.setCancelled(true);
		if (!(e.getCursor() == null || e.getCursor().getType()==Material.AIR)) return;
		
		int type = plugin.lawCmd.get(p);
		
		if (type==0) {
			ItemStack item = e.getCurrentItem();
			
			Inventory inv = plugin.lawClickItem.get(item);
			p.closeInventory();
			p.openInventory(inv);
			plugin.lawCmd.put(p, 1);
			plugin.lawInventory.put(p, inv);
			
			return;
		}
		
	}
	
}
