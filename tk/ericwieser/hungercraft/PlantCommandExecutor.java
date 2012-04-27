package tk.ericwieser.hungercraft;

import java.util.Random;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PlantCommandExecutor implements CommandExecutor {

	@SuppressWarnings("unused")
    private HungerCraftPlugin _plugin;

	public PlantCommandExecutor(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
	}
	
	private void plantAt(Block b, byte step) {
		b.setType(Material.CROPS);
		b.setData(step);
		b.getRelative(BlockFace.DOWN).setType(Material.SOIL);
	}
	
	private void unplantAt(Block b) {
		b.setType(Material.AIR);
	}
	
	private void plantRandom(Block b, Random r) {
		if (b == null || !isPlantable(b)) { return; }
		if(r == null) r = new Random();
		
		plantAt(b, (byte) r.nextInt(8));
		
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
			plantRandom(neighbour, r);
		}
	}
	private void plant(Block b, byte growth) {
		if (b == null || !isPlantable(b)) { return; }
		
		plantAt(b, growth);
		
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
			plant(neighbour, growth);
		}
	}
	
	private void unplant(Block b) {
		if (b == null || !isUnplantable(b)) { return; }
		
		unplantAt(b);
		
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
			unplant(neighbour);
		}
	}
	
	private boolean isPlantable(Block b) {
		Block below = b.getRelative(BlockFace.DOWN);
		return (below.getType() == Material.GRASS || below.getType() == Material.SOIL) && b.getType() == Material.AIR;
	}
	
	private boolean isUnplantable(Block b) {
		Block below = b.getRelative(BlockFace.DOWN);
		return (below.getType() == Material.SOIL && b.getType() == Material.CROPS);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			Block target = player.getTargetBlock(null, 20).getRelative(BlockFace.UP);
			sender.sendMessage(ChatColor.BLUE + "Igniting blocks");
			
			if(args.length == 1) {
				if(args[0].equals("random")) {
					plantRandom(target, null);
					return true;
				} else if(args[0].equals("lever")) {
					plant(target, (byte) 8);
					return true;
				} else if(args[0].equals("door")) {
					plant(target, (byte) 9);
					return true;
				} else if(args[0].equals("clear")) {
					if(target.getType() == Material.AIR)
						target = target.getRelative(BlockFace.DOWN);
					unplant(target);
					return true;
				} else {
					try {
						byte n = Byte.parseByte(args[0]);
						plant(target, n);
						return true;
					} catch(NumberFormatException e) {
						
					}
				}
			} else {
				plant(target, (byte) 7);
				return true;
			}
			return false;
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player!");
			return false;
		}
	}

}
