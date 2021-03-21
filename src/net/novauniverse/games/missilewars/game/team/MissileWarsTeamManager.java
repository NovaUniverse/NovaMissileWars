package net.novauniverse.games.missilewars.game.team;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class MissileWarsTeamManager extends TeamManager {
	@Override
	public boolean requireTeamToJoin(Player player) {
		return true;
	}

	public MissileWarsTeamManager() {
		teams.add(new MissileWarsTeam(TeamColor.RED));
		teams.add(new MissileWarsTeam(TeamColor.GREEN));
	}

	public MissileWarsTeam getTeam(TeamColor teamColor) {
		for (Team team : teams) {
			if (((MissileWarsTeam) team).getColor() == teamColor) {
				return (MissileWarsTeam) team;
			}
		}
		return null;
	}

	public boolean isInTeam(UUID uuid) {
		for (Team team : teams) {
			if (team.getMembers().contains(uuid)) {
				return true;
			}
		}
		return false;
	}
}