package tk.ericwieser.hungercraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class SpawnManager {
	private List<Location> _spawns;
	private Location       _center;

	public SpawnManager(Location center) {
		_spawns = new ArrayList<Location>();
		_center = center;

		Location cursor = center.clone();
		for (int y = -10; y <= 10 && _spawns.isEmpty(); y++) {
			cursor.setY(center.getY() + y);
			for (int x = -30; x <= 30; x++) {
				cursor.setX(center.getX() + x);
				for (int z = -30; z <= 30; z++) {
					cursor.setZ(center.getZ() + z);
					if (cursor.getBlock().getType() == Material.IRON_BLOCK) {
						Location spawn = cursor.clone();
						spawn.setY(spawn.getY() + 1);
						_spawns.add(spawn);
					}
				}
			}
		}

		// Add higher iron, remove pistons
		for (Location spawn : _spawns) {
			Block base = spawn.getBlock();
			base.setType(Material.IRON_BLOCK);
			base.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)
			        .setType(Material.AIR);
		}

		Collections.sort(_spawns, new Comparator<Location>() {
			@Override
			public int compare(Location a, Location b) {

				Vector aToC = _center.clone().subtract(a).toVector();
				Vector bToC = _center.clone().subtract(b).toVector();
				double diff = Math.atan2(aToC.getX(), aToC.getZ()) -
				        Math.atan2(bToC.getX(), bToC.getZ());
				return (int) Math.signum(diff);
			}
		});

		raiseBarriers();
	}
	
	public int count() {
		return _spawns.size();
	}

	public void assignPlayers(Set<Player> players) {
		raiseBarriers();

		float spawnsPerPlayer = _spawns.size() / (float) players.size();
		int i = 0;
		for(Player p : players) {
			if(i < _spawns.size()) {
    			p.setFoodLevel(20);
    			p.setHealth(20);
    			p.getInventory().clear();
    			
    			p.setVelocity(new Vector(0, 0, 0));
    
    			int spawnIndex = (int) (i * spawnsPerPlayer);
    			Location spawnLocation = _spawns.get(spawnIndex).clone()
    			        .add(0.5, 1, 0.5);
    
    			Vector pToC = _center.clone().add(0.5, 0, 0.5)
    			        .subtract(spawnLocation).toVector();
    
    			spawnLocation.setYaw((float) Math.toDegrees(-Math.atan2(
    			        pToC.getX(), pToC.getZ())));
    			p.teleport(spawnLocation, TeleportCause.PLUGIN);
    			i++;
			} else {
				p.sendMessage("Sorry, no room");
			}
		}
	}

	private void _setBarriers(Material m, byte data) {
		for (Location spawn : _spawns) {
			// Fix old layout
			Block base = spawn.getBlock();
			base.setType(Material.IRON_BLOCK);

			Block lower = base.getRelative(BlockFace.UP);
			lower.getRelative(BlockFace.NORTH).setType(m);
			lower.getRelative(BlockFace.EAST).setType(m);
			lower.getRelative(BlockFace.SOUTH).setType(m);
			lower.getRelative(BlockFace.WEST).setType(m);

			Block upper = lower.getRelative(BlockFace.UP);
			upper.getRelative(BlockFace.NORTH).setType(m);
			upper.getRelative(BlockFace.EAST).setType(m);
			upper.getRelative(BlockFace.SOUTH).setType(m);
			upper.getRelative(BlockFace.WEST).setType(m);
			
			upper = lower.getRelative(BlockFace.UP);
			upper.getRelative(BlockFace.NORTH).setType(m);
			upper.getRelative(BlockFace.EAST).setType(m);
			upper.getRelative(BlockFace.SOUTH).setType(m);
			upper.getRelative(BlockFace.WEST).setType(m);
		}
	}

	public void raiseBarriers() {
		_setBarriers(Material.GLASS, (byte) 0);
	}

	public void lowerBarriers() {
		_setBarriers(Material.AIR, (byte) 0);
	}
}
