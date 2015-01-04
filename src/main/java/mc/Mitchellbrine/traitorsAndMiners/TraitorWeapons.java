package mc.Mitchellbrine.traitorsAndMiners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

@SuppressWarnings("unused")
public class TraitorWeapons implements Listener{
	
	BukkitScheduler scheduler = Bukkit.getScheduler();
	
	private String traitorStore = "Traitor Store";
	private String detectiveStore = "Detective Store";
	private String teleportName = "Who will you teleport to?";
	
	public TraitorWeapons() {
	}

	@EventHandler(ignoreCancelled=true)
	public void openInventory(PlayerInteractEvent event) {
        Action a = event.getAction();
        ItemStack is = event.getItem();
 
        if(a == Action.PHYSICAL)
            return;
        if (is != null && is.getType() == Material.BOOK) {
            if (TraitorsAndMiners.instance.traitors.contains(event.getPlayer().getUniqueId())) {
            	openTraitorGUI(event.getPlayer());
            }
            if (TraitorsAndMiners.instance.detective.contains(event.getPlayer().getUniqueId())) {
            	openDetectiveGUI(event.getPlayer());
            }
        } else if (is != null && is.getType() == Material.SHEARS) {
        	if (event.getClickedBlock().getType() == Material.TNT) {
        		event.getClickedBlock().setType(Material.AIR);
				for (UUID uuid : TraitorsAndMiners.instance.players) {
					Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
					players.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "THE BOMB WAS DEFUSED BY " + event.getPlayer().getName() + "!");
				}
        	}
        }
        
