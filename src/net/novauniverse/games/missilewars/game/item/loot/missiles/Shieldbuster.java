package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Shieldbuster extends MissileItem {

	public Shieldbuster() {
		super("§4Shieldbuster", EntityType.WITCH, GameObject.SHIELD_BUSTER);
	}
}