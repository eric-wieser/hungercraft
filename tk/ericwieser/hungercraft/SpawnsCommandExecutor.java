package tk.ericwieser.hungercraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpawnsCommandExecutor implements CommandExecutor {
	private HungerCraftPlugin _plugin;

	public SpawnsCommandExecutor(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {
		
		if(args.length != 1) {
			return false;
		}
		
		String action = args[0];
		
		if(action.equalsIgnoreCase("open")) {
			_plugin.getLogger().info("Barriers lowered");
			_plugin.spawnManager.lowerBarriers();
			return true;
			
		}
		else if(action.equalsIgnoreCase("close")) {
			_plugin.getLogger().info("Barriers raised");
			_plugin.spawnManager.raiseBarriers();
			return true;
			
		}
		else if(action.equalsIgnoreCase("assign")) {
			_plugin.getLogger().info("Players assigned");
			_plugin.spawnManager.assignPlayers();
			return true;
		}
		return false;
	}
}
