package com.ascendpvp.events;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import com.ascendpvp.CropHopperMain;

import java.io.IOException;


public class CactusGrow implements Listener {

	CropHopperMain plugin;
	public CactusGrow(CropHopperMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onCactusGrow(ItemSpawnEvent e) {

		//Basic checks
	//	if(e.getEntity().getItemStack().getType() != Material.CACTUS) return;
		if(e.getEntity().getCustomName() != null) return;

		Plot plot = CropHopperMain.plotAPI.getPlot(e.getLocation());
		if(plot == null){
			return;
		}
		int plotCoordX = plot.getId().x;
		int plotCoordY = plot.getId().y;
		String plotID = plotCoordX + "," + plotCoordY;

		//Get spawn chunk of cactus and apply to a String
		int spawnX = e.getEntity().getLocation().getChunk().getX();
		int spawnZ = e.getEntity().getLocation().getChunk().getZ();
		String hopperSave = String.valueOf(spawnX + String.valueOf(spawnZ));
		//Determine whether or not there is a hopper in the chunk the cactus spawned in
		if(plugin.cfg.getString(plotID + "." + "hopperlocs." + hopperSave) != null) {
			//Cancel the ItemSpawnEvent and add a cactus directly into the hopper instead
			e.setCancelled(true);
			int cactusAmount = e.getEntity().getItemStack().getAmount();

			if(plugin.cfg.getString(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to.world") == null) {
				int hopperX = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "x");
				int hopperY = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "y");
				int hopperZ = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "z");
				String hopperWorld = plugin.cfg.getString(plotID + "." + "hopperlocs." + hopperSave + "." + "world");
				Location hopperLoc = new Location(Bukkit.getWorld(hopperWorld), hopperX, hopperY, hopperZ);
				Hopper hopper = (Hopper) hopperLoc.getBlock().getState();
				hopper.getInventory().addItem(new ItemStack(e.getEntity().getItemStack().getType(), cactusAmount));
			}else {
				int chestX = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "x");
				int chestY = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "y");
				int chestZ = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to" + "." + "z");
				String chestWorld = plugin.cfg.getString(plotID + "." + "hopperlocs." + hopperSave + "." + "redirect-to.world");
				Location chestLoc = new Location(Bukkit.getWorld(chestWorld), chestX, chestY, chestZ);
				try {
					Chest chest = (Chest) chestLoc.getBlock().getState();
					chest.getInventory().addItem(new ItemStack(e.getEntity().getItemStack().getType(), cactusAmount));
				}catch(Exception notAChest){
					int hopperX = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "x");
					int hopperY = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "y");
					int hopperZ = plugin.cfg.getInt(plotID + "." + "hopperlocs." + hopperSave + "." + "z");
					String hopperWorld = plugin.cfg.getString(plotID + "." + "hopperlocs." + hopperSave + "." + "world");
					Location hopperLoc = new Location(Bukkit.getWorld(hopperWorld), hopperX, hopperY, hopperZ);
					Hopper hopper = (Hopper) hopperLoc.getBlock().getState();
					hopper.getInventory().addItem(new ItemStack(e.getEntity().getItemStack().getType(), cactusAmount));

					plugin.cfg.set(plotID + "." + "hopperlocs." + hopperSave + ".redirect-to", false);
					try {
						plugin.cfg.save(plugin.f);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
