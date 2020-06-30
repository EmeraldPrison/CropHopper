package com.ascendpvp.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Helpers {

	public String cc(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public List<String> cc(List<String> s) {
		List<String> colored = new ArrayList<>();
		for(String l : s){
			colored.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return colored;
	}
	
	public ItemStack nameItemLore(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> lores = new ArrayList<>(lore);
		meta.setLore(lores);
		item.setItemMeta(meta);
		return item;
	}
}
