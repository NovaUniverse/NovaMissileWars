package net.novauniverse.games.missilewars.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;

public class MissileWarsGameEndEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private TeamColor winningTeam;
	private GameEndReason gameEndReason;

	public MissileWarsGameEndEvent(TeamColor winningTeam, GameEndReason gameEndReason) {
		this.winningTeam = winningTeam;
		this.gameEndReason = gameEndReason;
	}

	public TeamColor getWinningTeam() {
		return winningTeam;
	}

	public GameEndReason getGameEndReason() {
		return gameEndReason;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}