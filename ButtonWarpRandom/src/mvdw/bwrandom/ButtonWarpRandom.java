package mvdw.bwrandom;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

import com.codisimus.plugins.buttonwarp.*;

public class ButtonWarpRandom extends JavaPlugin {
	ButtonWarp bwPlugin = null;

	@Override
	public void onEnable() {
		getLogger().info("=============================");
		getLogger().info("ButtonWarpRandom Teleporter");
		getLogger().info("v1.0.0");
		getLogger().info("Author: Maxim Van de Wynckel");
		getLogger().info("=============================");

		try {
			getLogger().info("Checking if ButtonWarp is found ...");
			Plugin plugin = getServer().getPluginManager().getPlugin(
					"ButtonWarp");
			if (plugin != null) {
				bwPlugin = (ButtonWarp) plugin;
				getLogger().info("Hooked into ButtonWarp!");
			} else {
				getLogger().severe("ButtonWarp NOT FOUND!");
				return;
			}
		} catch (Exception ex) {

		}

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (cmd.getName().equalsIgnoreCase("bwr")) {
			if (args.length == 0) {
				// Show Help
				sender.sendMessage(" §6---------------[ButtonWarpRandom]---------------\n" +
						"§c/bwr make §f- Create a new random warp\n" + 
						"§c/bwr edit §f- Edit a random warp\n" + 
						"§e/bwr [name] §f- Simmulate a warp (with commands)\n" + 
						"§a/bwr about §f- About the plugin");
				getLogger().info(
						"Showing /bwr ? for player '" + player.getName() + "'");
			} else {
				if (args[0].equalsIgnoreCase("make")) {
					if (args.length != 4) {
						player.sendMessage("§2[ButtonWarp] §cUsage: §4/bwr make [name] [minradius] [maxradius]");
					} else {
						if (sender instanceof Player) {
							if (player.hasPermission("buttonwarp.create")) {
								// Make a random teleport
								String bwName = args[1];
								int minRange = Integer.parseInt(args[2]);
								int maxRange = Integer.parseInt(args[3]);
								Location location = player.getLocation();

								Warp warp = new Warp(bwName, null);
								warp.x = location.getX();
								warp.y = location.getY();
								warp.z = location.getZ();
								warp.ignorePitch = true;
								warp.ignoreYaw = true;
								warp.world = location.getWorld().getName();
								warp.commands.add("bwr randomize " + bwName
										+ " " + minRange + " " + maxRange + " "
										+ location.getWorld().getName()
										+ " <player>");
								// Add the warp
								getLogger().info(
										"Adding warp '" + bwName
												+ "' to ButtonWarp!");
								ButtonWarp.addWarp(warp);
								player.sendMessage("§2[ButtonWarp] §aRandom warp '"
										+ bwName + "' has been created!");
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("randomize")) {
					if (sender instanceof ButtonWarpCommandSender) {
						String bwName = args[1];
						getLogger().info("Randomizing warp '" + bwName + "'");
						Warp warp = ButtonWarp.findWarp(bwName);
						if (warp != null) {
							int minRange = Integer.parseInt(args[2]);
							int maxRange = Integer.parseInt(args[3]);
							String worldName = args[4];
							int xPos = (int) warp.x;
							int zPos = (int) warp.z;
							String playerName = args[5];
							player = getServer().getPlayer(playerName);

							// Random Generator
							Random r = new Random();
							int rndX = r.nextInt(maxRange - minRange)
									+ minRange + xPos;
							int rndZ = r.nextInt(maxRange - minRange)
									+ minRange + zPos;
							int rndY = getServer().getWorld(worldName)
									.getHighestBlockYAt(rndX, rndZ);
							warp.x = rndX;
							warp.y = rndY;
							warp.z = rndZ;
							warp.yaw = player.getLocation().getYaw();
							warp.pitch = player.getLocation().getPitch();
							getLogger().info(
									"New Location for warp '" + bwName + "'"
											+ " x:" + rndX + ", y:" + rndY
											+ ", z:" + rndZ);
							warp.teleport(player);
						} else {
							// Unable to find that warp
						}
					} else {
						// Message can only be run by buttonwarp
						player.sendMessage("§2[ButtonWarp] §cThis command can only be run by §4ButtonWarp!");
					}
				} else if (args[0].equalsIgnoreCase("about")) {
					// Show about
					sender.sendMessage(" §6---------------[ButtonWarpRandom]---------------\n"
							+ "§6Project: ButtonWarpRandom v1.0.0\n"
							+ "§6Author: Maxim Van de Wynckel (Maximvdw)\n"
							+ "§6Site: http://dev.bukkit.org/bukkit-mods/ButtonWarpRandom\n"
							+ "§6----------------------------------------------");
				} else {
					// Check if it is a warp
					String bwName = args[0];
					Warp warp = ButtonWarp.findWarp(bwName);
					if (warp != null) {
						if (sender instanceof Player) {
							if (player.hasPermission("buttonwarp.commandwarp")) {
								getLogger().info(
										"Activating warp by using command for player '"
												+ player.getName() + "'");
								warp.activate(player, null);
							}
						}
					}else{
						player.sendMessage("§2[ButtonWarp] §4" + bwName + " §cis not a valid warp!");
					}
				}
			}
			return true;
		}
		return false;
	}
}
