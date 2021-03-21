package net.novauniverse.games.missilewars;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.novauniverse.games.missilewars.game.MissileWars;
import net.novauniverse.games.missilewars.game.commands.MissileWarsItemsCommand;
import net.novauniverse.games.missilewars.game.item.GunBlade;
import net.novauniverse.games.missilewars.game.item.loot.ArrowItem;
import net.novauniverse.games.missilewars.game.item.loot.FireballItem;
import net.novauniverse.games.missilewars.game.item.loot.ShieldItem;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerBoots;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerChestplate;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerHelmet;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerLeggings;
import net.novauniverse.games.missilewars.game.item.loot.missiles.Guardian;
import net.novauniverse.games.missilewars.game.item.loot.missiles.Juggernaut;
import net.novauniverse.games.missilewars.game.item.loot.missiles.Lightning;
import net.novauniverse.games.missilewars.game.item.loot.missiles.Shieldbuster;
import net.novauniverse.games.missilewars.game.item.loot.missiles.Tomahawk;
import net.novauniverse.games.missilewars.game.team.MissileWarsTeam;
import net.novauniverse.games.missilewars.game.team.MissileWarsTeamManager;
import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.novauniverse.games.missilewars.game.world.DefaultMapData;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.UnzipUtility;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseWorld;
import net.zeeraa.novacore.spigot.module.modules.multiverse.PlayerUnloadOption;
import net.zeeraa.novacore.spigot.module.modules.multiverse.WorldUnloadOption;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

public class NovaMissileWars extends JavaPlugin implements Listener {
	public static final String MAP_DOWNLOAD_URL = "https://zeeraa.net/cdn/missilewars.zip";

	private static NovaMissileWars instance;

	private File mapFile;

	private Task checkPlayerTask;

	private MissileWars game;
	private MissileWarsTeamManager teamManager;

	private MultiverseWorld world;

	public static NovaMissileWars getInstance() {
		return instance;
	}

	public MultiverseWorld getWorld() {
		return world;
	}

	public MissileWars getGame() {
		return game;
	}

	public MissileWarsTeamManager getTeamManager() {
		return teamManager;
	}

