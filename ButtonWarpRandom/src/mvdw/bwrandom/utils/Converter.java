package mvdw.bwrandom.utils;

import java.util.Collection;

import org.bukkit.Bukkit;

import com.codisimus.plugins.buttonwarp.ButtonWarp;
import com.codisimus.plugins.buttonwarp.Warp;

public class Converter {
	public Converter(){
		Collection<Warp> warps = ButtonWarp.getWarps();
		for (Warp warp : warps){
			// Check if warp contains bwr
			if (warp.commands.size() != 0){
				String cmd = warp.commands.getFirst();
				if (cmd.startsWith("bwr") && !cmd.contains("-")){
					String args[] = cmd.split("\\s+");
					// Try to parse
					try{
						String newCmd = "bwr randomize " + args[2] + " -min " + args[3] + " -max " + args[4] +
								" -p <player>";
						warp.commands.set(0, newCmd);
						warp.save();
						Bukkit.getLogger().info("[ButtonWarpRandom] Converted old warp '" + warp.name + "'");
					}catch (Exception ex){
						
					}
				}
			}
		}
	}
}
