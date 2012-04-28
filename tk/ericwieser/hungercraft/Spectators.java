package tk.ericwieser.hungercraft;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("serial")
public class Spectators extends HashSet<Player> implements Listener {
	private Set<Player> _justSpawned;

	@EventHandler
	public void itemPickedUp(PlayerPickupItemEvent event) {
		if (contains(event.getPlayer())) event.setCancelled(true);
	}

	/**Prevent fire being extinguished*/
	@EventHandler
	public void interacted(PlayerInteractEvent event) {
		if (contains(event.getPlayer()))
			if (event.getAction() == Action.LEFT_CLICK_AIR
					&& event.getClickedBlock().getType() == Material.FIRE)
				event.setCancelled(true);
	}

	@EventHandler
	public void itemMoved(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (contains(p)) {
			if (event.getInventory().getType() == InventoryType.PLAYER) return;
			if (event.getInventory().getType() == InventoryType.CRAFTING) return;
			p.sendMessage(ChatColor.RED
			        + "Spectators cannot use chests, furnaces, or similar");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockDamaged(BlockDamageEvent event) {
		Player p = event.getPlayer();
		if (contains(p)) {
			p.sendMessage(ChatColor.RED + "Only tributes may break blocks");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerDamaged(EntityDamageEvent event) {
		// Handle spectators getting hurt
		Entity p = event.getEntity();
		if (p instanceof Player && contains((Player) p)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void attackedSomeone(EntityDamageByEntityEvent event) {
		Entity d = event.getDamager();
		if (d instanceof Player && contains((Player) d)) {
			((Player) d).sendMessage(ChatColor.RED
			        + "Hey! Spectators can't attack.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerSpawned(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (contains(p)) {
			_justSpawned.add(p);
		}

	}

	@EventHandler
	public void playerMoved(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (contains(p)) {
			if (_justSpawned.remove(p)) {;
				p.setAllowFlight(true);
				p.setFlying(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				p.sendMessage("Flying enabled!");
			}
		}
	}

	@Override
	public boolean add(Player p) {
		_justSpawned.add(p);
		return super.add(p);
	}

	@Override
	public boolean remove(Object o) {
		_justSpawned.remove(o);
		if(o instanceof Player) {
			Player p = (Player) o;
			p.removePotionEffect(PotionEffectType.SPEED);
			p.setAllowFlight(false);
			p.setFlying(false);
			p.setGameMode(GameMode.SURVIVAL);
		}
		return super.remove(o);
	}

	public Spectators() {
		_justSpawned = new HashSet<Player>();
	}

}
