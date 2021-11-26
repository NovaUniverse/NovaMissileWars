package net.novauniverse.games.missilewars.game.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;

import net.novauniverse.games.missilewars.NovaMissileWars;
import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.gameobject.GameObjectIndex;
import net.novauniverse.games.missilewars.game.team.MissileWarsTeam;
import net.zeeraa.novacore.spigot.NovaCore;

public class SpawnItems {
	public static void spawnFireball(Player player) {
		final Fireball fireball = player.launchProjectile(Fireball.class);
		fireball.setVelocity(player.getLocation().getDirection().multiply(2));
		fireball.setBounce(false);
		fireball.setIsIncendiary(true);
		fireball.setCustomName(ChatColor.GOLD + "Fireball");
		fireball.setCustomNameVisible(false);
		fireball.setShooter(player);

	}

	public static void spawnShield(Player player) {
		MissileWarsTeam team = (MissileWarsTeam) NovaCore.getInstance().getTeamManager().getPlayerTeam(player);

		if (team == null) {
			return;
		}

		final Snowball shield = player.launchProjectile(Snowball.class);
		shield.setCustomName(ChatColor.DARK_BLUE + "Shield");
		shield.setCustomNameVisible(false);
		shield.setBounce(false);

		Bukkit.getScheduler().scheduleSyncDelayedTask(NovaMissileWars.getInstance(), () -> {
			if (shield != null && !shield.isDead() && !shield.isOnGround()) {
				GameObjectIndex.spawnObject(team.getColor(), GameObject.SHIELD, shield.getLocation());
				shield.remove();
			}
		}, 20L);
	}
}