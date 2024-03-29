package tk.ericwieser.hungercraft.commands;

import java.util.Iterator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.ericwieser.hungercraft.Countdown;
import tk.ericwieser.hungercraft.HungerCraftPlugin;

public class GameCommand implements CommandExecutor {

	private HungerCraftPlugin _plugin;

	public GameCommand(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
	}

	String join(String[] array, String separator) {
		String s = "";
		for (int i = 0; i < array.length; i++) {
			if (i != 0) s += separator;
			s += array[i];
		}
		return s;
	}

	String join(String[] array) {
		return join(array, ", ");
	}

	String[] wrapList(String[] names, int maxWidth) {
		int columns = 1;
		String[][] bestGrid = null;
		int[] bestLengths = null;

		while (true) {
			int rows = (names.length + (columns - 1)) / columns;
			String[][] test = new String[rows][columns];
			int[] lengths = new int[columns];
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				lengths[i % columns] =
				                       Math.max(lengths[i % columns],
				                               name.length());
				test[i / columns][i % columns] = name;
			}

			// Calculate total width, with two space padding
			int totalWidth = 2 * (columns - 1);
			for (int i = 0; i < columns; i++)
				totalWidth += lengths[i];

			// Still acceptable wrapping?
			if (bestGrid == null || totalWidth <= maxWidth) {
				bestGrid = test;
				bestLengths = lengths;
			} else break;
			if (rows == 1) break;
			columns++;
		}

		String lines[] = new String[bestGrid.length];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = "";
			for (int j = 0; j < bestLengths.length; j++) {
				// Pad the item
				String item = bestGrid[i][j];
				if (item == null) break;
				int desiredLength = bestLengths[j];
				while (item.length() < desiredLength)
					item += " ";

				// Add to line
				if (j > 0) lines[i] += "  ";
				lines[i] += item;
			}
		}
		return lines;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {

		if (label.equals("status")) {
			// Show dead tributes
			int numDead = _plugin.spectators.size();
			sender.sendMessage("The following " + numDead + " tribute"
			        + (numDead == 1 ? "" : "s") + " have fallen:");
			String[] dead = new String[_plugin.spectators.size()];
			int i = 0;
			for (Player spectator : _plugin.spectators) {
				dead[i++] = spectator.getDisplayName();
			}
			sender.sendMessage(join(dead));

			// Show living tributes
			int numAlive = _plugin.tributes.size();
			sender.sendMessage("Surviving tributes (" + numAlive + "):");
			String[] alive = new String[numAlive];
			int j = 0;
			for (Player tribute : _plugin.tributes) {
				alive[j++] = tribute.getDisplayName();
			}
			sender.sendMessage(join(alive));
			return true;
		}

		if (args.length != 1) { return false; }

		String action = args[0];
		if (action.equals("setup")) {
			
			_plugin.spawnManager.raiseBarriers();
			_plugin.spawnManager.assignPlayers(_plugin.spectators);
			
			Iterator<Player> it = _plugin.spectators.iterator();
			while (it.hasNext()) {
				_plugin.tributes.add(it.next());
				it.remove();
			}
			_plugin.spectators.clear();

			return true;
		} else if (action.equals("start")) {
			_plugin.getServer().broadcastMessage("Countdown in XP bar");
			
			Countdown c = new Countdown(10) {
		        public void counted(int x) {
					//_plugin.getServer().broadcastMessage("" + x);
					for(Player p : _plugin.tributes) {
						p.setLevel(x);
					}
		        }
		        public void countedTenths(float x) {
		        	for(Player p : _plugin.tributes) {
						p.setExp(x / from);
					}
		        }
		        public void done() {
					_plugin.getServer().broadcastMessage("Let the games begin!");
					_plugin.spawnManager.lowerBarriers();
					for(Player p : _plugin.tributes) {
						p.setLevel(0);
					}
		        }
	        };
	        
	        c.start(_plugin);

			return true;
		}

		return false;
	}

}
