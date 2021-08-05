package eu.org.niberthix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class CommandEx implements CommandExecutor{
	
	private final Raveling plugin;
	
	public CommandEx(Raveling plugin) {
        this.plugin = plugin;
    }
	
	@SuppressWarnings("deprecation")
	public boolean check(LivingEntity mob, String type) {
		if(type.equals("ZOMBIE") || type.equals("HUSK") || type.equals("DROWNED") || type.equals("ZOMBIFIED_PIGLIN")) {
			Zombie ent = (Zombie) mob;
			if(ent.isBaby()) return false;
			else if(ent.getPassenger() != null) return false;
			else return true;
		}else if(type.equals("SKELETON") || type.equals("WITHER_SKELETON")) return true;
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (command.getName().equalsIgnoreCase("rvl")) {
    		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("rvl.reload")) {
                	plugin.reloadConfig();
                	try {
                		Raveling.config.load(Raveling.configFile);
						Raveling.players.load(Raveling.playersFile);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
                	
                	for(Player all : plugin.getServer().getOnlinePlayers()) {
                		int level = Raveling.players.getInt("Players." + all.getUniqueId() + ".level");
                        int xp = Raveling.players.getInt("Players." + all.getUniqueId() + ".xp");
                        
                        Raveling.players.set("Players." + all.getUniqueId() + ".level", level);
                        Raveling.players.set("Players." + all.getUniqueId() + ".xp", xp);
                        
                        if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                	        PlaceholderAPI.setPlaceholders(all, "level");
                	        PlaceholderAPI.setPlaceholders(all, "xp");
                        }
                        
                        Raveling.lvlManager.put(all.getUniqueId(), new PlayerLevel(level, xp));
                	}
                    
                    sender.sendMessage(clr("&2[&aRVL&2]&r Reloading"));
                    return true;
                }else {
                	sender.sendMessage(clr("&2[&aRVL&2]&r You Not Have Permission"));
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("item")){
            	if(sender.hasPermission("rvl.item")) {
	            	for(String equal : Raveling.config.getConfigurationSection("Items").getKeys(false)) {
		            	if(args.length == 2 && args[1].equalsIgnoreCase(equal)) {
		            		if(sender instanceof Player){
			            		Player p = (Player) sender;
	
			                    Material mat = Material.valueOf(Raveling.config.getString("Items."+equal+".material"));
			            		String name = Raveling.config.getString("Items."+equal+".name");
			                    int id = Raveling.config.getInt("Items."+equal+".id");
			                    List<String> effect = new ArrayList<>();
			            		for(String key : Raveling.config.getConfigurationSection("Items."+equal+".effect").getKeys(false)) {
			            			int level = Raveling.config.getInt("Items."+equal+".effect."+key);
			            			effect.add(key+":"+level);
			            		}
			            		
			                    ItemStack is = new ItemStack(mat, 1);
			                    ItemMeta im = is.getItemMeta();
			                    
			                    im.setDisplayName(clr(name));
			                    
			                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			                    im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			                    List<String> test = new ArrayList<>();
			                    test.add(""+id);
			                    test.add(clr("&r&fPotion Effect Mode &2&l>"));
			                    for(String effc : effect) {
			                    	String[] eff = effc.split(":");
			                    	int lvlenc = Integer.valueOf(eff[1]);
			                    	test.add(clr("&r&8+ &f"+eff[0]+": &7"+lvlenc));
		            			}
			                    test.add(clr(""));
			                    test.add(clr("&r&fEnhancement &2&l>"));
			                    if(Raveling.config.getString("Items."+equal+".enchant") != null) {
				                    for(String enchnt : Raveling.config.getConfigurationSection("Items."+equal+".enchant").getKeys(false)) {
				                    	int lvlenc = Raveling.config.getInt("Items."+equal+".enchant."+enchnt);
				                    	im.addEnchant(Enchantment.getByName(enchnt), lvlenc, true);
				                    	test.add(clr("&r&8+ &f"+enchnt+": &7"+lvlenc));
			            			}
			                    }
			                    im.setLore(test);
			                    is.setItemMeta(im);
			                    
			                    Raveling.itemdata.put(id, new ItemData(id, effect));
			
			                    p.getInventory().addItem(is);
		            		}
		            	}
	            	}
            	}
            }else if(args[0].equalsIgnoreCase("mission")){
            	for(String a : Raveling.config.getConfigurationSection("Missions").getKeys(false)) {
	            	if(args.length == 2 && args[1].equalsIgnoreCase(a)) {
	            		if(sender instanceof Player){
		            		Player p = (Player) sender;
		            		if(Raveling.mission.get(p.getUniqueId()) != null) {
		            			sender.sendMessage(clr("&2[&aRVL&2]&r Rejected Mission: "+Raveling.mission.get(p.getUniqueId()).g8()));
		            			Raveling.mission.remove(p.getUniqueId());
		            		}else {
			            		Raveling.mission.put(p.getUniqueId(), new Missions(a));
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Accepted Mission: "+Raveling.mission.get(p.getUniqueId()).g8()));
		            		}
	            		}
	            	}
            	}
            }else if(args[0].equalsIgnoreCase("kitmission")){
            	for(String a : Raveling.config.getConfigurationSection("KitMissions").getKeys(false)) {
	            	if(args.length == 2 && args[1].equalsIgnoreCase(a)) {
	            		if(sender instanceof Player){
		            		Player p = (Player) sender;
		            		new Guii(plugin).opMission(p, a);
	            		}
	            	}
            	}
            }else if (args[0].equalsIgnoreCase("mob")) {
	            if(sender.hasPermission("rvl.mob")) {
	            	long time = plugin.getServer().getWorld("world").getTime();
	            	Player player = (Player) sender;
	    			Location loc = player.getLocation();
	    			World wrld = player.getWorld();
	            	
	    			for(String equal : Raveling.config.getConfigurationSection("Monster").getKeys(false)) {
	        			if(args[1].equalsIgnoreCase(equal)) {
	        				if(args.length == 3 && isInt(args[2])) {
	        					int lvl = Integer.valueOf(args[2]);
	        					
		        				String type = Raveling.config.getString("Monster."+equal+".type");
		                		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.valueOf(type));
		    					mob.getEquipment().clear();
		    					String mainhand = Raveling.config.getString("Monster."+equal+".equipment.mainhand");
		    					String helmet = Raveling.config.getString("Monster."+equal+".equipment.helmet");
		    					String chestplate = Raveling.config.getString("Monster."+equal+".equipment.chestplate");
		    					String leggings = Raveling.config.getString("Monster."+equal+".equipment.leggings");
		    					String boots = Raveling.config.getString("Monster."+equal+".equipment.boots");
		    					
		    					if(mainhand != null) {
		    						if(mainhand.contains("-")) {
		    							String[] split = mainhand.split("-");
		    							ItemStack item = new ItemStack(Material.getMaterial(split[0]));
		    							for(int i = 1; i < split.length; i++) {
		    								item.addEnchantment(Enchantment.getByName(split[i]), 1);
		    							}
		    							mob.getEquipment().setItemInMainHand(item);
		    						}else {
		    							ItemStack item = new ItemStack(Material.getMaterial(mainhand));
		    							mob.getEquipment().setItemInMainHand(item);
		    						}
		    					}
		    					if(helmet != null) {
		    						if(helmet.contains("-")) {
		    							String[] split = helmet.split("-");
		    							ItemStack item = new ItemStack(Material.getMaterial(split[0]));
		    							for(int i = 1; i < split.length; i++) {
		    								item.addEnchantment(Enchantment.getByName(split[i]), 1);
		    							}
		    							mob.getEquipment().setHelmet(item);
		    						}else {
		    							ItemStack item = new ItemStack(Material.getMaterial(helmet));
		    							mob.getEquipment().setHelmet(item);
		    						}
		    					}
		    					if(chestplate != null) {
		    						if(chestplate.contains("-")) {
		    							String[] split = chestplate.split("-");
		    							ItemStack item = new ItemStack(Material.getMaterial(split[0]));
		    							for(int i = 1; i < split.length; i++) {
		    								item.addEnchantment(Enchantment.getByName(split[i]), 1);
		    							}
		    							mob.getEquipment().setChestplate(item);
		    						}else {
		    							ItemStack item = new ItemStack(Material.getMaterial(chestplate));
		    							mob.getEquipment().setChestplate(item);
		    						}
		    					}
		    					if(leggings != null) {
		    						if(leggings.contains("-")) {
		    							String[] split = leggings.split("-");
		    							ItemStack item = new ItemStack(Material.getMaterial(split[0]));
		    							for(int i = 1; i < split.length; i++) {
		    								item.addEnchantment(Enchantment.getByName(split[i]), 1);
		    							}
		    							mob.getEquipment().setLeggings(item);
		    						}else {
		    							ItemStack item = new ItemStack(Material.getMaterial(leggings));
		    							mob.getEquipment().setLeggings(item);
		    						}
		    					}
		    					if(boots != null) {
		    						if(boots.contains("-")) {
		    							String[] split = boots.split("-");
		    							ItemStack item = new ItemStack(Material.getMaterial(split[0]));
		    							for(int i = 1; i < split.length; i++) {
		    								item.addEnchantment(Enchantment.getByName(split[i]), 1);
		    							}
		    							mob.getEquipment().setBoots(item);
		    						}else {
		    							ItemStack item = new ItemStack(Material.getMaterial(boots));
		    							mob.getEquipment().setBoots(item);
		    						}
		    					}
		                		
		    	            	if(!check(mob, type)) {
		    	            		mob.remove();
		    	            		if(mob.getPassenger() != null) mob.getPassenger().remove();
		    	            		sender.sendMessage("&2[&aRVL&2]&r We have a problem, try again");
		    	            	}else if(time > 0 && time < 12300) {
		    	            		mob.remove();
		    	            		sender.sendMessage("&2[&aRVL&2]&r Do not spawn at day");
		    	                }else {
		    	                	String name = Raveling.config.getString("Monster."+equal+".name");
		    	            		mob.setCustomName(clr("&c"+name+"&7[&f"+lvl+"&7]"));
		    	            		mob.setCustomNameVisible(true);
		    	            		mob.setRemoveWhenFarAway(false);
		    	            		mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		    		        		.setBaseValue(Raveling.config.getDouble("Monster."+equal+".speed"));
		    	            		
		    	            		Raveling.mdata.put(mob.getUniqueId(), new MobData(lvl, loc, equal, new ArrayList<>(), false));
		    	            		
		    	            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
		    						try {
		    							Raveling.saves.save(Raveling.savesFile);
		    						} catch (IOException e) {
		    							e.printStackTrace();
		    						}
		    	            	}
	        				}
	                	}
	        		}
            	}else {
            		sender.sendMessage(clr("&2[&aRVL&2]&r You Not Have Permission"));
                    return true;
            	}
			}else if(args[0].equalsIgnoreCase("killspawnmob")) {
				if(sender.hasPermission("rvl.killspawnmob")) {
					if(args.length == 2) {
						if(args[1].equalsIgnoreCase("all")) {
		        			for(World world : plugin.getServer().getWorlds()) {
		        		        for(Entity all : world.getEntities()) {
		        		        	if(Raveling.mdata.get(all.getUniqueId()) != null) {
		        		        		all.remove();
		        		        	}
		        		    	}
		        	        }
		        			Raveling.mdata.clear();
							Raveling.saves.set("Mobld", null);
							Raveling.saves.set("Mobs", null);
		        			sender.sendMessage(clr("&2[&aRVL&2]&r All Spawner Deleted"));
						}
		        		
	        		}else if(sender instanceof Player){
		    			Player player = (Player) sender;
		    			Boolean hahah = false;
		    			for(Entity all : player.getWorld().getEntities()) {
	    		        	if(Raveling.mdata.get(all.getUniqueId()) != null) {
	    		        		MobData mob = Raveling.mdata.get(all.getUniqueId());
		    		        	int xm = mob.getMloc().getBlockX();
		    		        	int ym = mob.getMloc().getBlockY();
		    		        	int zm = mob.getMloc().getBlockZ();
		    		        	int xp = player.getLocation().getBlockX();
		    		        	int yp = player.getLocation().getBlockY();
		    		        	int zp = player.getLocation().getBlockZ();
		    		        	if(xm == xp && ym == yp && zm == zp) {
		    		        		Raveling.mdata.remove(all.getUniqueId());
		    		        		all.remove();
		    		        		Raveling.saves.set("Mobld."+all, null);
		    		        		hahah = true;
		    		        		sender.sendMessage(clr("&2[&aRVL&2]&r Spawner Deleted"));
		    		        	}
		    		    	}
		    	        }
		    			if(!hahah) {
			        		sender.sendMessage(clr("&2[&aRVL&2]&r Spawner not Exist"));
			        	}
					}
				}else {
					sender.sendMessage(clr("&2[&aRVL&2]&r You Not Have Permission"));
                    return true;
				}
        	}else if(args[0].equalsIgnoreCase("listspawnmob")) {
        		if(sender.hasPermission("rvl.listspawnmob")) {
	        		if(Raveling.saves.get("Mobld") == null) {
	        			sender.sendMessage(clr("&2[&aRVL&2]&r No Spawn Exist"));
	        		}else {
		        		for(String list : Raveling.saves.getConfigurationSection("Mobld").getKeys(false)) {
		        			Location loc = Raveling.saves.getLocation("Mobld."+list);
		        			int x = loc.getBlockX();
		        			int y = loc.getBlockY();
		        			int z = loc.getBlockZ();
			        		sender.sendMessage(clr("&2[&aRVL&2]&r "+list+": "+x+" "+y+" "+z));
				    	}
	        		}
        		}else {
        			sender.sendMessage(clr("&2[&aRVL&2]&r You Not Have Permission"));
                    return true;
        		}
        	}else {
            	sender.sendMessage(clr("&2[&aRVL&2]&r I Dont Know What Are You Doing"));
            }
        }
        return true;
    }
	
	public String clr(String msg) {
    	return ChatColor.translateAlternateColorCodes('&',msg);
    }
	
	public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
