package tk.ericwieser.hungercraft;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class IgniteCommandExecutor implements CommandExecutor {

	@SuppressWarnings("unused")
    private HungerCraftPlugin _plugin;

	public IgniteCommandExecutor(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
	}

	private void ignite(Block b) {
		if (!isIgnitable(b)) { return; }
		b.setType(Material.FIRE);
		Block[] neighbours = new Block[] {
		        b.getRelative(BlockFace.NORTH),
		        b.getRelative(BlockFace.NORTH_EAST),
		        b.getRelative(BlockFace.EAST),
		        b.getRelative(BlockFace.SOUTH_EAST),
		        b.getRelative(BlockFace.SOUTH),
		        b.getRelative(BlockFace.SOUTH_WEST),
		        b.getRelative(BlockFace.WEST),
		        b.getRelative(BlockFace.NORTH_WEST),
		};
		for (Block neighbour : neighbours) {
			ignite(neighbour);
		}
	}

	private boolean isIgnitable(Block b) {
		Block below = b.getRelative(BlockFace.DOWN);
		return below.getType() == Material.NETHERRACK && b.getType() == Material.AIR;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = player.getTargetBlock(null, 20).getRelative(BlockFace.UP);
			sender.sendMessage(ChatColor.BLUE + "Igniting blocks");
			ignite(target);
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player!");
			return false;
		}
	}

}
