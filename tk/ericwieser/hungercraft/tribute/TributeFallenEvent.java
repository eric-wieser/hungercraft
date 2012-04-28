package tk.ericwieser.hungercraft.tribute;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import tk.ericwieser.hungercraft.Tributes;

public class TributeFallenEvent extends Event {
	Tributes others;
	Player tribute;
	
	public TributeFallenEvent(Tributes o, Player p) {
		tribute = p;
		others = o;
	}
	
	public Player getTribute() { return tribute; }
	public Tributes getTributes() { return others; }

	private static HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }

}
