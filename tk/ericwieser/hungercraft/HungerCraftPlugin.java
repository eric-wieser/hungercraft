package tk.ericwieser.hungercraft;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class HungerCraftPlugin extends JavaPlugin implements Listener {
	Logger log;
	SpawnManager spawnManager;
	Location center;
	
	Set<Player> spectators = new HashSet<>();
	Set<Player> tributes = new HashSet<>();
	
	@EventHandler
    public void blockDamaged(BlockDamageEvent event) {
		if(spectators.contains(event.getPlayer())) {
			event.getPlayer().sendMessage(ChatColor.RED + "Only tributes may break blocks");
    		event.setCancelled(true);
		}
		else {
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
            event.getPlayer().sendMessage(ChatColor.RED + "Only leaves and mushrooms may be broken");
    		event.setCancelled(true);
		}
    }
	
	public void makeSpectator(Player p) {
		spectators.add(p);
		tributes.remove(p);
		p.setAllowFlight(true);
	}
	
	public void makeTribute(Player p) {
		tributes.add(p);
		spectators.remove(p);
		p.setAllowFlight(false);
	}
	
	@EventHandler
	public void playerEntered(PlayerLoginEvent event) {
		makeSpectator(event.getPlayer());
	}
	
	@EventHandler
	public void playerSpawned(PlayerRespawnEvent event) {
		if(spectators.contains(event.getPlayer()))
			event.getPlayer().setFlying(true);
			
	}
	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if(!spectators.contains(p)) {
    		makeSpectator(p);
    		getServer().broadcastMessage("*Cannon Fire*");
    		
    		if(tributes.size() == 1) {
    			getServer().broadcastMessage(tributes.iterator().next().getDisplayName() + " is the victor!");
    		}
		}
	}
	
	@EventHandler
	public void itemPickedUp(PlayerPickupItemEvent event) {
		if(spectators.contains(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void itemMoved(InventoryClickEvent event) {
		if(event.getInventory().getType() == InventoryType.PLAYER)
			return;
		Player p = (Player) event.getWhoClicked();
		if(spectators.contains(p)) {
			p.sendMessage(ChatColor.RED + "Spectators cannot steal from chests");
			event.setCancelled(true);
		}
	}
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		Object c = this.getConfig().get("cornucopia.center");
		if (c != null && c instanceof Vector) {
			center = ((Vector) c).toLocation(Bukkit.getWorld("world"));
		}
		if(center != null) {
			spawnManager = new SpawnManager(center);
		}
			
		log = this.getLogger();
		log.info("Your plugin has been enabled!");
		
		getCommand("ignite").setExecutor(new IgniteCommandExecutor(this));
		getCommand("set-center").setExecutor(new SetCenterCommandExecutor(this));
				
		getCommand("spawns").setExecutor(new SpawnsCommandExecutor(this));
		GameCommand g = new GameCommand(this);
		getCommand("game").setExecutor(g);
		getCommand("status").setExecutor(g);
	}
 
	public void onDisable(){
		log.info("Your plugin has been disabled.");
	}
}
