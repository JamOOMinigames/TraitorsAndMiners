package mc.Mitchellbrine.traitorsAndMiners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import mc.Mitchellbrine.traitorsAndMiners.map.Map;
import mc.Mitchellbrine.traitorsAndMiners.map.MapManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

@SuppressWarnings({"unused","deprecation"})
public class TraitorsAndMiners extends JavaPlugin implements Listener {

    public static Random randomW = new Random(System.currentTimeMillis());
    Random random;

    public int maxPlayers = 24;
    public static int gameState;

    public static String map;
    
    public ArrayList<UUID> players = new ArrayList<>();

    public ArrayList<UUID> traitors = new ArrayList<>();
    public ArrayList<UUID> detective = new ArrayList<>();
    public ArrayList<UUID> innocents = new ArrayList<>();
        
    public HashMap<UUID,Integer> points = new HashMap<>();

    public HashMap<UUID, Integer> karma = new HashMap<>();
    
    public Location lobby;
    public Location spawn;

    public static TraitorsAndMiners instance;

    public Map tamMap;

    @Override
    public void onLoad() {
        instance = this;

        // Map Loading Things

        tamMap = new Map("Western Mines","MCG","world_tam1",new Location(getServer().getWorld("world"),50,65, 0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);
        
        Map mansion = new Map("Mansion","Mitchellbrine","world_tam",new Location(getServer().getWorld("world"),50,65, 0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);
        Map mines = new Map("Western Mines","MCG","world_tam1",new Location(getServer().getWorld("world"),50,65, 0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);
        
        Map rainbow = new Map("Rainbow","kkaylium","world_rainbow",new Location(getServer().getWorld("world"),50,65, 0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);

        Map ruins = new Map("Ruins","TeamDoorknob","world_ruins",new Location(getServer().getWorld("world"),50,65,0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);
        
        Map fortress = new Map("Sky Fortress","TeamDoorknob","world_fortress",new Location(getServer().getWorld("world"),50,65,0),new Location(getServer().getWorld("world"),0,65,0)).setMaxPlayers(24);
        
        MapManager.registerMap(mansion);
        MapManager.registerMap(mines);
        MapManager.registerMap(rainbow);
        MapManager.registerMap(ruins);
        MapManager.registerMap(fortress);
        
        MapManager.resetMap();
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new TraitorWeapons(), this);
        
        gameState = 0;
        random = MathHelper.random(getServer().getWorld("world").getTime());
        Bukkit.getMessenger().registerOutgoingPluginChannel(TraitorsAndMiners.instance, "BungeeCord");
        
        if (tamMap.getLobby() != null) {
        	lobby = tamMap.getLobby().clone();
        }
        
        if (tamMap.getSpawn() != null) {
        	spawn = tamMap.getSpawn().clone();
        }
        
        lobby = new Location(getServer().getWorld("world"),50,65,0);
        
        spawn = new Location(getServer().getWorld("world"),0,65,0);
        
        ItemStackHelper.init();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doFireTick false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doMobLoot false");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule mobGriefing false");

        try {
        File karmaFile = new File(this.getDataFolder(),"karmaPoints.txt");
        
        if (karmaFile.exists()) {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(karmaFile)));
        	String s;
        	
        	while ((s = reader.readLine()) != null) {
        		if (s.startsWith("--")) continue;
        		if (s.contains(":")) {
        			this.karma.put(UUID.fromString(s.substring(0, s.indexOf(":") - 1)), Integer.parseInt(s.substring(s.indexOf(":") + 2)));
        		}
        	}
        	
        	reader.close();
        	
        }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
    }
    
    @EventHandler
    public void deathOfAPlayerman(PlayerDeathEvent event) {
    	Player player = event.getEntity();
        	if (gameState == 2) {
    		if (players.contains(player.getUniqueId())) {
            event.setDeathMessage("");
            player.setCanPickupItems(false);
            player.setGameMode(GameMode.SPECTATOR);
            List<ItemStack> newDrops = new ArrayList<ItemStack>();
            for (ItemStack stack : event.getDrops()) {
            	if (stack.getType() == Material.BOOK || stack.getType() == Material.COMPASS) newDrops.add(stack);
            }
            
            for (ItemStack stack1 : newDrops) {
            	event.getDrops().remove(stack1);
            }
            player.setHealth(20.0D);
            player.getInventory().clear();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED!");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Spectate to see who wins!");
            TitleManager.sendHeaderAndFooter(event.getEntity(), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.LIGHT_PURPLE + "You are " + ChatColor.BOLD + "DEAD");
    		player.setDisplayName(ChatColor.DARK_RED + "*DEAD* " + ChatColor.RESET +  player.getName());
            for (UUID uuidI : innocents) {
                getServer().getPlayer(uuidI).hidePlayer(player);
            }
            for (UUID uuidT : traitors) {
                getServer().getPlayer(uuidT).hidePlayer(player);
				Player playerT = getServer().getPlayer(uuidT);
				int newPoints = points.get(uuidT) + 2;
				if (detective.contains(player.getUniqueId())) {
					newPoints++;
				}
                points.remove(uuidT);
				points.put(uuidT, newPoints);
				playerT.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "You have earned more coins! Current Coins: " + points.get(uuidT));
            }
            for (UUID uuidD : detective) {
                getServer().getPlayer(uuidD).hidePlayer(player);
            }
                            
                if (innocents.contains(player.getUniqueId())) {
                	ItemStack stack = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                    SkullMeta meta1 = (SkullMeta) stack.getItemMeta();
                	meta1.setDisplayName(ChatColor.GREEN + player.getName());
                    meta1.setOwner(player.getName());
                    stack.setItemMeta(meta1);
                	if (!detective.isEmpty()) {
                    for (UUID uuid : detective) {
                    	getServer().getPlayer(uuid).getInventory().addItem(stack);
                    }
                	}
                    if (!innocents.isEmpty()) {
                    for (UUID uuid : innocents) {
                    	getServer().getPlayer(uuid).getInventory().addItem(stack);
                    }
                    }
                    if (!traitors.isEmpty()) {
                        for (UUID uuid : traitors) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    }
                    innocents.remove(player.getUniqueId());
                }
                else if (traitors.contains(player.getUniqueId())) {
                    ItemStack stack = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                    SkullMeta meta1 = (SkullMeta) stack.getItemMeta();
                    meta1.setDisplayName(ChatColor.DARK_RED + player.getName());
                    meta1.setOwner(player.getName());
                    stack.setItemMeta(meta1);
                    if (!detective.isEmpty()) {
                        for (UUID uuid : detective) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    }
                    if (!innocents.isEmpty()) {
                        for (UUID uuid : innocents) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    }
                    traitors.remove(player.getUniqueId());
                    if (!traitors.isEmpty()) {
                        for (UUID uuid : traitors) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                            TitleManager.sendFloatingText(player, ChatColor.DARK_RED + "Your ally has fallen:", player.getName(), 0, 40, 20);
                        }
                    }
                } else if (detective.contains(player.getUniqueId())) {
                    ItemStack stack = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                    SkullMeta meta1 = (SkullMeta) stack.getItemMeta();
                    meta1.setDisplayName(ChatColor.DARK_BLUE + player.getName());
                    meta1.setOwner(player.getName());
                    stack.setItemMeta(meta1);
                    if (!detective.isEmpty()) {
                        for (UUID uuid : detective) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    }
                    if (!innocents.isEmpty()) {
                        for (UUID uuid : innocents) {
                            getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    }
                    detective.remove(player.getUniqueId());
                }
                TitleManager.sendFloatingText(player, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED", ChatColor.LIGHT_PURPLE + "There are " + (innocents.size() + traitors.size() + detective.size()) + " players remaining", 0, 60, 40);           
            if (!players.isEmpty()) {
            if (traitors.isEmpty()) {
                endGame("innocents");
            } else if (innocents.isEmpty() && detective.isEmpty()) {
                endGame("traitors");
            }
            }
        	}
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void playerDeath(EntityDamageEvent event) {
    	if (gameState == 2) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDamage() >= ((Damageable) event.getEntity()).getHealth()) {
            	if (players.contains(player.getUniqueId())) {
                player.setGameMode(GameMode.SPECTATOR);
                player.setHealth(20.0D);
                player.setCanPickupItems(false);
                for (ItemStack stack : player.getInventory().getContents()) {
                	if (stack != null && stack.getType() != Material.COMPASS && stack.getType() != Material.BOOK) {
                		Location playerLoc = player.getLocation().add(0.5, 0.5, 0.5);
                		playerLoc.getWorld().dropItem(playerLoc, stack);
                	}
                }
                player.getInventory().clear();
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED!");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Spectate to see who wins!");
                TitleManager.sendHeaderAndFooter(player, ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.LIGHT_PURPLE + "You are " + ChatColor.BOLD + "DEAD");
        		player.setDisplayName(ChatColor.DARK_RED + "*DEAD* " + ChatColor.RESET +  player.getName());

                for (UUID uuidI : innocents) {
                    getServer().getPlayer(uuidI).hidePlayer(player);
                }
                for (UUID uuidT : traitors) {
                    getServer().getPlayer(uuidT).hidePlayer(player);
    				Player playerT = getServer().getPlayer(uuidT);
    				int newPoints = points.get(uuidT) + 2;
    				if (detective.contains(player.getUniqueId())) {
    					newPoints++;
    				}
    				points.remove(uuidT);
                    points.put(uuidT, newPoints);
                    playerT.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "You have earned more coins! Current Coins: " + points.get(uuidT));
                }
                for (UUID uuidD : detective) {
                    getServer().getPlayer(uuidD).hidePlayer(player);
                }
                                
                    if (innocents.contains(player.getUniqueId())) {
                    	ItemStack stack = new ItemStack(Material.SKULL_ITEM,1);
                    	stack.setDurability((short) 3);
                    	
                    	ItemMeta meta = stack.getItemMeta();
                    	meta.setDisplayName(ChatColor.GREEN + player.getName());
                    	stack.setItemMeta(meta);
                    	if (!detective.isEmpty()) {
                        for (UUID uuid : detective) {
                        	getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    	}
                        if (!innocents.isEmpty()) {
                        for (UUID uuid : innocents) {
                        	getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                        }
                        innocents.remove(player.getUniqueId());
                    }
                    else if (traitors.contains(player.getUniqueId())) {
                    	ItemStack stack = new ItemStack(Material.SKULL_ITEM,1);
                    	stack.setDurability((short) 3);
                    	ItemMeta meta = stack.getItemMeta();
                    	meta.setDisplayName(ChatColor.DARK_RED + player.getName());
                    	stack.setItemMeta(meta);
                    	if (!detective.isEmpty()) {
                        for (UUID uuid : detective) {
                        	getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                    	}
                        if (!innocents.isEmpty()) {
                        for (UUID uuid : innocents) {
                        	getServer().getPlayer(uuid).getInventory().addItem(stack);
                        }
                        }
                        traitors.remove(player.getUniqueId());
                    } else if (detective.contains(player.getUniqueId())) {
                        ItemStack stack = new ItemStack(Material.SKULL_ITEM,1,(short) 3);
                        SkullMeta meta1 = (SkullMeta) stack.getItemMeta();
                        meta1.setDisplayName(ChatColor.DARK_BLUE + player.getName());
                        meta1.setOwner(player.getName());
                        stack.setItemMeta(meta1);
                        if (!detective.isEmpty()) {
                            for (UUID uuid : detective) {
                                getServer().getPlayer(uuid).getInventory().addItem(stack);
                            }
                        }
                        if (!innocents.isEmpty()) {
                            for (UUID uuid : innocents) {
                                getServer().getPlayer(uuid).getInventory().addItem(stack);
                            }
                        }
                        detective.remove(player.getUniqueId());
                    }
                    TitleManager.sendFloatingText(player, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED", ChatColor.LIGHT_PURPLE + "There are " + (innocents.size() + traitors.size() + detective.size()) + " players remaining", 0, 60, 40);
                    
                if (!players.isEmpty()) {
                if (traitors.isEmpty()) {
                    endGame("innocents");
                } else if (innocents.isEmpty() && detective.isEmpty()) {
                    endGame("traitors");
                }
                }
                event.setCancelled(true);
            }
            }
        }
    }
    }

    @SuppressWarnings("unused")
	@EventHandler
	public void entityDamage(EntityDamageByEntityEvent event) {
	    Entity damager = event.getDamager();
	
	    if (damager instanceof Player || damager instanceof Projectile) {
	    	Player player;
            if (damager instanceof Player) {
	    	player = (Player) damager;
            } else {
            	player = (Player)((Projectile)damager).getShooter();
            }
	        if (event.getEntity() instanceof Player) {
	            Player entity = (Player) event.getEntity();
	            if (!innocents.contains(player.getUniqueId()) && !traitors.contains(player.getUniqueId()) && !detective.contains(player.getUniqueId())) {
	                event.setCancelled(true);
	            }
	            if (!innocents.contains(entity.getUniqueId()) && !traitors.contains(entity.getUniqueId()) && !detective.contains(entity.getUniqueId())) {
	            	event.setCancelled(true);
	            }
                if (gameState == 2) {
                	double currentDamage = event.getDamage();
                	event.setDamage(currentDamage * (karma.get(player.getUniqueId()) / 1000));
                    if (event.getDamage() >= ((Damageable)entity).getHealth()) {
                    	if ((!traitors.contains(entity) && !detective.contains(entity) && innocents.contains(player)) || (traitors.contains(entity) && traitors.contains(player))) {
                    		Integer currentKarma = karma.get(player.getUniqueId());
                    		karma.remove(player.getUniqueId());
                    		karma.put(player.getUniqueId(), currentKarma - 5);
                    	}
                    	if (innocents.contains(player) && detective.contains(entity)) {
                    		Integer currentKarma = karma.get(player.getUniqueId());
                    		karma.remove(player.getUniqueId());
                    		karma.put(player.getUniqueId(), currentKarma - 20);
                    	}
                    	if (innocents.contains(player) && traitors.contains(entity)) {
                    		Integer currentKarma = karma.get(player.getUniqueId());
                    		karma.remove(player.getUniqueId());
                    		if (currentKarma == 1000) {
                    			karma.put(player.getUniqueId(), currentKarma);
                    		} else {
                    			karma.put(player.getUniqueId(), currentKarma + 5);
                    		}
                    	}
                    	player.setLevel(karma.get(player.getUniqueId()));
                    }
                }
            } else if (event.getEntity() instanceof Bat) {
	        	if (gameState >= 2) {
                    if (event.getDamage() >= ((Damageable) event.getEntity()).getHealth()) {
                        if (traitors.contains(player.getUniqueId()) || detective.contains(player.getUniqueId())) {
                            int newPoints = points.get(player.getUniqueId()) + 2;
                            points.remove(player.getUniqueId());
                            points.put(player.getUniqueId(), newPoints);
                        }
                    }
	        	} else {
	        		event.setCancelled(true);
	        	}
	        }
	    }

        if (event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            if (!innocents.contains(entity.getUniqueId()) && !traitors.contains(entity.getUniqueId()) && !detective.contains(entity.getUniqueId())) {
                event.setCancelled(true);
            }
        }

	}

    public int safeRandom(int maxSize) {
        int newInt;
        do {
            newInt = random.nextInt(maxSize);
        } while (newInt == 0);
        return newInt;
    }

    // Game start/end mechanics

    public void startGame() {
    	
    	Thread thread = new Thread(new ThreadALLTHETHINGS(),"Chest Generation");
    	thread.start();
    	
        for (UUID uuid : players) {
        	TitleManager.sendHeader(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + "PRE-GAME");
        	
        	if (!karma.containsKey(uuid)) {
            	karma.put(uuid, 1000);	
        	}
        	
        	getServer().getPlayer(uuid).teleport(spawn);
            getServer().getPlayer(uuid).getInventory().clear();
            getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
            getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:30");
            if (karma.get(uuid) != 1000) {
            getServer().getPlayer(uuid).sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Your karma is " + karma.get(uuid) + ". As a result, all damage you deal is reduced by " + ((int)Math.floor(1000 / karma.get(uuid))) + "%");
            } else {
                getServer().getPlayer(uuid).sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Your karma is " + karma.get(uuid) + ". As a result, you deal full damage this round!");
            }
            
            getServer().getPlayer(uuid).setLevel(karma.get(uuid));
            
            gameState = 1;
        }
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                ItemStack book = new ItemStack(Material.BOOK,1);
                ItemMeta metaBook = book.getItemMeta();
                metaBook.setDisplayName(ChatColor.GOLD + "Store");
                book.setItemMeta(metaBook);
            	
                for (UUID uuid : players) {
                    innocents.add(uuid);
                }
                int random = safeRandom(innocents.size());
                detective.add(innocents.get(random));
                getServer().getPlayer(innocents.get(random)).setDisplayName(ChatColor.DARK_BLUE + getServer().getPlayer(innocents.get(random)).getName() + ChatColor.RESET);
                getLogger().info("UUID IS DETECTIVE: " + innocents.get(random));
                innocents.remove(random);

                if (players.size() >= 6) {
                for (int i = 0; i < Math.floor(players.size()%6) + 1;i++) {
                    int random2 = safeRandom(innocents.size());
                    traitors.add(innocents.get(random2));
                    getLogger().info("UUID IS TRAITOR: " + innocents.get(random2));
                    innocents.remove(random2);
                }
                } else {
                    int random2 = safeRandom(innocents.size());
                    traitors.add(innocents.get(random2));
                    getLogger().info("UUID IS TRAITOR: " + innocents.get(random2));
                    innocents.remove(random2);
                }

                for (UUID uuid : players) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "The traitors have been selected!");
                    for (UUID uuidD : detective) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_BLUE + "" + getServer().getPlayer(uuidD).getName() + " is the detective!");
                    }
                    points.put(uuid, 0);
                }
                for (UUID uuid : innocents) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "You are an " + ChatColor.BOLD + "INNOCENT" + ChatColor.RESET + "" + ChatColor.GREEN + "! But there are traitors around...");
                    getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "Who can you trust, and who is out to fill you with bullets?");
                    getServer().getPlayer(uuid).sendMessage("");
                    getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "Watch your back and work with your comrades to get out of this alive!");
                    /*ItemStack stack = new ItemStack(Material.COMPASS);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "DETECTIVE TRACKER");
                    stack.setItemMeta(meta);
                    getServer().getPlayer(uuid).getInventory().addItem(stack); */
                    TitleManager.sendHeaderAndFooter(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.GREEN + "You are an " + ChatColor.BOLD + "INNOCENT");
                }
                for (UUID uuid : traitors) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "You are a " + ChatColor.BOLD + "TRAITOR" + ChatColor.RESET + "" + ChatColor.DARK_RED + "! Work with fellow traitors to kill all others!");
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "But take care, or your treason might be discovered...");
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "These are your comrades: ");
                    for (UUID uuidT : traitors) {
                    	getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + getServer().getPlayer(uuidT).getName());
                    }
                    /*ItemStack stack = new ItemStack(Material.COMPASS,1);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "PLAYER TRACKER");
                    stack.setItemMeta(meta);
                    getServer().getPlayer(uuid).getInventory().addItem(stack); */
                    
                    getServer().getPlayer(uuid).getInventory().addItem(book);
                    TitleManager.sendHeaderAndFooter(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.DARK_RED + "You are a " + ChatColor.BOLD + "TRAITOR");
                }
                for (UUID uuidD : detective) {
                    getServer().getPlayer(uuidD).sendMessage(ChatColor.DARK_BLUE + "You are a " + ChatColor.BOLD + "DETECTIVE" + ChatColor.RESET + "" + ChatColor.DARK_BLUE + ". Try to stay alive and find the traitors!");
                    
                    // Store
                    getServer().getPlayer(uuidD).getInventory().addItem(book);

                    TitleManager.sendHeaderAndFooter(getServer().getPlayer(uuidD), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.DARK_BLUE + "You are a " + ChatColor.BOLD + "DETECTIVE");
                    
                }
                scheduler.scheduleSyncRepeatingTask(TraitorsAndMiners.instance, new Runnable() {
                	@Override
                	public void run() {
                		if (!detective.isEmpty()) {
                			for (UUID uuid : detective) {
                				Player player = getServer().getPlayer(uuid);
                				int newPoints = points.get(uuid) + 1;
                				points.remove(uuid);
                                points.put(uuid, newPoints);
                				player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "You have earned more coins! Current Coins: " + points.get(uuid));
                			}
                		}
                		if (!traitors.isEmpty()) {
                			for (UUID uuid : traitors) {
                				Player player = getServer().getPlayer(uuid);
                				int newPoints = points.get(uuid) + 1;
                                points.remove(uuid);
                                points.put(uuid, newPoints);
                				player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "You have earned more coins! Current Coins: " + points.get(uuid));
                			}
                		}
                	}
                }, 0L, 1200L);
                gameState = 2;
            }
        }, 600L);

        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : players) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:15");
                }
            }
        }, 300L);


        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : players) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:10");
                }
            }
        }, 400L);
        ScheduleSetups.scheduleRoundTimer(this, scheduler);

        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	@Override
        	public void run() {
        		for (UUID uuid : innocents) {
        			Player player = getServer().getPlayer(uuid);
        			if (((Damageable)player).getHealth() > 7.5D) {
        				player.setCustomName(ChatColor.GREEN + player.getName() + ChatColor.RESET);
        			} else if (((Damageable)player).getHealth() <= 7.5D && ((Damageable)player).getHealth() > 5.0D) {
        				player.setCustomName(ChatColor.YELLOW + player.getName() + ChatColor.RESET);
        			} else if (((Damageable)player).getHealth() <= 2.5D) {
        				player.setCustomName(ChatColor.RED + player.getName() + ChatColor.RESET);
        			}
        		}
        		
        		for (UUID uuid : traitors) {
        			Player player = getServer().getPlayer(uuid);
        			if (((Damageable)player).getHealth() > 7.5D) {
        				player.setCustomName(ChatColor.GREEN + player.getName() + ChatColor.RESET);
        			} else if (((Damageable)player).getHealth() <= 7.5D && ((Damageable)player).getHealth() > 5.0D) {
        				player.setCustomName(ChatColor.YELLOW + player.getName() + ChatColor.RESET);
        			} else if (((Damageable)player).getHealth() <= 2.5D) {
        				player.setCustomName(ChatColor.RED + player.getName() + ChatColor.RESET);
        			}
        		}
        	}
        }, 0L, 10L);

    }

    public void endGame(String whoWon) {
        if (whoWon.equalsIgnoreCase("innocents")) {
            for (UUID uuid : players) {
            	TitleManager.sendFloatingText(getServer().getPlayer(uuid), ChatColor.GREEN + "The " + ChatColor.BOLD + "INNOCENTS" + ChatColor.RESET + "" + ChatColor.GREEN + " won!", "", 0, 200, 0);
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
            	TitleManager.sendFloatingText(getServer().getPlayer(uuid), ChatColor.DARK_RED + "The " + ChatColor.BOLD + "TRAITORS" + ChatColor.RESET + "" + ChatColor.DARK_RED + " won!", "", 0, 200, 0);
                getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "The TRAITORS won!");
                getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "The remaining traitors were: ");
                for (UUID uuidT : traitors) {
                    getServer().getPlayer(uuid).sendMessage("" + ChatColor.DARK_RED + "" + ChatColor.ITALIC + getServer().getPlayer(uuidT).getDisplayName());
                }
            }
        } else {
            throw new IllegalArgumentException("So... who won again?");
        }

        gameState = 3;
        
        for (UUID uuid : players) {
            getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 30 seconds!");
            
            getServer().getPlayer(uuid).sendMessage(ChatColor.GOLD + "What map should be next?");
            getServer().getPlayer(uuid).sendMessage(ChatColor.GOLD + "- - -");
            for (Map map : MapManager.maps) {
                getServer().getPlayer(uuid).sendMessage(ChatColor.GOLD + "" + (MapManager.maps.indexOf(map) + 1) + ". " + map.getMapName() + " by " + map.getMapAuthor());
            }
            getServer().getPlayer(uuid).sendMessage(ChatColor.GOLD + "- - -");

            getServer().getPlayer(uuid).setBedSpawnLocation(lobby, true);
            getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
            getServer().getPlayer(uuid).setCanPickupItems(false);
            getServer().getPlayer(uuid).setAllowFlight(true);
            getServer().getPlayer(uuid).getInventory().clear();
            getServer().getPlayer(uuid).setNoDamageTicks(100000);
            getServer().getPlayer(uuid).setMaximumNoDamageTicks(10000);
            
        	getServer().getPlayer(uuid).setDisplayName(getServer().getPlayer(uuid).getName());
        	
            for (UUID uuid2 : players) {
            	getServer().getPlayer(uuid).showPlayer(getServer().getPlayer(uuid2));
            }
            
            TitleManager.sendHeader(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.BLUE + "POST-GAME");
            
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
            }
        }, 600L);
        
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
        	@Override
        	public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("lobby");
        		for (Player player : getServer().getWorld("world").getPlayers()) {
                    player.sendPluginMessage(TraitorsAndMiners.instance, "BungeeCord", out.toByteArray());
        		}
        		MapManager.recordVote();
        	}
        }, 595L);

        ScheduleSetups.scheduleEndTime(this, scheduler);

        try {
        	File karmaFile = new File(this.getDataFolder(),"karma.txt");
        	if (karmaFile.exists()) {
        		karmaFile.delete();
        	}
        	
        	PrintWriter writer = new PrintWriter(karmaFile);
        	
        	writer.println("-- The karma points on this server. DO NOT ALTER UNLESS AUTHORIZED!");
        	
        	for (int i = 0; i < karma.size();i++) {
                for (Player player : getServer().getWorld("world").getPlayers()) {
                	UUID uuid = player.getUniqueId();
                	if (karma.containsKey(uuid)) {
                		if (karma.get(player.getUniqueId()) == 1000) {
                		writer.println(uuid + ": " + karma.get(uuid));
                		} else {
                			writer.println(uuid + ": " + (karma.get(uuid) + 5));
                		}
                	}
                }
        	}
        	
        	writer.close();
        	
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startTAM")) {
        	if (gameState == 0) {
        	if (getServer().getWorld("world").getPlayers().size() >= 3) {
            for (Player player : getServer().getWorld("world").getPlayers()) {
                player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 10 seconds...");
            }

            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    for (Player player : getServer().getWorld("world").getPlayers()) {
                    players.add(player.getUniqueId());
                    }
                    startGame();
                }
            }, 200L);
            ScheduleSetups.scheduleStartTime(this, scheduler);
        	} else {
        		sender.sendMessage(ChatColor.RED + "There are not enough players to start!");
        	}
            return true;
        	}
        }
        if (cmd.getName().equalsIgnoreCase("vote")) {
        	if (sender instanceof Player) {
        	Player player = (Player) sender;
            if (args.length == 1) {
                try {
                	if (!MapManager.voted.contains(player.getUniqueId())) {
                    int mapNumber = Integer.parseInt(args[0]);
                    if (MapManager.maps.get(mapNumber - 1) != null) {
                        int amountOfVotes;
                        if (MapManager.votes.get(MapManager.maps.get(mapNumber - 1)) == null) {
                            amountOfVotes = 1;
                        } else {
                            amountOfVotes = MapManager.votes.get(MapManager.maps.get(mapNumber - 1)) + 1;
                            MapManager.votes.remove(MapManager.maps.get(mapNumber - 1));
                        }
                        MapManager.votes.put(MapManager.maps.get(mapNumber - 1),amountOfVotes);
                        player.sendMessage(ChatColor.GOLD + "You voted for: " + MapManager.maps.get(mapNumber - 1).getMapName() + " by " + MapManager.maps.get(mapNumber - 1).getMapAuthor());
                        MapManager.voted.add(player.getUniqueId());
                    } else {
                    	player.sendMessage(ChatColor.RED + "A map with that ID does not exist!");
                    }
                    } else {
                    	player.sendMessage(ChatColor.RED + "You already voted for a map!");
                    }
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
        	}
        }
        if (cmd.getName().equalsIgnoreCase("supply")) {
        	for (UUID uuid : players) {
        		Player player = getServer().getPlayer(uuid);
        		
				ItemStack bow = new ItemStack(Material.BOW);
				bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
				ItemStack arrow = new ItemStack(Material.ARROW);
				
				ItemStack sword = new ItemStack(Material.STONE_SWORD);
        		
				switch(random.nextInt(1)) {
				case 0: player.getInventory().addItem(bow,arrow); break;
				case 1: player.getInventory().addItem(sword); break;
				default: player.getInventory().addItem(sword); break;
				}
				
        	}
        }
        return false;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void disableReload1(ServerCommandEvent event) {
        String command = event.getCommand().split(" ")[0];
        if (command.equalsIgnoreCase("reload")) {
            event.setCommand("stop");
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void disableReload2(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        if (command.equalsIgnoreCase("reload")) {
            event.setMessage("stop");
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void joinHandler(final PlayerJoinEvent event) {
    	if (gameState == 0) {
	    	event.setJoinMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + event.getPlayer().getName() + " has joined (" + (getServer().getWorld("world").getPlayers().size() + 1) + "/" + getServer().getMaxPlayers() + ")");
    	} else {
    		event.setJoinMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "A wild spectator appeared!");
    	}
    	Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
    		@Override
    		public void run() {
    	    	if (gameState == 0) {
    	    		if (lobby != null) {
    	    				event.getPlayer().teleport(lobby);
    	    		} else {
    	    			event.getPlayer().teleport(new Location(getServer().getWorld("world"),50,65,0));
    	    		}
    	    	
    	    	final Player playerF = event.getPlayer();
    	    	    	
    	    	TitleManager.sendFloatingText(playerF, ChatColor.LIGHT_PURPLE + "The current map is: ", ChatColor.RED + map, 40, 60, 40);
    	    	
    	    	TitleManager.sendHeaderAndFooter(event.getPlayer(), ChatColor.DARK_RED + "TAM - " + ChatColor.GOLD + "LOBBY", ChatColor.AQUA + "(Plugin by Mitchellbrine)");
    	    	
    	    	if (getServer().getWorld("world").getPlayers().size() >= maxPlayers / 2) {
    	            for (Player player : getServer().getWorld("world").getPlayers()) {
    	                players.add(player.getUniqueId());
    	                player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 10 seconds...");
    	            }

    	            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    	            scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
    	                @Override
    	                public void run() {
    	                	if (getServer().getWorld("world").getPlayers().size() >= maxPlayers / 2) {
    	                		startGame();
    	                	}
    	                }
    	            }, 200L);
    	            ScheduleSetups.scheduleStartTime(instance, scheduler);
    	    	}
    	    	} else if (gameState >= 1) {
    	    		if (!players.contains(event.getPlayer().getUniqueId())) {
    	    		event.getPlayer().teleport(spawn);
    	            event.getPlayer().setBedSpawnLocation(lobby, true);
    	            event.getPlayer().setGameMode(GameMode.ADVENTURE);
    	            event.getPlayer().setCanPickupItems(false);
    	            event.getPlayer().setAllowFlight(true);
    	            event.getPlayer().setMaximumNoDamageTicks(10000);
    	            event.getPlayer().setHealth(20.0D);
    	            event.getPlayer().getInventory().clear();
    	            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU ARE SPECTATING!");
    	            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Spectate to see who wins!");
    	            TitleManager.sendHeaderAndFooter(event.getPlayer(), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.LIGHT_PURPLE + "You are " + ChatColor.BOLD + "SPECTATING");
    	    		event.getPlayer().setDisplayName(ChatColor.DARK_RED + "*DEAD* " + ChatColor.RESET +  event.getPlayer().getName());
    	            
    	    		for (UUID uuid : players) {
    	    			getServer().getPlayer(uuid).hidePlayer(event.getPlayer());
    	    		}
    	    		
    	    		}
    	    	}
    		}
    	}, 10L);

    }

    @SuppressWarnings("unused")
    @EventHandler
    public void leaveHandler(PlayerQuitEvent event) {
    	if (gameState == 1) {
    		if (!innocents.contains(event.getPlayer().getUniqueId()) && traitors.contains(event.getPlayer().getUniqueId()) && detective.contains(event.getPlayer().getUniqueId())) {
    			event.setQuitMessage("");
    		}
    	}
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void traitorChatEvent(final AsyncPlayerChatEvent event) {
    	if (traitors.contains(event.getPlayer().getUniqueId())) {
    		if (event.getMessage().startsWith("T:")) {
    			event.setMessage(event.getMessage().substring(2));
    			for (Player player : Bukkit.getOnlinePlayers()) {
    				if (!traitors.contains(player.getUniqueId())) {
    					event.getRecipients().remove(player);
    				}
    			}
    		}
    	}
    	if (!innocents.contains(event.getPlayer().getUniqueId()) && !traitors.contains(event.getPlayer().getUniqueId()) && !detective.contains(event.getPlayer().getUniqueId())) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (innocents.contains(player.getUniqueId()) || detective.contains(player.getUniqueId()) || traitors.contains(player.getUniqueId())) {
					event.getRecipients().remove(player);
				}
			}
    	}
    }
    
    private class ThreadALLTHETHINGS implements Runnable {

		@Override
		public void run() {

			ItemStack bow = new ItemStack(Material.BOW);
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,1);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);

            ItemStackHelper.setItemName(bow,"Pistol");

            ItemStack arrow = new ItemStack(Material.ARROW);
			
			ItemStack sword = new ItemStack(Material.STONE_SWORD);

            ItemStack woodSword = new ItemStack(Material.WOOD_SWORD);

            ItemStack fishing = new ItemStack(Material.FISHING_ROD);

            fishing.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            fishing.addUnsafeEnchantment(Enchantment.KNOCKBACK,4);

            ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL,1,(short)random.nextInt(65));

            flint.addUnsafeEnchantment(Enchantment.FIRE_ASPECT,1);

            ItemStack desertEagle = new ItemStack(Material.BOW);
            desertEagle.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,4);
            desertEagle.addUnsafeEnchantment(Enchantment.ARROW_INFINITE,1);

            ItemStackHelper.setItemName(desertEagle,"Deagle");

            ItemStack huge = new ItemStack(Material.BOW);
            huge.addUnsafeEnchantment(Enchantment.ARROW_INFINITE,1);

            ItemStackHelper.setItemName(huge,"H.U.G.E. 249");

            ItemStack glock = new ItemStack(Material.BOW);
            glock.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,1);
            glock.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK,2);
            glock.addUnsafeEnchantment(Enchantment.ARROW_INFINITE,1);

            ItemStackHelper.setItemName(glock,"Glock");

	    		for (int x = -100; x < 100;x++) {
	    			for (int y = 0; y < 255; y++) {
	    				for (int z = -100; z < 100;z++) {
	    					Location blockLoc = new Location(getServer().getWorld("world"),x,y,z);
	    					if (getServer().getWorld("world").getBlockAt(blockLoc).getType() == Material.CHEST) {
	    							Chest chest = (Chest) getServer().getWorld("world").getBlockAt(blockLoc).getState();
	    							Inventory inventory = chest.getInventory();
	    							inventory.clear();
                                    ItemStack stack;
	    							switch(random.nextInt(8)) {
	    							    case 0: stack = bow; break;
	    							    case 1: stack = sword; break;
	    							    case 2: stack = woodSword; break;
	    							    case 3: stack = fishing; break;
                                        case 4: stack = flint; break;
                                        case 5: stack = desertEagle; break;
                                        case 6: stack = huge; break;
                                        case 7: stack = glock; break;
	    							    default: stack = sword; break;
	    							}
                                    if (stack.getType() != Material.BOW && stack.getType() != Material.FLINT_AND_STEEL) {
                                        ItemStackHelper.setItemName(stack);
                                    }
                                    
                                    ItemStackHelper.setInRandomSlot(inventory,stack);
                                    if (stack.getType() == Material.BOW) {
                                        inventory.setItem(ItemStackHelper.getSlotOfMaterial(inventory,Material.BOW) + 1,arrow);
                                        //inventory.addItem(arrow);
                                    }
	    					}
	    				}
	    			}
	    		}
		}
    	
    }

    @EventHandler
    public void gameCheck(PlayerGameModeChangeEvent event) {
        if (gameState != 2) return;

        Player player = event.getPlayer();

        if (!innocents.contains(player.getUniqueId()) && !traitors.contains(player.getUniqueId()) && !detective.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }

    }
    

	  @EventHandler(priority = EventPriority.HIGHEST)
	  public void signChange(SignChangeEvent event) {
		  if (event.getLine(0).equalsIgnoreCase("HUB")) {
              if (event.getPlayer().isOp()) {
                  event.setLine(0, ChatColor.GREEN + "[HUB]");
                  event.setLine(1, "Return to lobby");
              }
          }
	  }

    /*@EventHandler
    public void onPlayerHurtPlayer(EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDamaged = event.getEntity();

        if (entityDamager instanceof Arrow) {
            if (entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
                Arrow arrow = (Arrow) entityDamager;

                Vector velocity = arrow.getVelocity();

                Player shooter = (Player) arrow.getShooter();
                Player damaged = (Player) entityDamaged;

                if (!innocents.contains(damaged.getUniqueId()) && !traitors.contains(damaged.getUniqueId()) && !detective.contains(damaged.getUniqueId())) {
                    Arrow newArrow = damaged.launchProjectile(Arrow.class);
                    newArrow.setShooter(shooter);
                    newArrow.setVelocity(velocity);
                    newArrow.setBounce(false);

                    event.setCancelled(true);
                    arrow.remove();
                }
            }
        }
    } */
        

}
