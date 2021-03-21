package net.novauniverse.games.missilewars.game.team;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.novauniverse.games.missilewars.NovaMissileWars;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.NumberUtils;

public class TeamBalancer {
	public static void balanceTeams() {
		MissileWarsTeam greenTeam = NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.GREEN);
		MissileWarsTeam redTeam = NovaMissileWars.getInstance().getTeamManager().getTeam(TeamColor.RED);

		int playerCount = Bukkit.getServer().getOnlinePlayers().size();
		boolean evenCount = NumberUtils.isEven(playerCount);

		Log.trace("TeamBalancer", "Player count: " + playerCount);
		Log.trace("TeamBalancer", "Player count even: " + evenCount);

		// Make players that have not chosen a team join
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			MissileWarsTeam team = (MissileWarsTeam) NovaMissileWars.getInstance().getTeamManager().getPlayerTeam(player);

			if (team == null) {
				if (greenTeam.size() > redTeam.size()) {
					redTeam.addPlayer(player);
					player.sendMessage(ChatColor.RED + "You joined the red team");
					Log.trace("TeamBalancer", "Adding " + player.getName() + " to the red team");
				} else {
					greenTeam.addPlayer(player);
					player.sendMessage(ChatColor.GREEN + "You joined the green team");
					Log.trace("TeamBalancer", "Adding " + player.getName() + " to the green team");
				}
			}
		}

		Random random = new Random();

		while (redTeam.size() > (greenTeam.size() + 1)) {
			int i = random.nextInt(redTeam.getMembers().size());
			UUID toMove = redTeam.getMembers().remove(i);

			TeamBalancer.trySendMessage(toMove, "You were moved to the green team to make the player count more balanced");

			Log.trace("TeamBalancer", "Moving " + toMove.toString() + " to the green team");

			greenTeam.addPlayer(toMove);
		}

		while (greenTeam.size() > (redTeam.size() + 1)) {
			int i = random.nextInt(greenTeam.getMembers().size());
			UUID toMove = greenTeam.getMembers().remove(i);

			TeamBalancer.trySendMessage(toMove, ChatColor.YELLOW + "You were moved to the red team to make the player count more balanced");

			Log.trace("TeamBalancer", "Moving " + toMove.toString() + " to the red team");

			redTeam.addPlayer(toMove);
		}

	}

	private static void trySendMessage(UUID uuid, String message) {
		Player player = Bukkit.getServer().getPlayer(uuid);
		if (player != null) {
			if (player.isOnline()) {
				player.sendMessage(message);
			}
		}
	}
}