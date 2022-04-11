package net.novauniverse.games.missilewars.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.novauniverse.games.missilewars.NovaMissileWars;
import net.novauniverse.games.missilewars.game.event.MissileWarsGameEndEvent;
import net.novauniverse.games.missilewars.game.event.MissileWarsGameStartEvent;
import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.gameobject.GameObjectIndex;
import net.novauniverse.games.missilewars.game.item.GunBlade;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerBoots;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerChestplate;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerHelmet;
import net.novauniverse.games.missilewars.game.item.loot.armor.PlayerLeggings;
import net.novauniverse.games.missilewars.game.loot.LootManager;
import net.novauniverse.games.missilewars.game.team.MissileWarsTeam;
import net.novauniverse.games.missilewars.game.team.TeamBalancer;
import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.novauniverse.games.missilewars.game.world.DefaultMapData;
import net.novauniverse.games.missilewars.game.world.PortalLocation;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.Rotation;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.PlayerDamageReason;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependantPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.RepeatingGameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerCallback;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerFlag;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

public class MissileWars extends Game implements Listener {
	public static final int INSTANT_KILL_Y = -1;
	public static final long LOOT_DELAY = 280L;

	public static final long PLAYER_CHECK_TASK_INTERVAL = 5L;
	public static final int TIME_BEFORE_SHUTDOWN_IF_ONLY_ONE_TEAM_LEFT = (int) ((20 * 60 * 3) / PLAYER_CHECK_TASK_INTERVAL);

	private int shutdownCounter;

	private boolean started;
	private boolean ended;
	private List<PortalLocation> portalLocations;

	private GameTrigger lootTrigger;

	private Task winCheckTask;
	private Task playerCheckTask;

	private TeamColor winner;

	private boolean useTeamBalancer;

	public MissileWars(List<PortalLocation> portalLocations, boolean useTeamBalancer) {
		super(NovaMissileWars.getInstance());
		
		this.portalLocations = portalLocations;
		this.useTeamBalancer = useTeamBalancer;
	}

	@Override
	public String getName() {
		return "missilewars";
	}

	@Override
	public String getDisplayName() {
		return "Missile Wars";
	}

