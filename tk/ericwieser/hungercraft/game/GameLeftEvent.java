package tk.ericwieser.hungercraft.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import tk.ericwieser.hungercraft.Game;

public class GameLeftEvent extends Event {
	private Game game;
	private Player player;

	public Game getGame() {
		return game;
	}
	public Player getPlayer() {
	    return player;
    }

	public GameLeftEvent(Game g, Player p) {
		game = g;
		player = p;
	}

	private static HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
