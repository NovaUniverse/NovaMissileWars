package net.novauniverse.games.missilewars.game.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.zeeraa.novacore.spigot.utils.LocationUtils;

public class PortalLocation {
	private Vector vector;
	private TeamColor color;

	public PortalLocation(Vector vector, TeamColor color) {
		this.vector = vector;
		this.color = color;
	}

	public Vector getVector() {
		return vector;
	}

	public TeamColor getColor() {
		return color;
	}

	public boolean isBroken(World world) {
		Location location = LocationUtils.getLocation(world, vector);

		return location.getBlock().getType() != Material.PORTAL;
	}
}