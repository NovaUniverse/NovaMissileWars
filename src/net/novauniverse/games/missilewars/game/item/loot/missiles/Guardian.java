package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Guardian extends MissileItem {

	public Guardian() {
		super("§aGuardian", EntityType.GUARDIAN, GameObject.GUARDIAN);
	}
}