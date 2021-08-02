package eu.org.niberthix;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
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
	public void spawn(UUID neweu) {
		BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
            	
            	long time = plugin.getServer().getWorld("world").getTime();
            	MobData mobd = Raveling.mdata.get(neweu);
            	
        		Location mlocs = mobd.getMloc();
        		int mlvls = mobd.getMlvl();
        		int et = mobd.getMcty();
        		
            	if(et == 0) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.ZOMBIE);
	            	Zombie ent = (Zombie) mob;
					ent.getEquipment().clear();
					
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else if(time > 0 && time < 12300) {
	            		spawn(neweu);
	            		ent.remove();
	                }else {
	            		ent.setAge(100);
	            		ent.setCustomName(clr("&cZombie &7[&f"+mlvls+"&7]"));
	            		ent.setCustomNameVisible(true);
	            		ent.setRemoveWhenFarAway(false);
	            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.0.speed"));
	            		
	            		Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 0, false));
	            		Raveling.mdata.remove(neweu);
	            		
	            		Raveling.saves.set("Mobld."+neweu, null);
	            		Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 1) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.SKELETON);
	            	Skeleton ent = (Skeleton) mob;
					ent.getEquipment().clear();
					ent.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
					
					if(time > 0 && time < 12300) {
						spawn(neweu);
	            		ent.remove();
	                }else {
						ent.setCustomName(clr("&cSkeleton &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.1.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 1, false));
	            		
						Raveling.saves.set("Mobld."+neweu, null);
						Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	                }
            	}else if(et == 2) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.HUSK);
	            	Husk ent = (Husk) mob;
					ent.getEquipment().clear();
	            	
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else if(time > 0 && time < 12300) {
	            		ent.remove();
	            		spawn(neweu);
	                }else {
						ent.setCustomName(clr("&cHusk &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.2.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 2, false));
	            		
						Raveling.saves.set("Mobld."+neweu, null);
						Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 3) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.DROWNED);
	            	Drowned ent = (Drowned) mob;
					ent.getEquipment().clear();
	            	
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else if(time > 0 && time < 12300) {
	            		ent.remove();
	            		spawn(neweu);
	                }else {
						ent.setCustomName(clr("&cDrowned &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.3.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 3, false));
	            		
						Raveling.saves.set("Mobld."+neweu, null);
						Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 4) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.WITHER_SKELETON);
	            	WitherSkeleton ent = (WitherSkeleton) mob;
					ent.getEquipment().clear();
	            	
	            	if(time > 0 && time < 12300) {
	            		ent.remove();
	            		spawn(neweu);
	                }else {
						ent.setCustomName(clr("&cWither Skeleton &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.4.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 4, false));
	            		
						Raveling.saves.set("Mobld."+neweu, null);
						Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 5) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.ZOMBIE);
	            	Zombie ent = (Zombie) mob;
					ent.getEquipment().clear();
					ent.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
					ent.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
					
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else if(time > 0 && time < 12300) {
	            		spawn(neweu);
	            		ent.remove();
	                }else {
	            		ent.setAge(100);
	            		ent.setCustomName(clr("&cBOSS Zombie &7[&f"+mlvls+"&7]"));
	            		ent.setCustomNameVisible(true);
	            		ent.setRemoveWhenFarAway(false);
	            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.5.speed"));
	            		
	            		Raveling.mdata.remove(neweu);
	            		Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 5, false));
	            		
	            		Raveling.saves.set("Mobld."+neweu, null);
	            		Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 6) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.ZOMBIFIED_PIGLIN);
	            	PigZombie ent = (PigZombie) mob;
					ent.getEquipment().clear();
					
	            	if(time > 0 && time < 12300) {
	            		spawn(neweu);
	            		ent.remove();
	                }else {
	            		ent.setCustomName(clr("&cZombie Pigman &7[&f"+mlvls+"&7]"));
	            		ent.setCustomNameVisible(true);
	            		ent.setRemoveWhenFarAway(false);
	            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.6.speed"));
	            		
	            		Raveling.mdata.remove(neweu);
	            		Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 6, false));
	            		
	            		Raveling.saves.set("Mobld."+neweu, null);
	            		Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	            	}
            	}else if(et == 7) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.SKELETON);
	            	Skeleton ent = (Skeleton) mob;
					ent.getEquipment().clear();
					ItemStack bowe = new ItemStack(Material.BOW);
					bowe.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
					ent.getEquipment().setItemInMainHand(bowe);
					ent.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
					ent.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
					
					if(time > 0 && time < 12300) {
						spawn(neweu);
	            		ent.remove();
	                }else {
						ent.setCustomName(clr("&cBOSS Skeleton &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.7.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 7, false));
	            		
						Raveling.saves.set("Mobld."+neweu, null);
						Raveling.saves.set("Mobld."+mob.getUniqueId(), mlocs);
						try {
							Raveling.saves.save(Raveling.savesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
	                }
            	}else if(et == 8) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.HUSK);
	            	Husk ent = (Husk) mob;
					ent.getEquipment().clear();
					ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
					ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
					ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
					
					if(ent.isBaby()) {
	            		ent.remove();
	            		spawn(neweu);
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		spawn(neweu);
	            	}else if(time > 0 && time < 12300) {
						spawn(neweu);
	            		ent.remove();
	                }else {
						ent.setCustomName(clr("&cBOSS Husk &7[&f"+mlvls+"&7]"));
						ent.setCustomNameVisible(true);
						ent.setRemoveWhenFarAway(false);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(Raveling.config.getDouble("Monster.8.speed"));
						
						Raveling.mdata.remove(neweu);
						Raveling.mdata.put(mob.getUniqueId(), new MobData(mlvls, mlocs, 8, false));
	            		
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
        }, 200L);
	}
	
	public void lifeTaker(){
    	long attime = 23960;
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
