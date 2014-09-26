package mc.Mitchellbrine.traitorsAndMiners;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TraitorsAndMiners extends JavaPlugin implements Listener{
	
	Random random = new Random();
	
	public ArrayList<UUID> players = new ArrayList<UUID>();
	
	public ArrayList<UUID> traitors = new ArrayList<UUID>();
	public ArrayList<UUID> detective = new ArrayList<UUID>();
	public ArrayList<UUID> innocents = new ArrayList<UUID>();
	
	public Location lobby;
	public Location spawn;
	public Location specSpawn;
	
	public static TraitorsAndMiners instance;
	
	@Override
	public void onLoad() {
		FileHelper.deleteDir(new File("world/"));
		try {
		FileHelper.unzip(new File("world_tam.zip"), new File("world/"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		lobby = new Location(getServer().getWorld(getServer().getWorld("world").getUID()), 50, 7, 0);
		spawn = new Location(getServer().getWorld(getServer().getWorld("world").getUID()), 0, 7, 0);
		specSpawn = new Location(getServer().getWorld(getServer().getWorld("world").getUID()), 0, 7, 0);
	}
	
	@EventHandler
	public void playerDeath(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
		Player player = (Player)event.getEntity();
		if (event.getDamage() > player.getHealth()) {
			player.teleport(specSpawn);
			player.setGameMode(GameMode.ADVENTURE);
			player.setCanPickupItems(false);
			player.setAllowFlight(true);
			player.setMaximumNoDamageTicks(10000);
			player.setHealth(20.0D);
			for (UUID uuidI : innocents) {
				getServer().getPlayer(uuidI).hidePlayer(player);
			}
			for (UUID uuidT : traitors) {
				getServer().getPlayer(uuidT).hidePlayer(player);
			}
			for (UUID uuidD : detective) {
				getServer().getPlayer(uuidD).hidePlayer(player);
			}
			for (int i = 0; i < innocents.size();i++) {
			if (innocents.get(i).equals(player.getUniqueId())) {
				Block newBlock = player.getWorld().getBlockAt(player.getLocation());
				newBlock.setType(Material.SIGN);
				
				if (newBlock instanceof Sign) {
					((Sign) newBlock).setLine(0, player.getDisplayName());
					((Sign) newBlock).setLine(1, ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Innocent");
				}
				innocents.remove(player.getUniqueId());
				break;
			}
		}
		for (int i = 0; i < traitors.size();i++) {
			if (traitors.get(i).equals(player.getUniqueId())) {
				Block newBlock = player.getWorld().getBlockAt(player.getLocation());
				newBlock.setType(Material.SIGN);
				
				if (newBlock instanceof Sign) {
					((Sign) newBlock).setLine(0, player.getDisplayName());
					((Sign) newBlock).setLine(1, ChatColor.DARK_RED + "" + ChatColor.UNDERLINE + "Traitor");
				}
				traitors.remove(player.getUniqueId());
				break;
			}
		}
		for (int i = 0; i < detective.size();i++) {
			if (detective.get(i).equals(player.getUniqueId())) {
				Block newBlock = player.getWorld().getBlockAt(player.getLocation());
				newBlock.setType(Material.SIGN);
				
				if (newBlock instanceof Sign) {
					((Sign) newBlock).setLine(0, player.getDisplayName());
					((Sign) newBlock).setLine(1, ChatColor.DARK_BLUE + "" + ChatColor.UNDERLINE + "Detective");
				}
				detective.remove(player.getUniqueId());
				break;
			}
		}
    	if (traitors.isEmpty()) {
    		endGame("innocents");
    	} else if (innocents.isEmpty() && detective.isEmpty()) {
    		endGame("traitors");
    	}
    	event.setCancelled(true);
		}
		}
	}
	
	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
		Entity victim = event.getEntity();
		Entity damager = event.getDamager();
		
		if (victim instanceof Player && damager instanceof Player) {
			Player player = (Player) damager;
			if (!innocents.contains(player.getUniqueId()) && !traitors.contains(player.getUniqueId()) && !detective.contains(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	public int safeRandom(int maxSize) {
		int newInt = 0;
		do {
			newInt = random.nextInt(maxSize);
		} while (newInt == 0);
		return newInt;
	}
	
	// Game start/end mechanics
	
	public void startGame() {
		for (UUID uuid : players) {
			getServer().getPlayer(uuid).teleport(spawn);
			getServer().getPlayer(uuid).getInventory().clear();
			getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 1:00");
		}
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
            	for (UUID uuid : players) {
            		innocents.add(uuid);
            	}
            	int random = safeRandom(innocents.size());
            	detective.add(innocents.get(random));
        		getLogger().info("UUID IS DETECTIVE: " + innocents.get(random));
        		innocents.remove(random);
        		
        		int random2 = safeRandom(innocents.size());
        		traitors.add(innocents.get(random2));
        		getLogger().info("UUID IS TRAITOR: " + innocents.get(random2));
        		innocents.remove(random2);
        		
        		for (UUID uuid : players) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "The traitors have been selected!");
        		}
        		for (UUID uuid : innocents) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "You are an " + ChatColor.BOLD + "INNOCENT" + ChatColor.RESET + "" + ChatColor.GREEN + ". Try to stay alive and find the traitors");
        		}
        		for (UUID uuid : traitors) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "You are a " + ChatColor.BOLD + "TRAITOR" + ChatColor.RESET + "" + ChatColor.DARK_RED + ". Try to stay alive and kill all the innocents!");
        		}
        		for (UUID uuid : detective) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_BLUE + "You are a " + ChatColor.BOLD + "DETECTIVE" + ChatColor.RESET + "" + ChatColor.DARK_BLUE + ". Try to stay alive and find the traitors!");
        		}
            }
        }, 1200L);
        
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
        	public void run() {
        		for (UUID uuid : players) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:30");
        		}
        	}
        }, 600L);

        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
        	public void run() {
        		for (UUID uuid : players) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:15");
        		}
        	}
        }, 900L);
        

        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
        	public void run() {
        		for (UUID uuid : players) {
        			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:10");
        		}
        	}
        }, 1000L);
        
		ScheduleSetups.scheduleRoundTimer(this, scheduler);
        
	}
	
	public void endGame(String whoWon) {
		if (whoWon.equalsIgnoreCase("innocents")) {
			for (UUID uuid : players) {
    			getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The INNOCENT won!");
    			getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "The remaining innocents were: ");
    			for (UUID uuidI : innocents) {
    			getServer().getPlayer(uuid).sendMessage("" + ChatColor.GREEN + "" + ChatColor.ITALIC + getServer().getPlayer(uuidI).getDisplayName());
    			}
    			if (!detective.isEmpty()) {
        			for (UUID uuidD : detective) {
        				getServer().getPlayer(uuid).sendMessage("" + ChatColor.GREEN + "" + ChatColor.ITALIC + getServer().getPlayer(uuidD).getDisplayName());
            		}
    			}
			}
		} else if (whoWon.equalsIgnoreCase("traitors")) {
			for (UUID uuid : players) {
				getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "The TRAITORS won!");
				getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "The remaining traitors were: ");
				for (UUID uuidT : traitors) {
					getServer().getPlayer(uuid).sendMessage("" + ChatColor.DARK_RED + "" + ChatColor.ITALIC + getServer().getPlayer(uuidT).getDisplayName());
				}
			}
		} else {
			throw new IllegalArgumentException("So... who won again?");
		}
		
		for (UUID uuid : players) {
			getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 30 seconds!");
			
			getServer().getPlayer(uuid).setBedSpawnLocation(lobby, true);
			getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
			getServer().getPlayer(uuid).setCanPickupItems(false);
			getServer().getPlayer(uuid).setAllowFlight(true);
			getServer().getPlayer(uuid).getInventory().clear();
			getServer().getPlayer(uuid).setMaximumNoDamageTicks(10000);
		}
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : players) {
					getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 20 seconds!");
				}
			}
		}, 200L);
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : players) {
					getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 10 seconds!");
				}
			}
		}, 400L);		
		
		scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
        		for (Player player : getServer().getWorld("world").getPlayers()) {
        			player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "The server has won!");
        			player.teleport(lobby);
        			players.remove(player.getUniqueId());
        			
        			if (traitors.contains(player.getUniqueId())) {
        				traitors.remove(player.getUniqueId());
        			}
        			
        			if (innocents.contains(player.getUniqueId())) {
        				innocents.remove(player.getUniqueId());
        			}
        			
        			if (detective.contains(player.getUniqueId())) {
        				detective.remove(player.getUniqueId());
        			}
        		}
            	getServer().shutdown();
            	/*getServer().getWorld("world").getWorldFolder().delete();
        		File file = new File(getDataFolder().getParent() + "/world_tam/");
        		for (File files : file.listFiles()) {
        			try {
        			InputStream is = files.toURI().toURL().openStream();
        			FileLoading.download(is, 1000, new File(getDataFolder().getParent() + "/world/" + files.getName().lastIndexOf(File.separatorChar)));
        			} catch (Exception ex) {
        				ex.printStackTrace();
        			}
        		} */
            }
        }, 600L);
		
		ScheduleSetups.scheduleEndTime(this, scheduler);
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("startTAM")) {
			for (Player player : getServer().getWorld("world").getPlayers()) {
				players.add(player.getUniqueId());
				player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 10 seconds...");
			}
			
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	startGame();
	            }
	        }, 200L);
	        ScheduleSetups.scheduleStartTime(this, scheduler);
			return true;
		}
		return false;
	}
	

	@EventHandler
    public void disableReload1(ServerCommandEvent event) {
        String command = event.getCommand().split(" ")[0];
        if (command.equalsIgnoreCase("reload")){
            event.setCommand("");
        }
    }


	@EventHandler
    public void disableReload2(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        if (command.equalsIgnoreCase("reload")){
            event.setCancelled(true);
        }
    }

}