	@Override
	public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
		return PlayerQuitEliminationAction.NONE;
	}

	@Override
	public boolean eliminatePlayerOnDeath(Player player) {
		return false;
	}

	@Override
	public boolean isPVPEnabled() {
		return hasStarted();
	}

	@Override
	public boolean autoEndGame() {
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity attacker, LivingEntity target) {
		return true;
	}

	@Override
	public boolean canStart() {
		boolean canStart = true;

		if (NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.RED).getMembers().size() == 0) {
			canStart = false;
		}

		if (NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.GREEN).getMembers().size() == 0) {
			canStart = false;
		}

		return canStart;
	}

	public boolean isUseTeamBalancer() {
		return useTeamBalancer;
	}

	@Override
	public void onLoad() {
		this.world = NovaMissileWars.getInstance().getWorld().getWorld();
		this.winner = null;
		this.started = false;
		this.ended = false;
		this.shutdownCounter = 0;

		this.winCheckTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (PortalLocation portalLocation : portalLocations) {
					if (portalLocation.isBroken(world)) {
						winner = portalLocation.getColor().getOpposite();

						Log.debug("MissileWars", "Portal at " + portalLocation.getVector().toString() + " owned by " + portalLocation.getColor().getLowerCaseName() + " team was destroyed. Ending game with " + winner.getLowerCaseName() + " team as winner");
						Log.trace("MissileWars", "Material at portal is " + LocationUtils.getLocation(world, portalLocation.getVector()).getBlock().getType().name() + " world: " + world.getName());

						endGame(GameEndReason.WIN);

						break;
					}
				}
			}
		}, 4L);

		this.playerCheckTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getLocation().getY() < INSTANT_KILL_Y) {
						if (players.contains(player.getUniqueId())) {
							NovaCore.getInstance().getVersionIndependentUtils().damagePlayer(player, PlayerDamageReason.OUT_OF_WORLD, 1000F);
						} else {
							player.setFallDistance(0);
							player.teleport(NovaMissileWars.getInstance().getSpawnLocation());
						}
					}
				}

				boolean redTeamOnline = false;
				boolean greenTeamOnline = false;

				if (hasStarted() && !hasEnded()) {
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						MissileWarsTeam team = (MissileWarsTeam) TeamManager.getTeamManager().getPlayerTeam(player);

						if (team != null) {
							switch (team.getColor()) {
							case RED:
								redTeamOnline = true;
								break;

							case GREEN:
								greenTeamOnline = true;
								break;
							}
						}
					}

					if (redTeamOnline && greenTeamOnline) {
						// Both teams online
						shutdownCounter = TIME_BEFORE_SHUTDOWN_IF_ONLY_ONE_TEAM_LEFT;
					} else if (!redTeamOnline && !greenTeamOnline) {
						// No teams online
						Log.info("MissileWars", "No players online. Ending game");
						Task.tryStopTask(playerCheckTask);
						endGame(GameEndReason.NO_PLAYERS_LEFT);
					} else {
						// Both of the above are false meaning that only 1 team is online
						shutdownCounter--;
						if (shutdownCounter <= 0) {
							Bukkit.getServer().broadcastMessage(ChatColor.RED + "Game ended since only 1 team is left");
							Task.tryStopTask(playerCheckTask);

							if (redTeamOnline) {
								winner = TeamColor.RED;
							}

							if (greenTeamOnline) {
								winner = TeamColor.GREEN;
							}

							endGame(GameEndReason.WIN);
						}
					}
				}
			}
		}, PLAYER_CHECK_TASK_INTERVAL);

		this.lootTrigger = new RepeatingGameTrigger("missilewars.loot", 1L, LOOT_DELAY, new TriggerCallback() {
			@Override
			public void run(GameTrigger trigger, TriggerFlag reason) {
				Class<? extends CustomItem> item = LootManager.getRandom(random);

				for (UUID uuid : players) {
					Player player = Bukkit.getServer().getPlayer(uuid);

					if (player != null) {
						if (player.isOnline()) {
							ItemStack stack = CustomItemManager.getInstance().getCustomItemStack(item, player);
							player.getInventory().addItem(stack);
						}
					}
				}
			}
		});

		lootTrigger.addFlag(TriggerFlag.START_ON_GAME_START);
		lootTrigger.addFlag(TriggerFlag.STOP_ON_GAME_END);

		addTrigger(lootTrigger);
	}

	@Override
	public void onStart() {
		if (useTeamBalancer) {
			TeamBalancer.balanceTeams();
		}

		Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
			MissileWarsTeam team = (MissileWarsTeam) NovaMissileWars.getInstance().getTeamManager().getPlayerTeam(player);

			if (team != null) {
				players.add(player.getUniqueId());
			} else {
				player.setGameMode(GameMode.SPECTATOR);
			}
		});

		started = true;
		winCheckTask.start();
		playerCheckTask.start();

		Bukkit.getServer().getOnlinePlayers().forEach((player) -> {
			PlayerUtils.clearPlayerInventory(player);

			givePlayerItems(player);

			setSpawnAndTeleportToTeam(player);
		});

		this.sendBeginEvent();

		Map<UUID, TeamColor> playerTeams = new HashMap<UUID, TeamColor>();

		NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.RED).getMembers().forEach((uuid) -> {
			playerTeams.put(uuid, TeamColor.RED);
		});

		NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.GREEN).getMembers().forEach((uuid) -> {
			playerTeams.put(uuid, TeamColor.GREEN);
		});

		MissileWarsGameStartEvent e = new MissileWarsGameStartEvent(playerTeams);
		Bukkit.getServer().getPluginManager().callEvent(e);
	}

	private void givePlayerItems(Player player) {
		player.getInventory().addItem(CustomItemManager.getInstance().getCustomItemStack(GunBlade.class, player));

		player.getInventory().setHelmet(CustomItemManager.getInstance().getCustomItemStack(PlayerHelmet.class, player));
		player.getInventory().setChestplate(CustomItemManager.getInstance().getCustomItemStack(PlayerChestplate.class, player));
		player.getInventory().setLeggings(CustomItemManager.getInstance().getCustomItemStack(PlayerLeggings.class, player));
		player.getInventory().setBoots(CustomItemManager.getInstance().getCustomItemStack(PlayerBoots.class, player));
	}

	private boolean setSpawnAndTeleportToTeam(Player player) {
		MissileWarsTeam team = (MissileWarsTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(player);

		if (team == null) {
			return false;
		}

		Location location;

		switch (team.getColor()) {
		case GREEN:
			location = LocationUtils.getLocation(world, DefaultMapData.GREEN_TEAM_SPAWN_LOCATION, DefaultMapData.GREEN_TEAM_SPAWN_ROTATION);
			break;

		case RED:
			location = LocationUtils.getLocation(world, DefaultMapData.RED_TEAM_SPAWN_LOCATION, DefaultMapData.RED_TEAM_SPAWN_ROTATION);
			break;

		default:
			return false;
		}

		player.setDisplayName(team.getColor().getChatColor() + player.getName());

		NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, team.getColor().getChatColor());

		player.setBedSpawnLocation(location, true);
		player.teleport(location);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setFireTicks(0);

		return true;
	}

	@Override
	public void onEnd(GameEndReason reason) {
		ended = true;

		Task.tryStopTask(winCheckTask);
		Task.tryStopTask(playerCheckTask);

		if (reason == GameEndReason.WIN) {
			GameObjectIndex.spawnObject(winner, GameObject.WIN, world);

			String message = winner.getChatColor() + winner.getName() + " team wins! Congratulations to";

			List<String> names = new ArrayList<String>();

			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.setGameMode(GameMode.SPECTATOR);

				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.WITHER_DEATH, 1F, 1F);

				MissileWarsTeam team = (MissileWarsTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(player);

				if (team == null) {
					continue;
				}

				if (team.getColor() == winner) {
					names.add(player.getName());
				}
			}

			while (names.size() > 0) {
				String separator;

				switch (names.size()) {
				case 1:
					separator = "";
					break;

				case 2:
					separator = " and";
					break;

				default:
					separator = " ,";
					break;
				}

				message += " " + names.remove(0) + separator;
			}

			Bukkit.getServer().broadcastMessage(message);
		}

		MissileWarsGameEndEvent e = new MissileWarsGameEndEvent(winner, reason);
		Bukkit.getPluginManager().callEvent(e);
	}

	/* -=-=-=-=-= Listeners =-=-=-=-=- */

	// Fix rotation
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player player = e.getPlayer();

		if (hasStarted()) {
			MissileWarsTeam team = (MissileWarsTeam) TeamManager.getTeamManager().getPlayerTeam(player);

			if (team != null) {
				Rotation rotation = null;

				switch (team.getColor()) {
				case GREEN:
					rotation = DefaultMapData.GREEN_TEAM_SPAWN_ROTATION;
					break;

				case RED:
					rotation = DefaultMapData.RED_TEAM_SPAWN_ROTATION;
					break;

				default:
					rotation = new Rotation(0, 0);
					break;
				}

				Location location = e.getRespawnLocation();

				location.setYaw(rotation.getYaw());
				location.setPitch(rotation.getPitch());

				e.setRespawnLocation(location);
			}
		}
	}

	// Handle join
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (hasStarted()) {
			if (players.contains(e.getPlayer().getUniqueId())) {
				setSpawnAndTeleportToTeam(e.getPlayer());
			}
		}
	}

	// Prevent portals from working
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (e.getCause() == TeleportCause.NETHER_PORTAL || e.getCause() == TeleportCause.END_PORTAL) {
			e.setCancelled(true);
		}
	}

	// Auto respawning players
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		MissileWarsTeam team = (MissileWarsTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(e.getEntity());
		if (team != null) {
			e.setDeathMessage(e.getDeathMessage().replaceAll(Pattern.compile(e.getEntity().getName()).pattern(), team.getColor().getChatColor() + e.getEntity().getName() + ChatColor.RESET));
		}

		Player killer = e.getEntity().getKiller();

		if (killer != null) {
			MissileWarsTeam killerTeam = (MissileWarsTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(killer);

			if (killerTeam != null) {
				e.setDeathMessage(e.getDeathMessage().replace(Pattern.compile(killer.getName()).pattern(), killerTeam.getColor().getChatColor() + killer.getName() + ChatColor.RESET));
			}
		}

		e.getEntity().setFallDistance(0F);
		e.getEntity().setFireTicks(0);
		e.getEntity().setVelocity(new Vector(0, 0, 0));

		new BukkitRunnable() {
			@Override
			public void run() {
				e.getEntity().spigot().respawn();
				e.getEntity().setFallDistance(0F);
				e.getEntity().setFireTicks(0);
				e.getEntity().setVelocity(new Vector(0, 0, 0));
			}
		}.runTaskLater(NovaMissileWars.getInstance(), 2L);
	}

	// Prevent food decrease
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (((Player) e.getEntity()).getFoodLevel() > e.getFoodLevel()) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVersionIndependantPlayerAchievementAwarded(VersionIndependantPlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}

	// stop kicking falling players
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equalsIgnoreCase("Flying is not enabled on this server")) {
			final Player p = event.getPlayer();
			if (p.getVelocity().getY() < 0) {
				event.setCancelled(true);
			}
		}
	}
}