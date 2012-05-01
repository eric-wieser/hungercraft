package tk.ericwieser.hungercraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import tk.ericwieser.hungercraft.game.GameJoinedEvent;
import tk.ericwieser.hungercraft.game.GameLeftEvent;

public class Game implements Listener {
	public Spectators spectators;
	public Tributes tributes;
	public Set<Player> allPlayers = new HashSet<>();
	public Location center;
	public double radius;
	public SpawnManager spawns;
	public Countdown countdown;
	
	private Plugin _plugin;
	
	private Map<Player, Game> gameEnterRequests = new HashMap<>();
	
	private void tryFireLeftEvent(Player p) {
		if(allPlayers.contains(p)) {
			GameLeftEvent e = new GameLeftEvent(this, p);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
	private void tryFireJoinedEvent(Player p) {
		if(allPlayers.contains(p)) {
			GameJoinedEvent e = new GameJoinedEvent(this, p);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
	
	@EventHandler
	public void playerMoved(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		//Moved in another world? Left the game.
		if(p.getWorld() != center.getWorld()) {
			tryFireLeftEvent(p);
		}
			
		boolean before = center.distance(event.getFrom()) < radius;
		boolean after = center.distance(event.getTo()) < radius;
		
		//Just moved inside the arena?
		if(!before && after) {
			tryFireJoinedEvent(p);
		}
		//Just moved outside the arena?
		else if(after && !before) {
			tryFireLeftEvent(p);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void gameLeft(GameLeftEvent event) {
		Player p = event.getPlayer();
		tributes.remove(p);
		spectators.remove(p);
		allPlayers.remove(p);
	}
	public Game(Plugin plugin, Location center) {
		_plugin = plugin;
		
	}
}
