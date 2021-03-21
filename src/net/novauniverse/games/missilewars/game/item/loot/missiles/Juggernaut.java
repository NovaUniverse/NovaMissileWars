package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Juggernaut extends MissileItem {

	public Juggernaut() {
		super("§aJuggernaut", EntityType.SHEEP, GameObject.JUGGERNAUT);
	}
}