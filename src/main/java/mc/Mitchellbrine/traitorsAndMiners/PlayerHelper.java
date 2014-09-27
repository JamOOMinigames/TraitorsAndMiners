package mc.Mitchellbrine.traitorsAndMiners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerHelper {
	 public static Player getNearest(Player p, Double range) {
		 double distance = Double.POSITIVE_INFINITY;
		 Player target = null;
		 for (Entity e : p.getNearbyEntities(range, range, range)) {
		 if (!(e instanceof Player))
		 continue;
		 if(e == p) continue;
		 double distanceto = p.getLocation().distance(e.getLocation());
		 if (distanceto > distance)
		 continue;
		 distance = distanceto;
		 target = (Player) e;
		 }
		 return target;
		 }
	 
	 public static int getDistance(Player player, Player player2) {
		 double distance = player.getLocation().distance(player2.getLocation());
		 
		 return (int)Math.floor(distance);
	 }
}
