package mc.Mitchellbrine.traitorsAndMiners;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ScheduleSetups {

	public static void scheduleStartTime(final Plugin plugin, BukkitScheduler scheduler) {
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 9 seconds...");
				}
			}
		}, 20L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 8 seconds...");
				}
			}
		}, 40L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 7 seconds...");
				}
			}
		}, 60L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 6 seconds...");
				}
			}
		}, 80L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 5 seconds...");
				}
			}
		}, 100L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 4 seconds...");
				}
			}
		}, 120L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 3 seconds...");
				}
			}
		}, 140L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 2 seconds...");
				}
			}
		}, 160L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 1 second...");
				}
			}
		}, 180L);
	}
	
	public static void scheduleRoundTimer(final Plugin plugin, BukkitScheduler scheduler) {
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:09");
				}
			}
		}, 420L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:08");
				}
			}
		}, 440L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:07");
				}
			}
		}, 460L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:06");
				}
			}
		}, 480L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:05");
				}
			}
		}, 500L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:04");
				}
			}
		}, 520L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:03");
				}
			}
		}, 540L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:02");
				}
			}
		}, 560L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:01");
				}
			}
		}, 580L);
	}
	
	public static void scheduleEndTime(final Plugin plugin, BukkitScheduler scheduler) {
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 9 seconds!");
				}
			}
		}, 420L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 8 seconds!");
				}
			}
		}, 440L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 7 seconds!");
				}
			}
		}, 460L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 6 seconds!");
				}
			}
		}, 480L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 5 seconds!");
				}
			}
		}, 500L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 4 seconds!");
				}
			}
		}, 520L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 3 seconds!");
				}
			}
		}, 540L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 2 seconds!");
				}
			}
		}, 560L);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player player = plugin.getServer().getPlayer(uuid);
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 1 second!");
				}
			}
		}, 580L);
	}
	
}
