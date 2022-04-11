package net.novauniverse.games.missilewars.game.team;

import net.md_5.bungee.api.ChatColor;

public enum TeamColor {
	RED(ChatColor.RED, "Red"), GREEN(ChatColor.GREEN, "Green");

	private ChatColor chatColor;
	private String name;

	private TeamColor(ChatColor chatColor, String name) {
		this.chatColor = chatColor;
		this.name = name;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}

	public String getName() {
		return name;
	}

	public String getLowerCaseName() {
		return name.toLowerCase();
	}

	public TeamColor getOpposite() {
		switch (this) {
		case GREEN:
			return RED;

		case RED:
			return GREEN;

		default:
			return null;
		}
	}
}