	@Override
	public void onEnable() {
		NovaMissileWars.instance = this;

		saveDefaultConfig();

		checkPlayerTask = null;

		mapFile = new File(getDataFolder().getPath() + File.separator + "missilewars");

		if (!mapFile.exists()) {
			Log.info(this.getName(), "Downloading map form zeeraa.net");

			File zipFile = new File(getDataFolder().getPath() + File.separator + "missilewars.zip");

			try {
				if (zipFile.exists()) {
					zipFile.delete();
				}

				URL url = new URL(MAP_DOWNLOAD_URL);
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
				conn.connect();
				FileUtils.copyInputStreamToFile(conn.getInputStream(), zipFile);

				UnzipUtility.unzip(zipFile.getPath(), getDataFolder().getPath());

				Log.info(this.getName(), "Download complete");

				zipFile.delete();
			} catch (IOException e) {
				Log.fatal(this.getName(), "Failed to download map from zeeraa.net");
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		}

		ModuleManager.require(MultiverseManager.class);
		ModuleManager.require(GameManager.class);
		ModuleManager.require(CustomItemManager.class);
		ModuleManager.require(GUIManager.class);
		// ModuleManager.require(NetherBoardScoreboard.class);

		Log.info(this.getName(), "Loading world");
		try {
			world = MultiverseManager.getInstance().createFromFile(mapFile, WorldUnloadOption.DELETE);
			world.getWorld().setAutoSave(false);
			world.getWorld().setStorm(false);
			world.getWorld().setTime(1000);
			world.setLockWeather(true);
			world.setPlayerUnloadOptions(PlayerUnloadOption.SEND_TO_FIRST);
			world.setSaveOnUnload(false);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal(this.getName(), "Failed to load world " + e.getClass().getName() + " " + e.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		game = new MissileWars(DefaultMapData.PORTAL_LOCATIONS);
		teamManager = new MissileWarsTeamManager();

		NovaCore.getInstance().setTeamManager(teamManager);

		GameManager.getInstance().setUseTeams(true);
		GameManager.getInstance().setShowDeathMessaage(true);
		GameManager.getInstance().loadGame(game);

		try {
			// Armor
			CustomItemManager.getInstance().addCustomItem(PlayerHelmet.class);
			CustomItemManager.getInstance().addCustomItem(PlayerChestplate.class);
			CustomItemManager.getInstance().addCustomItem(PlayerLeggings.class);
			CustomItemManager.getInstance().addCustomItem(PlayerBoots.class);

			// Items
			CustomItemManager.getInstance().addCustomItem(GunBlade.class);

			// Loot items
			CustomItemManager.getInstance().addCustomItem(FireballItem.class);
			CustomItemManager.getInstance().addCustomItem(ShieldItem.class);
			CustomItemManager.getInstance().addCustomItem(ArrowItem.class);

			// Missiles
			CustomItemManager.getInstance().addCustomItem(Guardian.class);
			CustomItemManager.getInstance().addCustomItem(Juggernaut.class);
			CustomItemManager.getInstance().addCustomItem(Lightning.class);
			CustomItemManager.getInstance().addCustomItem(Shieldbuster.class);
			CustomItemManager.getInstance().addCustomItem(Tomahawk.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bukkit.getPluginManager().registerEvents(this, this);

		checkPlayerTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (!game.hasStarted()) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						if (DefaultMapData.RED_TEAM_JOIN_AREA.isInsideBlock(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))) {
							player.sendMessage(ChatColor.RED + "You joined the red team");
							teamManager.getTeam(TeamColor.RED).addPlayer(player);
							player.teleport(LocationUtils.getLocation(world, DefaultMapData.RED_TEAM_WAITING_LOCATION, DefaultMapData.RED_TEAM_WAITING_ROTATION));
							player.setGameMode(GameMode.ADVENTURE);
							player.setDisplayName(TeamColor.RED.getChatColor() + player.getName());
						} else if (DefaultMapData.GREEN_TEAM_JOIN_AREA.isInsideBlock(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))) {
							player.sendMessage(ChatColor.GREEN + "You joined the green team");
							teamManager.getTeam(TeamColor.GREEN).addPlayer(player);
							player.teleport(LocationUtils.getLocation(world, DefaultMapData.GREEN_TEAM_WAITING_LOCATION, DefaultMapData.GREEN_TEAM_WAITING_ROTATION));
							player.setGameMode(GameMode.ADVENTURE);
							player.setDisplayName(TeamColor.GREEN.getChatColor() + player.getName());
						}
					}
				}
			}
		}, 5L);
		checkPlayerTask.start();

		CommandRegistry.registerCommand(new MissileWarsItemsCommand());
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(checkPlayerTask);
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}

	public Location getSpawnLocation() {
		return LocationUtils.getLocation(world, DefaultMapData.SPAWN_LOCATION, DefaultMapData.SPAWN_ROTATION);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		Location spawnLocation = getSpawnLocation();

		if (game.hasStarted()) {
			if (!game.getPlayers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "Joined as spectator since the game has already started");
				player.setGameMode(GameMode.SPECTATOR);
				player.setDisplayName(ChatColor.GRAY + player.getName());
				player.spigot().respawn();
				PlayerUtils.clearPlayerInventory(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						player.teleport(spawnLocation);
					}
				}.runTaskLater(this, 2L);
			}
		} else {
			PlayerUtils.clearPlayerInventory(player);
			player.setGameMode(GameMode.ADVENTURE);
			PlayerUtils.resetMaxHealth(player);
			PlayerUtils.resetPlayerXP(player);
			PlayerUtils.fullyHealPlayer(player);
			player.spigot().respawn();
			
			MissileWarsTeam team = (MissileWarsTeam) teamManager.getPlayerTeam(player);
			
			if (team == null) {
				player.setDisplayName(ChatColor.WHITE + player.getName());
			} else {
				player.setDisplayName(team.getColor().getChatColor() + player.getName());
			}
			
			new BukkitRunnable() {
				@Override
				public void run() {
					player.teleport(spawnLocation);
				}
			}.runTaskLater(this, 2L);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
				Log.trace(getName(), e.getPlayer() + " interacted with sign. " + e.getAction().name() + ". Canceled: " + e.isCancelled());

				if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
					if (e.getClickedBlock().getState() instanceof Sign) {
						Sign sign = (Sign) e.getClickedBlock().getState();

						if (sign.getLines()[2].contains("Lobby")) {
							if (!game.hasStarted()) {
								Player player = e.getPlayer();

								Team team = teamManager.getPlayerTeam(player);

								if (team != null) {
									team.getMembers().remove(player.getUniqueId());
								}

								player.sendMessage(ChatColor.YELLOW + "You left your team");

								player.teleport(getSpawnLocation());
								
								player.setDisplayName(ChatColor.WHITE + player.getName());
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (!game.hasStarted()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (!game.hasStarted()) {
			Team team = teamManager.getPlayerTeam(e.getPlayer());

			if (team != null) {
				team.getMembers().remove(e.getPlayer().getUniqueId());
			}
		}
	}
}