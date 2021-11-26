package net.novauniverse.games.missilewars.game.item.loot.missiles;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import net.novauniverse.games.missilewars.game.gameobject.GameObject;
import net.novauniverse.games.missilewars.game.item.MissileItem;

public class Juggernaut extends MissileItem {

	public Juggernaut() {
		super(ChatColor.GREEN + "Juggernaut", EntityType.SHEEP, GameObject.JUGGERNAUT);
	}
}