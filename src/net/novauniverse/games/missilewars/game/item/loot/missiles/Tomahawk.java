package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Tomahawk extends MissileItem {

	public Tomahawk() {
		super("§2Tomahawk", EntityType.CREEPER, GameObject.TOMAHAWK);
	}
}