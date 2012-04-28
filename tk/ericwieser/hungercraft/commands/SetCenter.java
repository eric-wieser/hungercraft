package tk.ericwieser.hungercraft.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import tk.ericwieser.hungercraft.HungerCraftPlugin;
import tk.ericwieser.hungercraft.SpawnManager;

public class SetCenter implements CommandExecutor {
	private HungerCraftPlugin _plugin;

	public SetCenter(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = player.getTargetBlock(null, 20);
			
			Location center = target.getLocation();

			_plugin.center = center;
			_plugin.getConfig().set("cornucopia.center", center.toVector());
			_plugin.saveConfig();

			_plugin.spawnManager = new SpawnManager(center);
			
			_plugin.getLogger().info("Center set to " + center.toVector());
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player!");
			return false;
		}
	}
}
