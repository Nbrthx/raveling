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
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
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
	            	
	            	if(args[1].equalsIgnoreCase("zombie")) {
		            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
			            	
			    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.ZOMBIE);
							Zombie ent = (Zombie) mob;
							ent.getEquipment().clear();
							
							int argint = Integer.parseInt(args[2]);
							
							if(ent.isBaby()) {
								ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            		return true;
			            	}else if(ent.getPassenger() != null) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            		return true;
			            	}else if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
			            		ent.setAge(100);
			            		ent.setCustomName(clr("&cZombie &7[&f"+argint+"&7]"));
			            		ent.setCustomNameVisible(true);
			            		ent.setRemoveWhenFarAway(false);
			            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.0.speed"));
			            		
			            		Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 0, false));
			            		
			            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
		            	}
	            	}else if(args[1].equalsIgnoreCase("skeleton")) {
		            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
			    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.SKELETON);
							Skeleton ent = (Skeleton) mob;
							ent.getEquipment().clear();
							ent.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
							
							int argint = Integer.parseInt(args[2]);
							
							if(time > 0 && time < 12300) {
								ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
			            		ent.setCustomName(clr("&cSkeleton &7[&f"+argint+"&7]"));
			            		ent.setCustomNameVisible(true);
			            		ent.setRemoveWhenFarAway(false);
			            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.1.speed"));
			            		
			            		Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 1, false));
			            		
			            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			                }
		            	}
	            	}else if(args[1].equalsIgnoreCase("husk")) {
	            		if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		            		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.HUSK);
			            	Husk ent = (Husk) mob;
			            	ent.getEquipment().clear();
			            	
			            	int argint = Integer.parseInt(args[2]);
			            	
			            	if(ent.isBaby()) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(ent.getPassenger() != null) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
								ent.setCustomName(clr("&cHusk &7[&f"+argint+"&7]"));
								ent.setCustomNameVisible(true);
								ent.setRemoveWhenFarAway(false);
								ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.2.speed"));
								
								Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 2, false));
								
								Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
	            		}
	            	}else if(args[1].equalsIgnoreCase("drowned")) {
	            		if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		            		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.DROWNED);
			            	Drowned ent = (Drowned) mob;
			            	ent.getEquipment().clear();
			            	
			            	int argint = Integer.parseInt(args[2]);
			            	
			            	if(ent.isBaby()) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(ent.getPassenger() != null) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
								ent.setCustomName(clr("&cDrowned &7[&f"+argint+"&7]"));
								ent.setCustomNameVisible(true);
								ent.setRemoveWhenFarAway(false);
								ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.3.speed"));
								
								Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 3, false));
								
								Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
	            		}
	            	}else if(args[1].equalsIgnoreCase("wither_skeleton")) {
	            		if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		            		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.WITHER_SKELETON);
			            	WitherSkeleton ent = (WitherSkeleton) mob;
			            	ent.getEquipment().clear();
			            	
			            	int argint = Integer.parseInt(args[2]);
			            	
			            	if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
								ent.setCustomName(clr("&cWither Skeleton &7[&f"+argint+"&7]"));
								ent.setCustomNameVisible(true);
								ent.setRemoveWhenFarAway(false);
								ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.4.speed"));
								
								Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 4, false));
								
								Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
	            		}
	            	}else if(args[1].equalsIgnoreCase("boss_zombie")) {
		            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
			            	
			    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.ZOMBIE);
							Zombie ent = (Zombie) mob;
							ent.getEquipment().clear();
							ent.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
							ent.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
							
							int argint = Integer.parseInt(args[2]);
							
							if(ent.isBaby()) {
								ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            		return true;
			            	}else if(ent.getPassenger() != null) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            		return true;
			            	}else if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
			            		ent.setAge(100);
			            		ent.setCustomName(clr("&cBOSS Zombie &7[&f"+argint+"&7]"));
			            		ent.setCustomNameVisible(true);
			            		ent.setRemoveWhenFarAway(false);
			            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.5.speed"));
			            		
			            		Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 5, false));
			            		
			            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
		            	}
	            	}else if(args[1].equalsIgnoreCase("zombie_pigman")) {
		            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
			            	
			    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.ZOMBIFIED_PIGLIN);
							PigZombie ent = (PigZombie) mob;
							ent.getEquipment().clear();
							
							int argint = Integer.parseInt(args[2]);
							
							if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
			            		ent.setCustomName(clr("&cZombie Pigman &7[&f"+argint+"&7]"));
			            		ent.setCustomNameVisible(true);
			            		ent.setRemoveWhenFarAway(false);
			            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.6.speed"));
			            		
			            		Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 6, false));
			            		
			            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
		            	}
	            	}else if(args[1].equalsIgnoreCase("boss_skeleton")) {
		            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
			            	
			    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.SKELETON);
							Skeleton ent = (Skeleton) mob;
							ent.getEquipment().clear();
							ItemStack bowe = new ItemStack(Material.BOW);
							bowe.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
							ent.getEquipment().setItemInMainHand(bowe);
							ent.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
							ent.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
							
							int argint = Integer.parseInt(args[2]);
							
							if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
			            		ent.setCustomName(clr("&cBOSS Skeleton &7[&f"+argint+"&7]"));
			            		ent.setCustomNameVisible(true);
			            		ent.setRemoveWhenFarAway(false);
			            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.7.speed"));
			            		
			            		Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 7, false));
			            		
			            		Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
			            	}
		            	}
	            	}else if(args[1].equalsIgnoreCase("boss_husk")) {
	            		if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		            		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.HUSK);
			            	Husk ent = (Husk) mob;
			            	ent.getEquipment().clear();
			            	ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
							ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
							ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
							ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
							
			            	int argint = Integer.parseInt(args[2]);
			            	
			            	if(ent.isBaby()) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(ent.getPassenger() != null) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
			            	}else if(time > 0 && time < 12300) {
			            		ent.remove();
			            		sender.sendMessage(clr("&2[&aRVL&2]&r Dont spawn at Day"));
			                }else {
								ent.setCustomName(clr("&cBOSS Husk &7[&f"+argint+"&7]"));
								ent.setCustomNameVisible(true);
								ent.setRemoveWhenFarAway(false);
								ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
				        		.setBaseValue(Raveling.config.getDouble("Monster.8.speed"));
								
								Raveling.mdata.put(mob.getUniqueId(), new MobData(argint, loc, 8, false));
								
								Raveling.saves.set("Mobld."+mob.getUniqueId(), loc);
								try {
			                		Raveling.saves.save(Raveling.savesFile);
								} catch (IOException e) {
									e.printStackTrace();
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
		    			for(World world : plugin.getServer().getWorlds()) {
		    		        for(Entity all : world.getEntities()) {
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
			    		        		Raveling.saves.set("Mobld."+all.getUniqueId(), null);
			    		        		hahah = true;
			    		        		sender.sendMessage(clr("&2[&aRVL&2]&r Spawner Deleted"));
			    		        	}
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
