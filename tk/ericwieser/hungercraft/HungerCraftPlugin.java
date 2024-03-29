package tk.ericwieser.hungercraft;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import tk.ericwieser.hungercraft.commands.GameCommand;
import tk.ericwieser.hungercraft.commands.Ignite;
import tk.ericwieser.hungercraft.commands.Plant;
import tk.ericwieser.hungercraft.commands.SetCenter;
import tk.ericwieser.hungercraft.commands.Spawns;
import tk.ericwieser.hungercraft.tribute.TributeFallenEvent;
import tk.ericwieser.hungercraft.tribute.TributeFallenReason;

public class HungerCraftPlugin extends JavaPlugin implements Listener {
	Logger log;
	public SpawnManager spawnManager;
	public Location center;
	
	public Spectators spectators = new Spectators();
	public Tributes tributes = new Tributes(this);
	
	@EventHandler
	public void tributeFallen(TributeFallenEvent event) {
		if(tributes == event.getTributes()) {
			Player p = event.getTribute();
			getLogger().info(p.getDisplayName() + " has fallen ("+event.getReason()+")");
			
			//Explosion for non-administration movements
			if(event.getReason() != TributeFallenReason.OTHER) {
    			getServer().broadcastMessage(ChatColor.DARK_PURPLE+"*Cannon Fire*");
    			
    			for(Player other : tributes) {
    				Location loc = other.getLocation();
    				loc.setY(loc.getY()+10);
    				loc.getWorld().createExplosion(loc, 0);
    			}
			}
    		if(tributes.size() == 1) {
    			Bukkit.broadcastMessage(tributes.iterator().next().getDisplayName() + " is the victor!");
    		}
    		
    		spectators.add(p);
		}
	}
	
	@EventHandler
	public void playerEntered(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if(!tributes.contains(p)) {
			spectators.add(p);
		}
	}
	
	/**prevent explosions near the center*/
	@EventHandler
	public void explosion(EntityExplodeEvent event) {
		Location at = event.getLocation();
		if(at.distance(center) < 30)
			event.blockList().clear();
	}
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(spectators, this);
		getServer().getPluginManager().registerEvents(tributes, this);
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			spectators.add(p);
		}
		
		Object c = this.getConfig().get("cornucopia.center");
		String world = this.getConfig().getString("cornucopia.world");
		if (c != null && c instanceof Vector) {
			center = ((Vector) c).toLocation(Bukkit.getWorld(world));
		}
		if(center != null) {
			spawnManager = new SpawnManager(center);
		}
			
		log = this.getLogger();
		log.info("Your plugin has been enabled!");

		getCommand("ignite").setExecutor(new Ignite(this));
		getCommand("plant").setExecutor(new Plant(this));
		getCommand("set-center").setExecutor(new SetCenter(this));
		getCommand("move").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String label,
			        String[] args) {
				Player p = null;
				String group = null;
				if(args.length == 1 && sender instanceof Player) {
					p = (Player) sender;
					group = args[0];
				} else if (args.length == 2) {
					p = Bukkit.getPlayer(args[0]);
					group = args[1];
				}
				
				if(p != null) {
					if(group.equals("tribute")) {
						spectators.remove(p);
						tributes.add(p);
						return true;
					}
					else if(group.equals("spectator")) {
						tributes.remove(p);
						spectators.add(p);
						return true;
					}
					else if(group.equals("none")){
						tributes.remove(p);
						spectators.remove(p);
						return true;
					}
					return false;
				}
				return false;
			}
		});
				
		getCommand("spawns").setExecutor(new Spawns(this));
		GameCommand g = new GameCommand(this);
		getCommand("game").setExecutor(g);
		getCommand("status").setExecutor(g);
	}
 
	public void onDisable(){
		log.info("Your plugin has been disabled.");
	}
}
