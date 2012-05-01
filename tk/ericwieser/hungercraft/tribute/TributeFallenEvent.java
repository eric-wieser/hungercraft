package tk.ericwieser.hungercraft.tribute;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import tk.ericwieser.hungercraft.Tributes;

public class TributeFallenEvent extends Event {
	Tributes others;
	Player tribute;
	TributeFallenReason reason;
	
	public TributeFallenEvent(Tributes o, Player p, TributeFallenReason r) {
		tribute = p;
		others = o;
		reason = r;
	}

	public TributeFallenReason getReason() { return reason; }
	public Player getTribute() { return tribute; }
	public Tributes getTributes() { return others; }

	private static HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }

}
