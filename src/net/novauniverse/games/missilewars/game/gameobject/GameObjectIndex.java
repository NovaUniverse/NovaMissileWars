package net.novauniverse.games.missilewars.game.gameobject;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.novauniverse.games.missilewars.game.world.MWStructureUtil;

public class GameObjectIndex {
	public static HashMap<GameObject, Vector> positions = new HashMap<GameObject, Vector>();
	static {
		positions.put(GameObject.SHIELD, new Vector(0, 0, 0));
		positions.put(GameObject.TOMAHAWK, new Vector(0, -3, 4));
		positions.put(GameObject.SHIELD_BUSTER, new Vector(0, -3, 4));
		positions.put(GameObject.JUGGERNAUT, new Vector(0, -3, 4));
		positions.put(GameObject.LIGHTNING, new Vector(0, -3, 5));
		positions.put(GameObject.GUARDIAN, new Vector(0, -3, 4));
		positions.put(GameObject.WIN, new Vector(-27, 88, -51));
	}
	public static HashMap<GameObject, Vector> missilesRelative = new HashMap<GameObject, Vector>();
	static {
		missilesRelative.put(GameObject.SHIELD, new Vector(-115, 62, -1));
		missilesRelative.put(GameObject.TOMAHAWK, new Vector(-113, 61, -24));
		missilesRelative.put(GameObject.SHIELD_BUSTER, new Vector(-108, 61, -24));
		missilesRelative.put(GameObject.JUGGERNAUT, new Vector(-103, 62, -24));
		missilesRelative.put(GameObject.LIGHTNING, new Vector(-98, 62, 16));
		missilesRelative.put(GameObject.GUARDIAN, new Vector(-93, 62, -25));
		missilesRelative.put(GameObject.WIN, new Vector(-268, 200, -100));
	}
	public static HashMap<GameObject, Vector> missilesStart = new HashMap<GameObject, Vector>();
	static {
		missilesStart.put(GameObject.SHIELD, new Vector(-112, 65, -1));
		missilesStart.put(GameObject.TOMAHAWK, new Vector(-113, 62, -12));
		missilesStart.put(GameObject.SHIELD_BUSTER, new Vector(-107, 63, -24));
		missilesStart.put(GameObject.JUGGERNAUT, new Vector(-102, 61, -24));
		missilesStart.put(GameObject.LIGHTNING, new Vector(-97, 61, 16));
		missilesStart.put(GameObject.GUARDIAN, new Vector(-91, 61, -25));
		missilesStart.put(GameObject.WIN, new Vector(-253, 217, -91));
	}
	public static HashMap<GameObject, Vector> missilesEnd = new HashMap<GameObject, Vector>();
	static {
		missilesEnd.put(GameObject.SHIELD, new Vector(-118, 59, -1));
		missilesEnd.put(GameObject.TOMAHAWK, new Vector(-111, 61, -24));
		missilesEnd.put(GameObject.SHIELD_BUSTER, new Vector(-109, 61, -10));
		missilesEnd.put(GameObject.JUGGERNAUT, new Vector(-104, 63, -14));
		missilesEnd.put(GameObject.LIGHTNING, new Vector(-99, 62, 24));
		missilesEnd.put(GameObject.GUARDIAN, new Vector(-94, 63, -18));
		missilesEnd.put(GameObject.WIN, new Vector(-283, 200, -113));
	}

	public static void spawnObject(TeamColor team, GameObject missile, Location location) {
		Vector position = positions.get(missile);
		Vector relative = missilesRelative.get(missile);
		Vector start = missilesStart.get(missile);
		Vector end = missilesEnd.get(missile);

		if (team == TeamColor.GREEN) {
			position = position.clone().setZ(-position.getZ());
			relative = relative.clone().setZ(-relative.getZ());
			start = start.clone().setZ(-start.getZ());
			end = end.clone().setZ(-end.getZ());

			if (missile == GameObject.GUARDIAN) {
				start.setX(start.getX() - 1);
				end.setX(end.getX() - 1);
				relative.setX(relative.getX() - 1);
			} else if (missile == GameObject.TOMAHAWK) {
				relative.setX(relative.getX() + 1);
			}
		}

		Location rel = location.add(position);
		Location pasteRel = new Location(rel.getWorld(), relative.getX(), relative.getY(), relative.getZ());
		Location pasteStart = new Location(rel.getWorld(), start.getX(), start.getY(), start.getZ());
		Location pasteEnd = new Location(rel.getWorld(), end.getX(), end.getY(), end.getZ());

		MWStructureUtil.clone(pasteRel, pasteStart, pasteEnd, rel, true);
	}

	public static void spawnObject(TeamColor team, GameObject object, World world) {
		spawnObject(team, object, new Location(world, 0, 0, 0));
	}
}
