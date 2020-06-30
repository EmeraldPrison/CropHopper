package com.ascendpvp;

import java.io.File;

import com.intellectualcrafters.plot.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.ascendpvp.cmds.GiveHopper;
import com.ascendpvp.events.CactusGrow;
import com.ascendpvp.events.HopperBreak;
import com.ascendpvp.events.HopperPlace;
import com.ascendpvp.events.ItemDrop;

public class CropHopperMain extends JavaPlugin {
	
	public FileConfiguration cfg;
	public File f;

	public static PlotAPI plotAPI;

	public void onEnable() {
		plotAPI = new PlotAPI();

		Bukkit.getPluginManager().registerEvents(new CactusGrow(this), this);
		Bukkit.getPluginManager().registerEvents(new HopperPlace(this), this);
		Bukkit.getPluginManager().registerEvents(new ItemDrop(this), this);
		Bukkit.getPluginManager().registerEvents(new HopperBreak(this), this);
		getCommand("crophopper").setExecutor(new GiveHopper(this));
		f = new File("plugins/EPCropHoppers", "hopper_data.yml");
		cfg = YamlConfiguration.loadConfiguration(f);
		saveDefaultConfig();
	}
	public FileConfiguration getSpecialConfig() {
		return cfg;
	}
}