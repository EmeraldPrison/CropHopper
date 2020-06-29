package com.ascendpvp.events;

import java.io.IOException;
import java.util.*;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ascendpvp.CropHopperMain;
import com.ascendpvp.utils.Helpers;

public class HopperPlace implements Listener {

	CropHopperMain plugin;
	public HopperPlace(CropHopperMain plugin){
		this.plugin = plugin;
	}
	Helpers help = new Helpers();

	public static Map<UUID, List<Block>> waitingHoppers = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHopperRedirect(PlayerInteractEvent e){
		if(e.isCancelled()) return;

		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		Block b = e.getClickedBlock();
		if(waitingHoppers.get(uuid) == null){
			return;
		}

		if(b == null || b.getType() != Material.CHEST || b.getType() != Material.TRAPPED_CHEST){
			waitingHoppers.get(uuid).clear();
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_redirect_dont")));
			return;
		}

		Plot plot = CropHopperMain.plotAPI.getPlot(b.getLocation());
		if(plot != null && plot.isAdded(p.getUniqueId())){

			int hopperX = b.getX();
			int hopperY = b.getY();
			int hopperZ = b.getZ();

			int plotCoordX = plot.getId().x;
			int plotCoordY = plot.getId().y;
			String plotID = plotCoordX + "," + plotCoordY;

			// check if they can build on the plot of the clicked chest

			for(Block hopper : waitingHoppers.get(uuid)){
				int chunkX = hopper.getChunk().getX();
				int chunkZ = hopper.getChunk().getZ();
				String hopperSave = String.valueOf(chunkX) + String.valueOf(chunkZ);

				//Set and save values in .yml
				plugin.cfg.set(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "x", hopperX);
				plugin.cfg.set(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "y", hopperY);
				plugin.cfg.set(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "z", hopperZ);
				plugin.cfg.set(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "world", b.getWorld().getName());
			}

			try {
				plugin.cfg.save(plugin.f);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			waitingHoppers.get(uuid).clear();

			p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_redirect_success")));
		}else{
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_redirect_fail")));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHopperPlace(BlockPlaceEvent e) {

		//Basic checks
		if(e.isCancelled()) return;
		if(e.getItemInHand() == null) return;
		ItemStack blockPlaced = e.getItemInHand();
		Player p = e.getPlayer();
		if(blockPlaced.getType() != Material.HOPPER) return;
		if(!blockPlaced.hasItemMeta() || !blockPlaced.getItemMeta().hasDisplayName()) return;
		if(!blockPlaced.getItemMeta().getDisplayName().equals(help.cc(plugin.getConfig().getString("hopper_name")))) return;
		if(!blockPlaced.getItemMeta().getLore().contains(help.cc(plugin.getConfig().getString("hopper_lore")))) return;

		int chunkX = e.getBlockPlaced().getChunk().getX();
		int chunkZ = e.getBlockPlaced().getChunk().getZ();
		int hopperX = e.getBlockPlaced().getX();
		int hopperY = e.getBlockPlaced().getY();
		int hopperZ = e.getBlockPlaced().getZ();
		String hopperSave = String.valueOf(chunkX) + String.valueOf(chunkZ);

		Plot plot = CropHopperMain.plotAPI.getPlot(e.getBlockPlaced().getLocation());
		if(plot != null && plot.isAdded(p.getUniqueId())) {

			int plotCoordX = plot.getId().x;
			int plotCoordY = plot.getId().y;
			String plotID = plotCoordX + "," + plotCoordY;

			if (plugin.cfg.getString(plotID + ".hopperlocs." + hopperSave) != null) {
				e.setCancelled(true);
				p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_already_in_chunk")));
				return;
			}

			waitingHoppers.putIfAbsent(p.getUniqueId(), new ArrayList<>());
			waitingHoppers.get(p.getUniqueId()).add(e.getBlockPlaced());

			//Set and save values in .yml
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "chunkx", chunkX);
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "chunkz", chunkZ);
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "x", hopperX);
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "y", hopperY);
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "z", hopperZ);
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "world", e.getBlockPlaced().getWorld().getName());
			plugin.cfg.set(plotID + ".hopperlocs." + hopperSave + "." + "redirect-to", false);
			try {
				plugin.cfg.save(plugin.f);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_place_success")));
		}else{
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.hopper_place_fail")));
		}
	}
}
