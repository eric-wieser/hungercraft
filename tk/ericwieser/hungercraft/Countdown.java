package tk.ericwieser.hungercraft;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class Countdown implements Runnable {
	private int at;
	protected int from;
	private int taskId = -1;
	private BukkitScheduler _s = null;
	public Countdown(int _from) {
	    from = _from;
	    reset();
    }
	public void reset() {
		at = from * 10;
	}
	@Override
	public void run() {
		if(at == 0) {
			countedTenths(0);
			done();
			stop();
		}
		else {
			if(at % 10 == 0) {
    			counted(at / 10);
    			countedTenths((float) at / 10f);
    		}
			at--;
		}
	}
	public abstract void counted(int x);
	public void countedTenths(float x) {};
	public void done() {};
	
	public void stop() {
		_s.cancelTask(taskId);
	}
	
	public void start(Plugin p) {
		_s = p.getServer().getScheduler();
		taskId = _s.scheduleSyncRepeatingTask(p, this, 0, 2);
	}
}
