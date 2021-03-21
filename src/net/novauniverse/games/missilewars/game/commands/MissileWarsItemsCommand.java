package net.novauniverse.games.missilewars.game.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.games.missilewars.NovaMissileWars;
import net.novauniverse.games.missilewars.game.ui.MissileWarsItemsMenu;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class MissileWarsItemsCommand extends NovaCommand {

	public MissileWarsItemsCommand() {
		super("missilewarsitems", NovaMissileWars.getInstance());

		setAliases(generateAliasList("mwi"));

		setEmptyTabMode(true);
		setDescription("Show item menu");
		setAllowedSenders(AllowedSenders.PLAYERS);

		setPermission("novauniverse.games.missilewars.command.missilewarsitems");
		setPermissionDefaultValue(PermissionDefault.OP);
		setUseage("/missilewarsitems");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player player = (Player) sender;

		MissileWarsItemsMenu.show(player);

		return true;
	}
}