		if (event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (!TraitorsAndMiners.instance.innocents.contains(event.getPlayer().getUniqueId()) && !TraitorsAndMiners.instance.traitors.contains(event.getPlayer().getUniqueId()) && !TraitorsAndMiners.instance.detective.contains(event.getPlayer().getUniqueId())) {
                if (sign.getLine(0).contains("HUB")) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                    event.getPlayer().sendPluginMessage(TraitorsAndMiners.instance, "BungeeCord", out.toByteArray());
                }
            } else {
                event.setCancelled(true);
            }
		}
	}
	
	@SuppressWarnings({"unchecked","rawtypes","deprecation"})
	@EventHandler
	public void inventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (TraitorsAndMiners.instance.innocents.contains(player.getUniqueId()) || TraitorsAndMiners.instance.traitors.contains(player.getUniqueId()) || TraitorsAndMiners.instance.detective.contains(player.getUniqueId()) || TraitorsAndMiners.gameState < 2) {
		if (event.getInventory().getName().equalsIgnoreCase(traitorStore)) {
			if (TraitorsAndMiners.instance.traitors.contains(player.getUniqueId())) {
				switch(event.getCurrentItem().getType()) {
				case TNT:
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
					final Location playerLoc = player.getLocation();
						Block block = TraitorsAndMiners.instance.getServer().getWorld("world").getBlockAt(playerLoc);
						block.setType(Material.TNT);
					for (UUID uuid : TraitorsAndMiners.instance.players) {
						Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
						players.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "A BOMB IS PRIMED AND WILL EXPLODE IN 10 SECONDS!");
					}
					scheduler.scheduleSyncDelayedTask(TraitorsAndMiners.instance, new Runnable() {
						@Override
						public void run() {
							if (TraitorsAndMiners.instance.getServer().getWorld("world").getBlockAt(playerLoc).getType() == Material.TNT) {
							TraitorsAndMiners.instance.getServer().getWorld("world").createExplosion(playerLoc, 30.0F, true);
							TraitorsAndMiners.instance.getServer().getWorld("world").getBlockAt(playerLoc).setType(Material.AIR);
							}
						}
					}, 200L);
						TraitorsAndMiners.instance.points.remove(player.getUniqueId());
						TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
					} else {
						player.sendMessage(ChatColor.RED + "Not enough coins!");
					}
					break;
				case DIAMOND_SWORD:
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 6) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 6;
						ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
						stack.setDurability((short)1562);
						ItemMeta meta = stack.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "Last Hope");
						List lore = new ArrayList();
						lore.add(ChatColor.RED + "An enchanted sword with one hit left. Insta-kills!");
						meta.setLore(lore);
						meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
						stack.setItemMeta(meta);
						player.getInventory().addItem(stack);
						TraitorsAndMiners.instance.points.remove(player.getUniqueId());
						TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
					} else {
						player.sendMessage(ChatColor.RED + "Not enough coins!");
					}
					break;
				case SKULL_ITEM:
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 8) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 8;
						for (UUID uuid : TraitorsAndMiners.instance.players) {
							Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
							players.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + player.getName() + " IS PRIMED AND WILL EXPLODE IN 10 SECONDS!");
						}
						final Player playerF = player;
						scheduler.scheduleSyncDelayedTask(TraitorsAndMiners.instance, new Runnable() {
							@Override
							public void run() {
								Location playerLoc = playerF.getLocation();
								TraitorsAndMiners.instance.getServer().getWorld("world").createExplosion(playerLoc, 30.0F, true);
							}
						}, 200L);
						TraitorsAndMiners.instance.points.remove(player.getUniqueId());
						TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
					} else {
						player.sendMessage(ChatColor.RED + "Not enough coins!");
					}
					break;
				case BONE:
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 6) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 6;
                    	ItemStack stack = new ItemStack(Material.SKULL_ITEM,1);
                    	stack.setDurability((short) 3);
                    	ItemMeta meta = stack.getItemMeta();
                    	meta.setDisplayName(ChatColor.DARK_RED + player.getName());
                    	stack.setItemMeta(meta);
						for (UUID uuid : TraitorsAndMiners.instance.innocents) {
							Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
							players.getInventory().addItem(stack);
							players.hidePlayer(player);
						}
						for (UUID uuid : TraitorsAndMiners.instance.detective) {
							Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
							players.getInventory().addItem(stack);
							players.hidePlayer(player);
						}
						final Player playerF = player;
						player.sendMessage(ChatColor.DARK_RED + "YOU HAVE 20 SECONDS UNTIL YOU REAPPEAR!");
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 400, 0, true));
						scheduler.scheduleSyncDelayedTask(TraitorsAndMiners.instance, new Runnable() {
							@Override
							public void run() {
								playerF.sendMessage(ChatColor.DARK_RED + "YOU ARE NOW VISIBLE! BEWARE!");
							}
						}, 400L);
						TraitorsAndMiners.instance.points.remove(player.getUniqueId());
						TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
					} else {
						player.sendMessage(ChatColor.RED + "Not enough coins!");
					}
					break;
				case GOLDEN_APPLE:
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
						player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,400,4));
						player.sendMessage(ChatColor.DARK_RED + "You have 20 seconds of increased health!");
						TraitorsAndMiners.instance.points.remove(player.getUniqueId());
						TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
					} else {
						player.sendMessage(ChatColor.RED + "Not enough coins!");
					}
					break;
					default:
						player.closeInventory();
						break;

					
				}
				event.setCurrentItem(null);
				/*if (event.getCurrentItem().getType() == Material.TNT) {
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
					final Location playerLoc = player.getLocation();
					for (UUID uuid : TraitorsAndMiners.instance.players) {
						Player players = TraitorsAndMiners.instance.getServer().getPlayer(uuid);
						players.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "A BOMB IS PRIMED AND WILL EXPLODE IN 5 SECONDS!");
					}
					scheduler.scheduleSyncDelayedTask(TraitorsAndMiners.instance, new Runnable() {
						@Override
						public void run() {
							TraitorsAndMiners.instance.getServer().getWorld("world").createExplosion(playerLoc, 5.0F, false);
						}
					}, 100L);
					TraitorsAndMiners.instance.points.replace(player.getUniqueId(), newPoints);
					}
				}
				if (event.getCurrentItem().getType() == Material.DIAMOND_SWORD) {
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 6) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 6;
						ItemStack stack = new ItemStack(Material.DIAMOND_SWORD);
						stack.setDurability((short)1562);
						ItemMeta meta = stack.getItemMeta();
						meta.setDisplayName(ChatColor.YELLOW + "Last Hope");
						meta.getLore().add(ChatColor.RED + "An enchanted sword with one hit left. Insta-kills!");
						stack.setItemMeta(meta);
						player.getInventory().addItem(stack);
						TraitorsAndMiners.instance.points.replace(player.getUniqueId(), newPoints);
				}
				} */
			}
		}
		
		if (event.getInventory().getName().equalsIgnoreCase(detectiveStore)) {
			if (TraitorsAndMiners.instance.detective.contains(player.getUniqueId())) {
				
				switch(event.getCurrentItem().getType()) {
					case POTION:
						if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
							int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
							ItemStack stack = new ItemStack(Material.POTION,3);
							stack.setDurability((short)16421);
							ItemMeta meta = stack.getItemMeta();
							meta.setDisplayName(ChatColor.DARK_AQUA + "Medical Kit");
							stack.setItemMeta(meta);
							player.getInventory().addItem(stack);
							TraitorsAndMiners.instance.points.remove(player.getUniqueId());
							TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
						} else {
							player.sendMessage(ChatColor.RED + "Not enough coins!");
						}
						break;
					case SHEARS:
						if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
							int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
							ItemStack stack = new ItemStack(Material.SHEARS);
							ItemMeta meta = stack.getItemMeta();
							meta.setDisplayName(ChatColor.YELLOW + "Wire Cutters");
							stack.setItemMeta(meta);
							player.getInventory().addItem(stack);
							TraitorsAndMiners.instance.points.remove(player.getUniqueId());
							TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
						} else {
							player.sendMessage(ChatColor.RED + "Not enough coins!");
						}
						break;
					/* case NETHER_BRICK_ITEM:
						if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 5) {
							int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 5;
							Inventory teleportInv = Bukkit.createInventory(null, 27, teleportName);
							Location playerLoc = player.getLocation();
							for (UUID playersUUID : TraitorsAndMiners.instance.players) {
								if (TraitorsAndMiners.instance.innocents.contains(playersUUID) || TraitorsAndMiners.instance.traitors.contains(playersUUID)) {
									Player players = TraitorsAndMiners.instance.getServer().getPlayer(playersUUID);
									Location playersLoc = players.getLocation();
									if (playersLoc.distance(playerLoc) <= 20) {
										ItemStack head = new ItemStack(Material.SKULL_ITEM,1,(short)3);
										ItemMeta meta = head.getItemMeta();
										meta.setDisplayName(ChatColor.GREEN + players.getName());
										head.setItemMeta(meta);
										teleportInv.setItem(teleportInv.firstEmpty(), head);
									}
								}
							}
							ItemStack stack9 = new ItemStack(Material.EYE_OF_ENDER);
							ItemMeta meta9 = stack9.getItemMeta();
							meta9.setDisplayName(ChatColor.LIGHT_PURPLE + "CLOSE INVENTORY");
							stack9.setItemMeta(meta9);
							teleportInv.setItem(26, stack9);
							TraitorsAndMiners.instance.points.remove(player.getUniqueId());
							TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
						} else {
							player.sendMessage(ChatColor.RED + "Not enough coins!");
						}
						break; */
					case BOW:
						if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 6) {
							int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 6;
							ItemStack stack = new ItemStack(Material.BOW);
							stack.setDurability((short)385);
							ItemMeta meta = stack.getItemMeta();
							meta.setDisplayName(ChatColor.YELLOW + "Last Hope");
							List lore = new ArrayList();
							lore.add(ChatColor.RED + "An enchanted bow with one shot left.");
							meta.setLore(lore);
							meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
							meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
							stack.setItemMeta(meta);
							player.getInventory().addItem(stack);
							TraitorsAndMiners.instance.points.remove(player.getUniqueId());
							TraitorsAndMiners.instance.points.put(player.getUniqueId(), newPoints);
						} else {
							player.sendMessage(ChatColor.RED + "Not enough coins!");
						}
						break;
					default:
						player.closeInventory();
						break;
				}
				event.setCurrentItem(null);
				/*if (event.getCurrentItem().getType() == Material.POTION) {
					if (TraitorsAndMiners.instance.points.get(player.getUniqueId()) >= 4) {
						int newPoints = TraitorsAndMiners.instance.points.get(player.getUniqueId()) - 4;
						ItemStack stack = new ItemStack(Material.POTION,3);
						stack.setDurability((short)16421);
						ItemMeta meta = stack.getItemMeta();
						meta.setDisplayName(ChatColor.DARK_AQUA + "Medical Kit");
						meta.getLore().add(ChatColor.RED + "3 med-kits capable of greatly healing a player");
						stack.setItemMeta(meta);
						player.getInventory().addItem(stack);
						TraitorsAndMiners.instance.points.replace(player.getUniqueId(), newPoints);
					}
				}*/
			}
		}
		
		if (event.getInventory().getName().equalsIgnoreCase(teleportName)) {
			if (event.getCurrentItem().getType() == Material.SKULL_ITEM){
				final String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				if (event.getWhoClicked() instanceof Player) {
					final UUID uuid = event.getWhoClicked().getUniqueId();
					TraitorsAndMiners.instance.getServer().getPlayer(name).sendMessage(TraitorsAndMiners.instance.getServer().getPlayer(uuid).getDisplayName() + ChatColor.RED + " is teleporting to you in 5 seconds!");
					scheduler.scheduleSyncDelayedTask(TraitorsAndMiners.instance, new Runnable() {
						@Override
						public void run() {
							Location playerLoc = TraitorsAndMiners.instance.getServer().getPlayer(name).getLocation();
							TraitorsAndMiners.instance.getServer().getPlayer(uuid).sendMessage(ChatColor.DARK_BLUE + "Teleporting to " + name);
							TraitorsAndMiners.instance.getServer().getPlayer(uuid).teleport(playerLoc);
						}
					}, 100L);
				}
			}
			event.getWhoClicked().closeInventory();
			event.setCurrentItem(null);
		}
		
		} else {
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	private void openTraitorGUI(Player player) {
		Inventory traitorInv = Bukkit.createInventory(null, 9, traitorStore);
		
		// Store Item 1
		ItemStack stack = new ItemStack(Material.TNT,4);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Creeper4 Bomb");
			List lore = meta.getLore();
		if (meta.getLore() == null) {
			lore = new ArrayList(); 
		}
		lore.add(ChatColor.RED + "Spawns an explosion in 10 seconds.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Traitor Points: 4");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		traitorInv.setItem(0, stack);
		
		// Store Item 2
		ItemStack stack1 = new ItemStack(Material.DIAMOND_SWORD,6);
		ItemMeta meta1 = stack1.getItemMeta();
		meta1.setDisplayName(ChatColor.YELLOW + "Last Hope");
		List lore1 = meta1.getLore();
		if (lore1 == null) {
			lore1 = new ArrayList();
		}
		lore1.add(ChatColor.RED + "An enchanted sword with one hit left.");
		lore1.add("");
		lore1.add(ChatColor.GOLD + "Traitor Points: 6");
		meta1.setLore(lore1);
		stack1.setItemMeta(meta1);
		traitorInv.setItem(1, stack1);
		
		// Store Item 3
		ItemStack stack2 = new ItemStack(Material.SKULL_ITEM,8,(short)3);
		ItemMeta meta2 = stack2.getItemMeta();
		meta2.setDisplayName(ChatColor.DARK_RED + "Jihad Bomb");
		List lore2 = meta2.getLore();
		if (lore2 == null) {
			lore2 = new ArrayList();
		}
		int random = TraitorsAndMiners.instance.random.nextInt(1);
		switch(random) {
		case 0:
			lore2.add(ChatColor.RED + "Blow yourself up");
			break;
		case 1:
			lore2.add(ChatColor.RED + "Knock yourself out!");
			break;
		default:
			lore2.add(ChatColor.RED + "Blow yourself up");
			break;
		}
		lore2.add("");
		lore2.add(ChatColor.GOLD + "Traitor Points: 8");
		meta2.setLore(lore2);
		stack2.setItemMeta(meta2);
		traitorInv.setItem(2, stack2);
		
		// Store Item 4
		ItemStack stack3 = new ItemStack(Material.BONE,6);
		ItemMeta meta3 = stack3.getItemMeta();
		meta3.setDisplayName(ChatColor.DARK_PURPLE + "Decoy");
		List lore3 = meta3.getLore();
		if (lore3 == null) {
			lore3 = new ArrayList();
		}
		lore3.add(ChatColor.RED + "Hide yourself and fake your death.");
		lore3.add("");
		lore3.add(ChatColor.GOLD + "Traitor Points: 6");
		meta3.setLore(lore3);
		stack3.setItemMeta(meta3);
		traitorInv.setItem(3, stack3);

		ItemStack stack4 = new ItemStack(Material.GOLDEN_APPLE,4);
		ItemMeta meta4 = stack4.getItemMeta();
		meta4.setDisplayName(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "Half" + ChatColor.RESET + " " + ChatColor.GOLD + "Full-Life");
		List lore4 = meta4.getLore();
		if (lore4 == null) {
			lore4 = new ArrayList();
		}
		lore4.add(ChatColor.RED + "Gives you another set of hearts for 20 seconds");
		lore4.add("");
		lore4.add(ChatColor.GOLD + "Traitor Points: 4");
		meta4.setLore(lore4);
		stack4.setItemMeta(meta4);
		traitorInv.setItem(4,stack4);

		// End Item
		ItemStack stack9 = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta9 = stack9.getItemMeta();
		meta9.setDisplayName(ChatColor.LIGHT_PURPLE + "CLOSE INVENTORY");
		stack9.setItemMeta(meta9);
		traitorInv.setItem(8, stack9);
		
		player.openInventory(traitorInv);
	}
	
	@SuppressWarnings({"unchecked","rawtypes"})
	private void openDetectiveGUI(Player player) {
		Inventory detectiveInv = Bukkit.createInventory(null, 9, detectiveStore);
		
		// Store Item 1
		ItemStack stack = new ItemStack(Material.POTION,4);
		stack.setDurability((short)16421);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_AQUA + "Medical Kit");
		List lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList();
		}
		lore.add(ChatColor.RED + "3 med-kits capable of greatly healing a player");
		lore.add("");
		lore.add(ChatColor.GOLD + "Points: 4");
		meta.setLore(lore);
		stack.setItemMeta(meta);
		detectiveInv.setItem(0, stack);
		
		ItemStack stack1 = new ItemStack(Material.SHEARS,4);
		ItemMeta meta1 = stack1.getItemMeta();
		meta1.setDisplayName(ChatColor.YELLOW + "Wire Cutters");
		List lore1 = meta1.getLore();
		if (lore1 == null) {
			lore1 = new ArrayList();
		}
		lore1.add(ChatColor.RED + "Useful for defusing bombs");
		lore1.add("");
		lore1.add(ChatColor.GOLD + "Points: 4");
		meta1.setLore(lore1);
		stack1.setItemMeta(meta1);
		detectiveInv.setItem(1, stack1);
		
		/*ItemStack stack2 = new ItemStack(Material.NETHER_BRICK_ITEM,5);
		ItemMeta meta2 = stack2.getItemMeta();
		meta2.setDisplayName(ChatColor.LIGHT_PURPLE + "Request Teleport");
		List lore2 = meta2.getLore();
		if (lore2 == null) {
			lore2 = new ArrayList();
		}
		lore2.add(ChatColor.RED + "Teleport to any player");
		lore2.add(ChatColor.RED + "within a 20 block radius");
		lore2.add("");
		lore2.add(ChatColor.GOLD + "Points: 5");
		meta2.setLore(lore2);
		stack2.setItemMeta(meta2);
		detectiveInv.setItem(2, stack2);

		*/

		ItemStack stack3 = new ItemStack(Material.BOW,6);
		stack3.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 10);
		ItemMeta meta3 = stack3.getItemMeta();
		meta3.setDisplayName(ChatColor.GOLD + "Last Stand");
		List lore3 = meta3.getLore();
		if (lore3 == null) {
			lore3 = new ArrayList();
		}
		lore3.add(ChatColor.RED + "An enchanted bow with one shot left.");
		lore3.add("");
		lore3.add(ChatColor.GOLD + "Points: 6");
		meta3.setLore(lore3);
		stack3.setItemMeta(meta3);
		detectiveInv.setItem(2, stack3);
		
		// End Item
		ItemStack stack9 = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta meta9 = stack9.getItemMeta();
		meta9.setDisplayName(ChatColor.LIGHT_PURPLE + "CLOSE INVENTORY");
		stack9.setItemMeta(meta9);
		detectiveInv.setItem(8, stack9);
		
		player.openInventory(detectiveInv);
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}
	
}
