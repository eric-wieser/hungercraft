package tk.ericwieser.hungercraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.ericwieser.hungercraft.tribute.TributeFallenEvent;

@SuppressWarnings("serial")
public class Tributes extends HashSet<Player> implements Listener {
	private HungerCraftPlugin _plugin;
	private Map<Player, Integer> _timeouts;
	public static final int TIMEOUT = 30 * 20; //20 ticks per second
	public Tributes(HungerCraftPlugin plugin) {
	    _plugin = plugin;
	    _timeouts = new HashMap<>();
    }
	
	
	@EventHandler
	public void playerLeaves(PlayerQuitEvent event) {
		final Player p = event.getPlayer();
		if(contains(p)) {
    		int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable() {
    			@Override public void run() {
    				remove(p);
    			}
    		}, TIMEOUT);
    		_timeouts.put(p, taskId);
		}
	}

	/** Cancel timeouts */
	@EventHandler
	public void playerLeaves(PlayerLoginEvent event) {
		final Player p = event.getPlayer();
		if(_timeouts.containsKey(p)) {
			int taskId = _timeouts.get(p);
    		Bukkit.getScheduler().cancelTask(taskId);
		}
	}
	
	@EventHandler
    public void blockDamaged(BlockDamageEvent event) {
		Player p = event.getPlayer();
		if(contains(p)) {
            Material type = event.getBlock().getType();
            Material[] allowed = new Material[] {
            		Material.BROWN_MUSHROOM,
            		Material.RED_MUSHROOM,
            		Material.LEAVES,
            		Material.RED_ROSE,
            		Material.YELLOW_FLOWER,
            		Material.LONG_GRASS
            };
            for(Material m : allowed) {
            	if(type == m)
            		return;
            }
            p.sendMessage(ChatColor.RED + "Only leaves and mushrooms may be broken");
    		event.setCancelled(true);
		}
    }
	
	/**Remove a player from the list of tributes. Fires a TributeFallenEvent */
	public boolean remove(Object p) {
		boolean contained = super.remove(p);
		if(contained) {
			_timeouts.remove(p);
			if(p instanceof Player) {
    			TributeFallenEvent e = new TributeFallenEvent(this, (Player) p);
    			Bukkit.getServer().broadcastMessage("DEAD");
    			Bukkit.getServer().getPluginManager().callEvent(e);
			}
		}
		return contained;
	}

	public boolean add(Player p) {
		p.setAllowFlight(false);
		p.setFoodLevel(20);
		p.setHealth(20);
		p.getInventory().clear();
		return super.add(p);
	};
	
}
