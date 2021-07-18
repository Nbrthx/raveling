package com.niberthix.Raveling;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener{
	
	Plugin plugin;
	
	public static File playersFile;
	public static FileConfiguration players;
	public static File mobsFile;
	public static FileConfiguration mobs;
	public static File configFile;
	public static FileConfiguration config;

    HashMap<UUID, PlayerLevelManager> levelManagerHashMap;
    HashMap<UUID, Integer> mlvl;
    HashMap<UUID, Location> mloc;
    
    public static void playerFile(Plugin data) {
		if(!data.getDataFolder().exists()) {
			data.getDataFolder().mkdirs();
		}
		
		playersFile = new File(data.getDataFolder(), "players.yml");
		if(!playersFile.exists()) {
			data.saveResource("players.yml", false);
		}
		players = YamlConfiguration.loadConfiguration(playersFile);
	}
    public static void mobFile(Plugin data) {
		if(!data.getDataFolder().exists()) {
			data.getDataFolder().mkdirs();
		}
		
		mobsFile = new File(data.getDataFolder(), "mobs.yml");
		if(!mobsFile.exists()) {
			data.saveResource("mobs.yml", false);
		}
		mobs = YamlConfiguration.loadConfiguration(mobsFile);
	}
	
	@Override
    public void onEnable() {
		getServer().getConsoleSender()
		.sendMessage(clr("&aRaveling&r has been Enabled"));
		PluginManager pm = getServer().getPluginManager();
		plugin = this;
        this.levelManagerHashMap = new HashMap<>();
        this.mlvl = new HashMap<>();
        this.mloc = new HashMap<>();
        pm.registerEvents(this, this);
        this.getConfig().options().copyDefaults(true);
        this.getServer().getWorld("world").setGameRule(GameRule.DO_MOB_SPAWNING, false);
        this.saveConfig();
        playerFile(this);
        mobFile(this);
        
        this.getServer().getPluginCommand("rvl").setTabCompleter(new plTabCompleter());
        
        for(Player all : this.getServer().getOnlinePlayers()) {
    		int level = players.getInt("Players." + all.getUniqueId() + ".level");
            int xp = players.getInt("Players." + all.getUniqueId() + ".xp");
            
            levelManagerHashMap.put(all.getUniqueId(), new PlayerLevelManager(level, xp));
    	}
        World world = this.getServer().getWorld("world");
        for(Entity all : world.getEntities()) {
        	if(mobs.get("Mobs."+all.getUniqueId()+".level") != null) {
        		int mlvls = mobs.getInt("Mobs."+all.getUniqueId()+".level");
	            Location mlocs = mobs.getLocation("Mobs." + all.getUniqueId() + ".location");
	            
	            mlvl.put(all.getUniqueId(), mlvls);
	            mloc.put(all.getUniqueId(), mlocs);
	            mobs.set("Mobs", null);
	            try {
	    			mobs.save(mobsFile);
	    			mobs.load(mobsFile);
	    		} catch (Exception n) {
	    			n.printStackTrace();
	    		}
        	}
    	}
    }

    @Override
    public void onDisable() {
    	getServer().getConsoleSender()
    	.sendMessage(clr("&aRaveling&r has been Disabled"));
    	
    	for(Player all : this.getServer().getOnlinePlayers()) {
    		PlayerLevelManager plm = levelManagerHashMap.get(all.getUniqueId());
    		int level = plm.getLevel();
            int xp = plm.getXp();
            
            players.set("Players." + all.getUniqueId() + ".level", level);
            players.set("Players." + all.getUniqueId() + ".xp", xp);
    	}
    	World world = this.getServer().getWorld("world");
    	for(Entity all : world.getEntities()) {
        	if(mlvl.get(all.getUniqueId()) != null) {
        		getServer().getConsoleSender()
        		.sendMessage(ChatColor.GREEN + "MissionRush has been Enabled "+all.getUniqueId());
	            mobs.set("Mobs."+all.getUniqueId()+".level", mlvl.get(all.getUniqueId()));
	            mobs.set("Mobs."+all.getUniqueId()+".location", mloc.get(all.getUniqueId()));
        	}
    	}
    	
    	try {
			players.save(playersFile);
			mobs.save(mobsFile);
		} catch (IOException n) {
			n.printStackTrace();
		}
    	
        this.levelManagerHashMap.clear();
        this.mlvl.clear();
        this.mloc.clear();
    }
    
    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        event.setJoinMessage(clr(""+player.getDisplayName()+"&r &aJoined The Game!"));
        
        if (this.levelManagerHashMap.get(player.getUniqueId()) == null) {
            player.sendMessage(clr("&bWelcome to "+getConfig().getString("Info.title")));

            this.levelManagerHashMap.put(player.getUniqueId(), new PlayerLevelManager(1, 0));
            players.set("Players." + player.getUniqueId() + ".level", 1);
            players.set("Players." + player.getUniqueId() + ".xp", 0);
            try {
    			players.save(playersFile);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

            new Scoreboards(player, 1, 0, plugin);
        } else {
            int level = players.getInt("Players." + player.getUniqueId() + ".level");
            int xp = players.getInt("Players." + player.getUniqueId() + ".xp");
            levelManagerHashMap.put(player.getUniqueId(), new PlayerLevelManager(level, xp));
            new Scoreboards(player, level, xp, plugin);
        }
    }

	@EventHandler
    public void quit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage(clr(""+player.getDisplayName()+"&r &7Left The Game!"));
        int level = players.getInt("Players."+player.getUniqueId()+".level");
        int xp = players.getInt("Players."+player.getUniqueId()+".xp");

        if (level != 0) {
            players.set("Players." + player.getUniqueId() + ".level", level);
            players.set("Players." + player.getUniqueId() + ".xp", xp);
            try {
    			players.save(playersFile);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    }
    
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (command.getName().equalsIgnoreCase("rvl")) {
            if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("rvl.reload")) {
                	this.reloadConfig();
                	try {
						players.load(playersFile);
						mobs.load(mobsFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
                	
                	for(Player all : Bukkit.getServer().getOnlinePlayers()) {
                		int level = players.getInt("Players." + all.getUniqueId() + ".level");
                        int xp = players.getInt("Players." + all.getUniqueId() + ".xp");
                        
                        players.set("Players." + all.getUniqueId() + ".level", level);
                        players.set("Players." + all.getUniqueId() + ".xp", xp);
                        
                        levelManagerHashMap.put(all.getUniqueId(), new PlayerLevelManager(level, xp));
                        new Scoreboards(all, level, xp, plugin);
                	}
                    
                    sender.sendMessage(clr("&2[&aRVL&2]&r Reloading"));
                    return true;
                }else {
                	sender.sendMessage(clr("&2[&aRVL&2]&r You Not Have Permission"));
                    return true;
                }
            }
            if(args[0].equalsIgnoreCase("item")){
            	if(args[1].equalsIgnoreCase("starter")) {
            		if(sender instanceof Player){
	            		Player p = (Player) sender;
	                    ItemStack is = new ItemStack(Material.WOODEN_SWORD, 1);
	                    ItemMeta im = is.getItemMeta();
	                    im.setDisplayName(clr("&bBeginer Sword"));
	                    im.addEnchant(Enchantment.DURABILITY, 3, true);
	                    im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	                    im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	                    List<String> test = new ArrayList<>();
	                    test.add("Beginer Sword");
	                    test.add("For Starter Player");
	                    im.setLore(test);
	                    is.setItemMeta(im);
	
	                    p.getInventory().addItem(is);
            		}
            	}
            }
            if (args[0].equalsIgnoreCase("mob")) {
            	
            	Player player = (Player) sender;
    			Location loc = player.getLocation();
    			World wrld = player.getWorld();
            	
            	if(args[1].equalsIgnoreCase("zombie")) {
	            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		            	
		    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.ZOMBIE);
						Zombie ent = (Zombie) mob;
						
						int argint = Integer.parseInt(args[2]);
						
						if(ent.isBaby()) {
							ent.remove();
		            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
		            		return true;
		            	}else if(ent.getPassenger() != null) {
		            		ent.remove();
		            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
		            		return true;
		            	}else {
		            		ent.setAge(100);
		            		ent.setCustomName(clr("&cZombie &7[&f"+argint+"&7]"));
		            		ent.setCustomNameVisible(true);
		            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			        		.setBaseValue(0.3);
		            		
		            		mlvl.put(mob.getUniqueId(), argint);
		            		mloc.put(mob.getUniqueId(), loc);
		            	}
	            	}
            	}else if(args[1].equalsIgnoreCase("skeleton")) {
	            	if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
		    			LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.SKELETON);
						Skeleton ent = (Skeleton) mob;
						
						int argint = Integer.parseInt(args[2]);
						
	            		ent.setCustomName(clr("&cSkeleton &7[&f"+argint+"&7]"));
	            		ent.setCustomNameVisible(true);
	            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(0.25);
	            		
	            		mlvl.put(mob.getUniqueId(), argint);
	            		mloc.put(mob.getUniqueId(), loc);
	            	}
            	}else if(args[1].equalsIgnoreCase("husk")) {
            		if(args.length == 3 && isInt(args[2]) && sender instanceof Player){
	            		LivingEntity mob = (LivingEntity) wrld.spawnEntity(loc, EntityType.HUSK);
		            	Husk ent = (Husk) mob;
		            	
		            	int argint = Integer.parseInt(args[2]);
		            	
		            	if(ent.isBaby()) {
		            		ent.remove();
		            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
		            	}else if(ent.getPassenger() != null) {
		            		ent.remove();
		            		sender.sendMessage(clr("&2[&aRVL&2]&r We Have Problem, Try Again"));
		            	}else {
							ent.setCustomName(clr("&cHusk &7[&f"+argint+"&7]"));
							ent.setCustomNameVisible(true);
							ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
			        		.setBaseValue(0.35);
							
							mlvl.put(mob.getUniqueId(), argint);
							mloc.put(mob.getUniqueId(), loc);
		            	}
            		}
            	}
			}else {
            	sender.sendMessage(clr("&2[&aRVL&2]&r I Dont Know What Are You Doing"));
            }
        }
        return true;
    }
    
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
    	
		Entity damager = event.getDamager();
		Entity e = event.getEntity();
		double dmg = event.getDamage();
		
		
		
    	if(e instanceof Player) {
    		int lvl = players.getInt("Players."+e.getUniqueId()+".level");
    		double mxhlth = 20 * lvl;
    		
    		if(damager.getType() == EntityType.ZOMBIE && mlvl.get(damager.getUniqueId()) != null) {
    			dmg = mlvl.get(damager.getUniqueId()) * 2;
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(damager.getType() == EntityType.ARROW) {
    			Arrow arrow = (Arrow) damager;
    			Entity shotofarrow = (Entity) arrow.getShooter();
    			if(shotofarrow.getType() == EntityType.SKELETON && mlvl.get(shotofarrow.getUniqueId()) != null) {
	    			dmg = mlvl.get(shotofarrow.getUniqueId()) * 5;
	    			event.setDamage(dmg * 20 / mxhlth);
    			}
    		}else if(damager.getType() == EntityType.HUSK && mlvl.get(damager.getUniqueId()) != null) {
    			dmg = mlvl.get(damager.getUniqueId()) * 3;
    			event.setDamage(dmg * 20 / mxhlth);
    		}else {
    			event.setDamage(dmg * 20 / mxhlth);
    		}
    		
    	}else if(e instanceof Zombie && mlvl.get(e.getUniqueId()) != null) {
    		
    		double mxhlth = 10 * mlvl.get(e.getUniqueId());
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(e instanceof Skeleton && mlvl.get(e.getUniqueId()) != null) {
    		
    		double mxhlth = 10 * mlvl.get(e.getUniqueId());
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(e instanceof Husk && mlvl.get(e.getUniqueId()) != null) {
    		
    		double mxhlth = 15 * mlvl.get(e.getUniqueId());
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}
    }	
    	
    
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
    	Player player = event.getEntity().getKiller();
    	Entity e = event.getEntity();
    	
    	event.getDrops().clear();
		event.setDroppedExp(0);
    	
		if(player instanceof Player) {
			
			PlayerLevelManager playerLevelManager = this.levelManagerHashMap.get(player.getUniqueId());
        	int xp = playerLevelManager.getXp();
	        
	        if (e.getType() == EntityType.ZOMBIE && mlvl.get(e.getUniqueId()) != null) {
	        	playerLevelManager.setXp(xp + 
	        			(this.getConfig().getInt("Monster.zombie") * mlvl.get(e.getUniqueId())));
	            player.sendMessage(clr("&a+"+
	        			(this.getConfig().getInt("Monster.zombie") * mlvl.get(e.getUniqueId()))+" &bExperience"));
	            xpcheck(player, playerLevelManager);
	            spawn(e.getUniqueId(), e.getType());
	        }
	        if (e.getType() == EntityType.SKELETON && mlvl.get(e.getUniqueId()) != null) {
	        	playerLevelManager.setXp(xp + 
	        			(this.getConfig().getInt("Monster.skeleton") * mlvl.get(e.getUniqueId())));
	            player.sendMessage(clr("&a+"+
	        			(this.getConfig().getInt("Monster.skeleton") * mlvl.get(e.getUniqueId()))+" &bExperience"));
	            xpcheck(player, playerLevelManager);
	            spawn(e.getUniqueId(), e.getType());
	        }
	        if (e.getType() == EntityType.HUSK && mlvl.get(e.getUniqueId()) != null) {
	        	playerLevelManager.setXp(xp + 
	        			(this.getConfig().getInt("Monster.husk") * mlvl.get(e.getUniqueId())));
	            player.sendMessage(clr("&a+"+
	        			(this.getConfig().getInt("Monster.husk") * mlvl.get(e.getUniqueId()))+" &bExperience"));
	            xpcheck(player, playerLevelManager);
	            spawn(e.getUniqueId(), e.getType());
	        }
        }
    }
    
	@SuppressWarnings("deprecation")
	public void spawn(UUID neweu, EntityType et) {
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
            	
            	int mlvls = mlvl.get(neweu);
            	Location mlocs = mloc.get(neweu);
            	
            	if(et == EntityType.ZOMBIE) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.ZOMBIE);
	            	Zombie ent = (Zombie) mob;
	            	
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else {
	            		ent.setAge(100);
	            		ent.setCustomName(clr("&cZombie &7[&f"+mlvls+"&7]"));
	            		ent.setCustomNameVisible(true);
	            		ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(0.35);
	            		
	            		mlvl.put(mob.getUniqueId(), mlvls);
	            		mloc.put(mob.getUniqueId(), mlocs);
	            	}
            	}else if(et == EntityType.SKELETON) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.SKELETON);
	            	Skeleton ent = (Skeleton) mob;
	            	
					ent.setCustomName(clr("&cSkeleton &7[&f"+mlvls+"&7]"));
					ent.setCustomNameVisible(true);
					ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
	        		.setBaseValue(0.30);
					
					mlvl.put(mob.getUniqueId(), mlvls);
            		mloc.put(mob.getUniqueId(), mlocs);
            	}else if(et == EntityType.HUSK) {
            		LivingEntity mob = (LivingEntity) mlocs.getWorld().spawnEntity(mlocs, EntityType.HUSK);
	            	Husk ent = (Husk) mob;
	            	
	            	if(ent.isBaby()) {
	            		ent.remove();
	            		run();
	            	}else if(ent.getPassenger() != null) {
	            		ent.remove();
	            		run();
	            	}else {
						ent.setCustomName(clr("&cHusk &7[&f"+mlvl+"&7]"));
						ent.setCustomNameVisible(true);
						ent.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
		        		.setBaseValue(0.35);
						
						mlvl.put(mob.getUniqueId(), mlvls);
	            		mloc.put(mob.getUniqueId(), mlocs);
	            	}
            	}
            }
        }, 200L);
		
	}
    


    public void xpcheck(Player player, PlayerLevelManager playerLevelManager) {
    	int lvl = playerLevelManager.getLevel();
    	int xp = playerLevelManager.getXp();
        int xpneeded = this.getConfig().getInt("Levels.xp") * (lvl * lvl);

        if (xp >= xpneeded) {
            player.sendMessage(clr("&6Leveled UP!"));
            playerLevelManager.setLevel(lvl + 1);
            playerLevelManager.setXp(xp - xpneeded);
            new Scoreboards(player, lvl + 1, xp - xpneeded, plugin);
            players.set("Players." + player.getUniqueId() + ".level", lvl);
            players.set("Players." + player.getUniqueId() + ".xp", xp);
            if(xp >= xpneeded) {
            	xpcheck(player, playerLevelManager);
            }
        }else {
        	new Scoreboards(player, lvl, xp, plugin);
        	players.set("Players." + player.getUniqueId() + ".level", lvl);
            players.set("Players." + player.getUniqueId() + ".xp", xp);
        }
        
        try {
			players.save(playersFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public String clr(String msg) {
    	return ChatColor.translateAlternateColorCodes('&',msg);
    }
    
    public String randString(int n){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(n);
  
        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
  
        return sb.toString();
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
