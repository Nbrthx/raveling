package eu.org.niberthix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

public class Spawner {
	
	private final Raveling plugin;
	
	public Spawner(Raveling plugin, UUID neweu) {
		this.plugin = plugin;
		if(neweu != null) spawn(neweu);
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
	
	public void spawn(UUID neweu) {
		BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
            	
            	long time = plugin.getServer().getWorld("world").getTime();
            	MobData mobd = Raveling.mdata.get(neweu);
            	
        		Location mlocs = mobd.getMloc();
        		int mlvls = mobd.getMlvl();
        		String et = mobd.getMcty();
        		
        		if(!(time > 0 && time < 12300)) {
	        		for(String equal : Raveling.config.getConfigurationSection("Monster").getKeys(false)) {
	        			if(et.equals(equal)) {
	        				String type = Raveling.config.getString("Monster."+equal+".type");
	                		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.valueOf(type));
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
	    	            		run();
	    	            	}else {
	    	                	String name = Raveling.config.getString("Monster."+equal+".name");
	    	            		mob.setCustomName(clr("&c"+name+"&7[&f"+mlvls+"&7]"));
	    	            		mob.setCustomNameVisible(true);
	    	            		mob.setRemoveWhenFarAway(false);
	    	            		mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
	    		        		.setBaseValue(Raveling.config.getDouble("Monster."+equal+".speed"));
	    	            		
	    	            		Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, equal, new ArrayList<>(), false));
	    	            		Raveling.mdata.remove(neweu);
	    	            		
	    	            		Raveling.saves.set("Mobld."+neweu, null);
	    	            		Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
	    						try {
	    							Raveling.saves.save(Raveling.savesFile);
	    						} catch (IOException e) {
	    							e.printStackTrace();
	    						}
	    	            	}
	                	}
	        		}
	            }else {
	            	spawn(neweu);
	            }
            }
        }, 200L);
	}
	
	public void lifeTaker(){
    	long attime = 23900;
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
            @Override
            public void run() {
            	long time = plugin.getServer().getWorld("world").getTime();
            	if(time >= attime) {
	                for(World world : plugin.getServer().getWorlds()) {
	                	for(LivingEntity e : world.getLivingEntities()) {
	                		if(Raveling.mdata.get(e.getUniqueId()) != null) {
		                		e.setHealth(1);
		                		e.damage(100);
	                		}
	                	}
	                }
            	}
            }
        }, 20, 20);
    }
	
	public String clr(String msg) {
    	return ChatColor.translateAlternateColorCodes('&',msg);
    }
}
