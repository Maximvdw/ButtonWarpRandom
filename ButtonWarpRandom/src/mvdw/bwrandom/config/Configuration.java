package mvdw.bwrandom.config;

import java.util.ArrayList;
import java.util.List;

import mvdw.bwrandom.ButtonWarpRandom;

import org.bukkit.block.Biome;

public class Configuration{
	public static ArrayList<Biome> BiomesExcluded = new ArrayList<Biome>();
	public static ArrayList<Biome> BiomesIncluded = new ArrayList<Biome>();
	public static ButtonWarpRandom plugin = null;
	
	public Configuration(ButtonWarpRandom plugin){
		this.plugin = plugin;
		
	    plugin.getConfig().options().copyDefaults(true);
	    plugin.getLogger().info("Configuration loaded!");
	    List<?> biomesInc = plugin.getConfig().getList("included-biomes");
	    for (Object b : biomesInc){
	    	String biome = ((String)b).toUpperCase(); // Convert to upper
	    	try{
		    	plugin.getLogger().info("Default Include Biome: " + biome);
		    	BiomesIncluded.add(Biome.valueOf(biome));	
	    	}catch (Exception ex){
	    		plugin.getLogger().severe("Unknown Biome in configuration! '" + b + "'");
	    		ex.printStackTrace();
	    	}
	    }
	    
	    List<?> biomesExcl = plugin.getConfig().getList("excluded-biomes");
	    for (Object b : biomesExcl){
	    	String biome = ((String)b).toUpperCase(); // Convert to upper
	    	try{
		    	plugin.getLogger().info("Default Exclude Biome: " + biome);
		    	BiomesExcluded.add(Biome.valueOf(biome));	
	    	}catch (Exception ex){
	    		plugin.getLogger().severe("Unknown Biome in configuration! '" + biome + "'");
	    		ex.printStackTrace();
	    	}
	    }
	    
	    plugin.saveConfig();
	}
	
	public static void reloadConfig(){
		plugin.reloadConfig();
	}
}
