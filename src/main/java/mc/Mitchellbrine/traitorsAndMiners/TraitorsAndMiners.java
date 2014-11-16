package mc.Mitchellbrine.traitorsAndMiners;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class TraitorsAndMiners extends JavaPlugin implements Listener {

    Random randomW = new Random(System.currentTimeMillis());
    Random random;

    public int maxPlayers = 24;
    public static int gameState;

    public static String map;
    
    public ArrayList<UUID> players = new ArrayList<>();

    public ArrayList<UUID> traitors = new ArrayList<>();
    public ArrayList<UUID> detective = new ArrayList<>();
    public ArrayList<UUID> innocents = new ArrayList<>();
    
    public ArrayList<UUID> voteStart = new ArrayList<>();
    
    public HashMap<UUID,Integer> points = new HashMap<>();

    public Location lobby;
    public Location spawn;

    public static TraitorsAndMiners instance;

    @Override
    public void onLoad() {
        FileHelper.deleteDir(new File(this.getServer().getWorldContainer(), "world"));
        try {
        	switch(randomW.nextInt(2)) {
        	case 0: FileHelper.unzip(new File(this.getDataFolder(), "world_tam.zip"), new File(this.getServer().getWorldContainer(), "world")); 
			getLogger().info("Loaded world 1 (Mansion by MBrine)");
			map = "Mansion by Mitchellbrine";
        	break;
        	case 1: if (new File(this.getDataFolder(),"world_tam1.zip").exists()) {
        		FileHelper.unzip(new File(this.getDataFolder(), "world_tam1.zip"), new File(this.getServer().getWorldContainer(), "world")); 
    			getLogger().info("Loaded world 2 (Western Mines by MCG)");
    			map = "Western Mines by MrComputerGhost";
        	} else {
    		FileHelper.unzip(new File(this.getDataFolder(), "world_tam.zip"), new File(this.getServer().getWorldContainer(), "world")); 
			getLogger().info("Loaded world 1 (Mansion by MBrine)");
			map = "Mansion by Mitchellbrine";
        	}
    		break;
        	case 2:
        		if (new File(this.getDataFolder(),"world_tam2.zip").exists()) {
        			FileHelper.unzip(new File(this.getDataFolder(), "world_tam2.zip"), new File(this.getServer().getWorldContainer(), "world")); 
        			getLogger().info("Loaded world 3 (Rainbow Road by kk)");
        			map = "Rainbow Road by kkaylium";
        		} else {
             		FileHelper.unzip(new File(this.getDataFolder(), "world_tam.zip"), new File(this.getServer().getWorldContainer(), "world")); 
        			getLogger().info("Loaded world 1 (Mansion by MBrine)");
        			map = "Mansion by Mitchellbrine";
        		}
        	break;
        	default:
        		FileHelper.unzip(new File(this.getDataFolder(), "world_tam.zip"), new File(this.getServer().getWorldContainer(), "world")); 
    			getLogger().info("Loaded world 1 (Mansion by MBrine)");
    			map = "Mansion by Mitchellbrine";
        		break;
        	}
        } catch (Exception ex) {
			getLogger().info("Someone derped!");
            ex.printStackTrace();
            getServer().shutdown();
        }
    }

    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(this, this);
        new TraitorWeapons(this);
        lobby = new Location(getServer().getWorld(getServer().getWorld("world").getUID()), 50, 66, 0);
        spawn = new Location(getServer().getWorld(getServer().getWorld("world").getUID()), 0, 65, 0);
        gameState = 0;
        random = MathHelper.random(getServer().getWorld("world").getTime());
        Bukkit.getMessenger().registerOutgoingPluginChannel(TraitorsAndMiners.instance, "BungeeCord");
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void deathOfAPlayerman(PlayerDeathEvent event) {
    	Player player = event.getEntity();
        	if (gameState == 2) {
    		if (players.contains(player.getUniqueId())) {
            event.setDeathMessage("");
            player.setGameMode(GameMode.ADVENTURE);
            player.setCanPickupItems(false);
            player.setAllowFlight(true);
            player.setMaximumNoDamageTicks(10000);
            player.setHealth(20.0D);
            player.getInventory().clear();
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED!");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Spectate to see who wins!");
            TitleManager.sendHeaderAndFooter(event.getEntity(), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.LIGHT_PURPLE + "You are " + ChatColor.BOLD + "DEAD");
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
                    traitors.remove(player.getUniqueId());
                } else if (detective.contains(player.getUniqueId())) {
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
        if (event.getEntity() instanceof Player && event.getCause() == DamageCause.PROJECTILE) {
            Player player = (Player) event.getEntity();
            if (event.getDamage() > player.getHealth()) {
            	if (players.contains(player.getUniqueId())) {
                event.setCancelled(true);
                player.setGameMode(GameMode.ADVENTURE);
                player.setCanPickupItems(false);
                player.setAllowFlight(true);
                player.setMaximumNoDamageTicks(10000);
                player.setHealth(20.0D);
                player.getInventory().clear();
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "YOU DIED!");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Spectate to see who wins!");
                TitleManager.sendHeaderAndFooter(player, ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.LIGHT_PURPLE + "You are " + ChatColor.BOLD + "DEAD");
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
    }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Player) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) damager;
                Player entity = (Player) event.getEntity();
                if (!innocents.contains(player.getUniqueId()) && !traitors.contains(player.getUniqueId()) && !detective.contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
                if (!innocents.contains(entity.getUniqueId()) && !traitors.contains(entity.getUniqueId()) && !detective.contains(entity.getUniqueId())) {
                	event.setCancelled(true);
                }
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
        	if (getServer().getPlayer(uuid).isInsideVehicle()) {
        		getServer().getPlayer(uuid).eject();
        	}
        	TitleManager.sendHeader(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + "PRE-GAME");
            getServer().getPlayer(uuid).teleport(spawn);
            getServer().getPlayer(uuid).getInventory().clear();
            getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
            getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Round starting in 0:30");
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
                    getServer().getPlayer(uuid).sendMessage(ChatColor.GREEN + "You are an " + ChatColor.BOLD + "INNOCENT" + ChatColor.RESET + "" + ChatColor.GREEN + ". Try to stay alive and find the traitors");
                    ItemStack stack = new ItemStack(Material.COMPASS);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "DETECTIVE TRACKER");
                    stack.setItemMeta(meta);
                    getServer().getPlayer(uuid).getInventory().addItem(stack);
                    TitleManager.sendHeaderAndFooter(getServer().getPlayer(uuid), ChatColor.DARK_RED + "TAM - " + ChatColor.AQUA + " In-Game", ChatColor.GREEN + "You are an " + ChatColor.BOLD + "INNOCENT");
                }
                for (UUID uuid : traitors) {
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "You are a " + ChatColor.BOLD + "TRAITOR" + ChatColor.RESET + "" + ChatColor.DARK_RED + ". Try to stay alive and kill all the innocents!");
                    getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + "The traitors are: ");
                    for (UUID uuidT : traitors) {
                    	getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_RED + getServer().getPlayer(uuidT).getName());
                    }
                    ItemStack stack = new ItemStack(Material.COMPASS,1);
                    ItemMeta meta = stack.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "PLAYER TRACKER");
                    stack.setItemMeta(meta);
                    getServer().getPlayer(uuid).getInventory().addItem(stack);
                    
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
        
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	@Override
        	public void run() {
        		if (!traitors.isEmpty()) {
        		for (UUID uuid : traitors) {
        			Player player = getServer().getPlayer(uuid);
        			Player target = PlayerHelper.getNearest(player,100D);
        			if (target != null) {
        				if (traitors.contains(target.getUniqueId()) && (innocents.contains(target.getUniqueId()) || detective.contains(target.getUniqueId()))) {
        				player.setCompassTarget(target.getLocation());
        				for (int i = 0; i < player.getInventory().getSize(); i++) {
        					if (player.getInventory().getItem(i).getType() == Material.COMPASS) {
        						ItemMeta meta = player.getInventory().getItem(i).getItemMeta();
        						meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "PLAYER TRACKER | " + PlayerHelper.getDistance(player, target) + "m");
        						List<String> lore = meta.getLore();
        						lore.add("Tracked Player: " + target.getName());
        						meta.setLore(lore);
        	                    player.getInventory().getItem(i).setItemMeta(meta);
        					}
        				}
        			}
        			}
        		}
        		if (!innocents.isEmpty()) {
        		for (UUID uuid : innocents) {
        			Player player = getServer().getPlayer(uuid);
        			Player target = PlayerHelper.getNearest(player,100D);
        			if (target != null) {
        				if (detective.contains(target.getUniqueId())) {
        				player.setCompassTarget(target.getLocation());
        				for (int i = 0; i < player.getInventory().getSize(); i++) {
        					if (player.getInventory().getItem(i).getType() == Material.COMPASS) {
        						ItemMeta meta = player.getInventory().getItem(i).getItemMeta();
        						meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "PLAYER TRACKER | " + PlayerHelper.getDistance(player, target) + "m");
        						meta.getLore().add("Tracked Player: " + target.getName());
        	                    player.getInventory().getItem(i).setItemMeta(meta);
        					}
        				}
        				}
        			}
        		}
        		}
        		}
        		
        	}
        }, 0L, 10L);

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

        gameState = 3;
        
        for (UUID uuid : players) {
            getServer().getPlayer(uuid).sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Server restarting in 30 seconds!");

            getServer().getPlayer(uuid).setBedSpawnLocation(lobby, true);
            getServer().getPlayer(uuid).setGameMode(GameMode.ADVENTURE);
            getServer().getPlayer(uuid).setCanPickupItems(false);
            getServer().getPlayer(uuid).setAllowFlight(true);
            getServer().getPlayer(uuid).getInventory().clear();
            getServer().getPlayer(uuid).setMaximumNoDamageTicks(10000);
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
        	}
        }, 595L);

        ScheduleSetups.scheduleEndTime(this, scheduler);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("startTAM")) {
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
        if (cmd.getName().equalsIgnoreCase("vote")) {
        	if (sender instanceof Player) {
        	Player player = (Player) sender;
        	if (!voteStart.contains(player.getUniqueId())) {
        	voteStart.add(player.getUniqueId());
        	player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "You have voted to start the game!");
        	} else {
        	voteStart.remove(player.getUniqueId());
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "You have removed your vote to start the game!");
        	}
        	if (getServer().getWorld("world").getPlayers().size() >= 3) {
        	if (voteStart.size() > getServer().getWorld("world").getPlayers().size() / 2) {
        		for (UUID uuid : voteStart) {
        			voteStart.remove(uuid);
        		}
        		for (Player players : getServer().getWorld("world").getPlayers()) {
        			players.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "The vote to start the game has passed!");
                    this.players.add(player.getUniqueId());
                    players.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 10 seconds...");
        		}

                BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
                scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                }, 200L);
                ScheduleSetups.scheduleStartTime(this, scheduler);
        	}
        	}
        	return true;
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
    public void joinHandler(PlayerJoinEvent event) {
    	if (((CraftPlayer) event.getPlayer()).getHandle().playerConnection.networkManager.getVersion() == 47) {
    	if (gameState == 0) {
    	event.getPlayer().teleport(lobby);
    	event.setJoinMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + event.getPlayer().getName() + " has joined (" + (getServer().getWorld("world").getPlayers().size() + 1) + "/" + getServer().getMaxPlayers() + ")");
    	
    	final Player playerF = event.getPlayer();
    	    	
    	TitleManager.sendFloatingText(playerF, ChatColor.LIGHT_PURPLE + "The current map is: ", ChatColor.RED + map, 40, 60, 40);
    	
    	TitleManager.sendHeaderAndFooter(event.getPlayer(), ChatColor.DARK_RED + "TAM - " + ChatColor.GOLD + "LOBBY", ChatColor.AQUA + "(Plugin by Mitchellbrine)");
    	
    	if (getServer().getWorld("world").getPlayers().size() >= maxPlayers / 2) {
            for (Player player : getServer().getWorld("world").getPlayers()) {
                players.add(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Game beginning in 10 seconds...");
            }

            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                	if (getServer().getWorld("world").getPlayers().size() >= maxPlayers / 2) {
                		startGame();
                	}
                }
            }, 200L);
            ScheduleSetups.scheduleStartTime(this, scheduler);
    	}
    	} else if (gameState >= 1) {
    		if (!players.contains(event.getPlayer().getUniqueId())) {
    		event.setJoinMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "A wild spectator appeared!");
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
    		
    		for (UUID uuid : players) {
    			getServer().getPlayer(uuid).hidePlayer(event.getPlayer());
    		}
    		
    		}
    	}
    	} else {
    		event.getPlayer().kickPlayer(ChatColor.AQUA + "Please upgrade to 1.8! We're soory for the inconvenience!");
    	}
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
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			ItemStack arrow = new ItemStack(Material.ARROW);
			
			ItemStack sword = new ItemStack(Material.STONE_SWORD);
			
			
	    		for (int x = -100; x < 100;x++) {
	    			for (int y = 0; y < 255; y++) {
	    				for (int z = -100; z < 100;z++) {
	    					Location blockLoc = new Location(getServer().getWorld("world"),x,y,z);
	    					if (getServer().getWorld("world").getBlockAt(blockLoc).getType() == Material.CHEST) {
	    							Chest chest = (Chest) getServer().getWorld("world").getBlockAt(blockLoc).getState();
	    							Inventory inventory = chest.getInventory();
	    							inventory.clear();
	    							switch(random.nextInt(4)) {
	    							case 0: inventory.addItem(bow,arrow); break;
	    							case 1: inventory.addItem(sword); break;
	    							case 2: inventory.addItem(bow,arrow); break;
	    							case 3: inventory.addItem(sword); break;
	    							default: inventory.addItem(sword); break;
	    							}
	    					}
	    				}
	    			}
	    		}
		}
    	
    }

}
