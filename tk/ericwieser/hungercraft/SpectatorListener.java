package tk.ericwieser.hungercraft;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpectatorListener implements Listener {
	private Set<Player> _spectators;
	private Set<Player> _justSpawned;

	@EventHandler
	public void itemPickedUp(PlayerPickupItemEvent event) {
		if(_spectators.contains(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void itemMoved(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(_spectators.contains(p)) {
			if(event.getInventory().getType() == InventoryType.PLAYER) return;
			p.sendMessage(ChatColor.RED + "Spectators cannot use chests, furnaces, or similar");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void blockDamaged(BlockDamageEvent event) {
		Player p = event.getPlayer();
		if(_spectators.contains(p)) {
			p.sendMessage(ChatColor.RED + "Only tributes may break blocks");
    		event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void playerDamaged(EntityDamageEvent event) {
		//Handle spectators getting hurt
		Entity p = event.getEntity();
		if(p instanceof Player && _spectators.contains((Player) p)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void attackedSomeone(EntityDamageByEntityEvent event) {
		Entity d = event.getDamager();
		if(d instanceof Player && _spectators.contains((Player) d)) {
			((Player) d).sendMessage(ChatColor.RED + "Hey! Spectators can't attack.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerSpawned(PlayerRespawnEvent event) {
		if(_spectators.contains(event.getPlayer())) {
			_justSpawned.add(event.getPlayer());
			event.getPlayer().setFlying(true);
		}
			
	}
	
	@EventHandler
	public void playerMoved(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if(_spectators.contains(p)) {
			if(_justSpawned.contains(p)) {
				p.setAllowFlight(true);
				p.setFlying(true);
				p.sendMessage("Flying enabled!");
				_justSpawned.remove(p);
			}
		}
	}
	
	public SpectatorListener(Set<Player> spectators) {
		_justSpawned = new HashSet<Player>();
		_spectators = spectators;
	}

}
