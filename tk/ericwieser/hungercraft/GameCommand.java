package tk.ericwieser.hungercraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommand implements CommandExecutor {

	private HungerCraftPlugin _plugin;

	public GameCommand(HungerCraftPlugin hungerCraftPlugin) {
		_plugin = hungerCraftPlugin;
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
				lengths[i % columns] = Math.max(lengths[i % columns], name.length());
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
			sender.sendMessage(wrapList(dead, 40));

			// Show living tributes
			int numAlive = _plugin.tributes.size();
			sender.sendMessage("Surviving tributes ("+numAlive+"):");
			String[] alive = new String[numAlive];
			int j = 0;
			for (Player tribute : _plugin.tributes) {
				alive[j++] = tribute.getDisplayName();
			}
			sender.sendMessage(wrapList(alive, 40));
			return true;
		}

		if (args.length != 1) { return false; }

		String action = args[0];
		if (action.equals("setup")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				_plugin.makeTribute(p);
			}
			_plugin.spawnManager.raiseBarriers();
			_plugin.spawnManager.assignPlayers();

			return true;
		}

		return false;
	}

}
