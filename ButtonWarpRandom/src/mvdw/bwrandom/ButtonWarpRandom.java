package mvdw.bwrandom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import mvdw.bwrandom.config.Configuration;
import mvdw.bwrandom.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.codisimus.plugins.buttonwarp.*;

public class ButtonWarpRandom extends JavaPlugin {
	ButtonWarp bwPlugin = null;

	@Override
	public void onEnable() {
		getLogger().info("=============================");
		getLogger().info("ButtonWarpRandom Teleporter");
		getLogger().info("v" + this.getDescription().getVersion().toString());
		getLogger().info("Author: Maxim Van de Wynckel");
		getLogger().info("=============================");

		try {
			// Load configuration
			new Configuration(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			// Check for updates
			new Updater(this, 67938, this.getFile(),
					Updater.UpdateType.DEFAULT, true);
		} catch (Exception ex) {
			// Unable to check for updates
			ex.printStackTrace();
		}

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

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
		if (bwPlugin == null) {
			sender.sendMessage("§6ButtonWarp Plugin not found! [REQUIRED]");
		}
		if (cmd.getName().equalsIgnoreCase("bwr")) {
			if (args.length == 0) {
				// Show Help
				sender.sendMessage(" §6---------------[ButtonWarpRandom]---------------\n"
						+ "§c/bwr make §f- Create a new random warp\n"
						+ "§c/bwr set §f- Edit an existing warp\n"
						+ "§c/bwr reload §f- Reload the configuration\n"
						+ "§e/bwr list §f- List all random warps\n"
						+ "§e/bwr [name] §f- Simulate a warp (with commands)\n"
						+ "§a/bwr about §f- About the plugin");
				getLogger().info(
						"Showing /bwr ? for player '" + player.getName() + "'");
			} else {
				if (args[0].equalsIgnoreCase("make")) {
					if (args.length != 4) {
						player.sendMessage("§2[ButtonWarp] §cUsage: §4/bwr make [name] [minradius] [maxradius]");
					} else {
						if (sender instanceof Player) {
							if (player.hasPermission("buttonwarprandom.create")) {
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
										+ " -min " + minRange + " -max "
										+ maxRange + " -w "
										+ location.getWorld().getName()
										+ " -p <player>");
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
						// Arguments
						String worldName = ""; // WorldName
						String bwName = args[1];
						String playerName = "";
						String exclBiomes = "";
						String inclBiomes = "";
						int minRange = 0;
						int maxRange = 0;
						for (int i = 2; i < args.length; i += 2) {
							String argument = args[i].toLowerCase();
							switch (argument) {
							case "-w":
								worldName = args[i + 1];
								break;
							case "-min":
								minRange = Integer.parseInt(args[i + 1]);
								break;
							case "-max":
								maxRange = Integer.parseInt(args[i + 1]);
								break;
							case "-p":
								playerName = args[i + 1];
								break;
							case "-excl":
								exclBiomes = args[i + 1];
								break;
							case "-incl":
								inclBiomes = args[i + 1];
								break;
							}
						}
						getLogger().info("Randomizing warp '" + bwName + "'");
						Warp warp = ButtonWarp.findWarp(bwName);
						if (warp != null) {
							int xPos = (int) warp.x;
							int yPos = (int) warp.y;
							int zPos = (int) warp.z;

							ArrayList<Biome> BiomesExcluded = Configuration.BiomesExcluded;
							ArrayList<Biome> BiomesIncluded = Configuration.BiomesIncluded;
							if (inclBiomes != "") {
								String[] biomes = inclBiomes.toUpperCase()
										.split(",");
								for (Object b : biomes) {
									String biome = ((String) b).toUpperCase(); // Convert
																				// to
																				// upper
									try {
										BiomesIncluded
												.add(Biome.valueOf(biome));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
							if (exclBiomes != "") {
								String[] biomes = exclBiomes.toUpperCase()
										.split(",");
								for (Object b : biomes) {
									String biome = ((String) b).toUpperCase(); // Convert
																				// to
																				// upper
									try {
										BiomesExcluded
												.add(Biome.valueOf(biome));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}

							player = getServer().getPlayer(playerName);

							World world = getServer().getWorld(worldName);
							// Get a random but valid location
							Location coords = randomCoords(world, minRange,
									maxRange, xPos, zPos, BiomesIncluded,
									BiomesExcluded, 0);
							warp.x = coords.getX();
							warp.y = coords.getY();
							warp.z = coords.getZ();
							warp.yaw = player.getLocation().getYaw();
							warp.pitch = player.getLocation().getPitch();
							getLogger().info(
									"Location for warp '" + bwName + "'"
											+ " x:" + warp.x + ", y:" + warp.y
											+ ", z:" + warp.z);
							warp.teleport(player);
							warp.x = xPos;
							warp.y = yPos;
							warp.z = zPos;
							warp.save();
						} else {
							// Unable to find that warp
						}
					} else {
						// Message can only be run by buttonwarp
						player.sendMessage("§2[ButtonWarp] §cThis command can only be run by §4ButtonWarp!");
					}
				} else if (args[0].equalsIgnoreCase("set")) {
					if (args.length != 4) {
						// Show help
						player.sendMessage("§2[ButtonWarp] §cUsage: §4/bwr set [name] [key] [value]");
						player.sendMessage("§2[ButtonWarp] §cAvailable keys: exclude,include,min,max");
					} else {
						String bwName = args[1];
						Warp warp = ButtonWarp.findWarp(bwName);
						if (warp != null) {
							if (warp.commands.size() != 0) {
								String command = warp.commands.getFirst();
								if (command.startsWith("bwr")) {
									// Decode warp settings
									String[] args_cmd = command.split("\\s+");
									String worldName = ""; // WorldName
									String exclBiomes = "";
									String inclBiomes = "";
									int minRange = 0;
									int maxRange = 0;
									for (int i = 3; i < args_cmd.length; i += 2) {
										String argument = args_cmd[i]
												.toLowerCase();
										switch (argument) {
										case "-w":
											worldName = args_cmd[i + 1];
											break;
										case "-min":
											minRange = Integer
													.parseInt(args_cmd[i + 1]);
											break;
										case "-max":
											maxRange = Integer
													.parseInt(args_cmd[i + 1]);
											break;
										case "-excl":
											exclBiomes = args_cmd[i + 1];
											break;
										case "-incl":
											inclBiomes = args_cmd[i + 1];
											break;
										}
									}

									String key = args[2].toLowerCase();
									String value = args[3];
									switch (key) {
									case "exclude":
										player.sendMessage("§2[ButtonWarp] §aExcluded biomes have been changed!");
										exclBiomes = value;
										inclBiomes = "";
										break;
									case "include":
										player.sendMessage("§2[ButtonWarp] §aIncluded biomes have been changed!");
										inclBiomes = value;
										break;
									case "min":
										player.sendMessage("§2[ButtonWarp] §aMinimum range has been changed!");
										minRange = Integer.parseInt(value);
										break;
									case "max":
										player.sendMessage("§2[ButtonWarp] §aMaximum range has been changed!");
										maxRange = Integer.parseInt(value);
										break;
									default:
										player.sendMessage("§2[ButtonWarp] §cUnable to change this setting");
										player.sendMessage("§2[ButtonWarp] §cAvailable keys: exclude,include,min,max");
										break;
									}

									String cmdStr = "bwr randomize " + bwName
											+ " -min " + minRange + " -max "
											+ maxRange + " -w " + worldName
											+ " -p <player>";
									if (inclBiomes != "") {
										cmdStr += " -incl "
												+ inclBiomes.toUpperCase();
									} else {
										if (exclBiomes != "") {
											cmdStr += " -excl "
													+ exclBiomes.toUpperCase();
										}
									}
									// Save COMMAND
									warp.commands.set(0, cmdStr);
									warp.save();
								} else {
									// Not a valid warp
									player.sendMessage("§2[ButtonWarp] §4"
											+ bwName
											+ " §cis not a valid random warp!");
								}
							} else {
								// Not a valid warp
								player.sendMessage("§2[ButtonWarp] §4" + bwName
										+ " §cis not a valid random warp!");
							}
						} else {
							// Unable to find that warp
							player.sendMessage("§2[ButtonWarp] §4" + bwName
									+ " §cis not a valid warp!");
						}
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						if (player.hasPermission("buttonwarprandom.reload")) {
							Configuration.reloadConfig();
							sender.sendMessage("§2[ButtonWarp] §aConfiguration reloaded!");
						}
					} else {
						Configuration.reloadConfig();
						sender.sendMessage("§2[ButtonWarp] §aConfiguration reloaded!");
					}
				} else if (args[0].equalsIgnoreCase("about")) {
					// Show about
					sender.sendMessage(" §6----------------[ButtonWarpRandom]----------------\n"
							+ "§6Project: ButtonWarpRandom v"
							+ this.getDescription().getVersion().toString()
							+ "\n"
							+ "§6Author: Maxim Van de Wynckel (Maximvdw)\n"
							+ "§6Site: http://dev.bukkit.org/bukkit-mods/ButtonWarpRandom\n"
							+ "§6------------------------------------------------");
				} else {
					// Check if it is a warp
					String bwName = args[0];
					Warp warp = ButtonWarp.findWarp(bwName);
					if (warp != null) {
						if (sender instanceof Player) {
							if (player
									.hasPermission("buttonwarprandom.commandwarp")) {
								getLogger().info(
										"Activating warp by using command for player '"
												+ player.getName() + "'");
								warp.activate(player, null);
							}
						}
					} else {
						player.sendMessage("§2[ButtonWarp] §4" + bwName
								+ " §cis not a valid warp!");
					}
				}
			}
			return true;
		}
		return false;
	}

	public static Location randomCoords(World world, int min, int max,
			int xPos, int zPos, ArrayList<Biome> inc, ArrayList<Biome> excl, int flood) {
		Random rndGen = new Random();
		Location location = null;
		int r = rndGen.nextInt(max - min);
		int x = rndGen.nextInt(r) + min;
		int z = rndGen.nextInt(r) + min;
		if (rndGen.nextBoolean())
			x *= -1;
		if (rndGen.nextBoolean())
			z *= -1;
		x += xPos;
		z += zPos;
		int y = world.getHighestBlockYAt(x, z);
		location = new Location(world, x, y, z);
		Biome biome = location.getBlock().getBiome();
		if (flood >= 40){
			if (inc.size() != 0) {
				for (Biome b : inc) {
					if (biome != b) {
						return randomCoords(world, min, max, xPos, zPos, inc, excl, flood++);
					}
				}
			} else if (excl.size() != 0) {
				for (Biome b : excl) {
					if (biome == b) {
						return randomCoords(world, min, max, xPos, zPos, inc, excl, flood++);
					}
				}
			}	
		}else{
			// Overflow safety
			Bukkit.getLogger().severe("[ButtonWarpRandom] Warning: Unable to teleport to expected biome!");
		}
		return location;
	}
}
