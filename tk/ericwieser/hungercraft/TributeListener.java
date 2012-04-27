package tk.ericwieser.hungercraft;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class TributeListener implements Listener {
	Set<Player> _tributes;
	
	public TributeListener(Set<Player> tributes) {
		_tributes = tributes;
	}

	@EventHandler
	public void playerDied(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if(_tributes.contains(p)) {
			Bukkit.broadcastMessage("*Cannon Fire*");
			
			for(Player other : _tributes) {
				Location loc = other.getLocation();
				loc.setY(loc.getY()+10);
				loc.getWorld().createExplosion(loc, 0);
			}
    		
    		if(_tributes.size() == 1) {
    			Bukkit.broadcastMessage(_tributes.iterator().next().getDisplayName() + " is the victor!");
    		}
		}
	}
}
