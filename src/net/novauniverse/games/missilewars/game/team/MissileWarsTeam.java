package net.novauniverse.games.missilewars.game.team;

import org.bukkit.ChatColor;

import net.zeeraa.novacore.spigot.teams.Team;

public class MissileWarsTeam extends Team {
	private TeamColor color;

	public MissileWarsTeam(TeamColor color) {
		this.color = color;
	}

	public TeamColor getColor() {
		return color;
	}

	@Override
	public ChatColor getTeamColor() {
		return color.getChatColor();
	}

	@Override
	public String getDisplayName() {
		return color.getChatColor() + color.getName();
	}
}
