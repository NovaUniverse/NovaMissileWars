package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Tomahawk extends MissileItem {

	public Tomahawk() {
		super(ChatColor.DARK_GREEN + "Tomahawk", EntityType.CREEPER, GameObject.TOMAHAWK);
	}
}