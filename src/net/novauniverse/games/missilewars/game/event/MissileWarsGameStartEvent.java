package net.novauniverse.games.missilewars.game.event;

import java.util.Map;
import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.novauniverse.games.missilewars.game.team.TeamColor;

public class MissileWarsGameStartEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Map<UUID, TeamColor> playerTeams;

	public MissileWarsGameStartEvent(Map<UUID, TeamColor> playerTeams) {
		this.playerTeams = playerTeams;
	}

	public Map<UUID, TeamColor> getPlayerTeams() {
		return playerTeams;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}