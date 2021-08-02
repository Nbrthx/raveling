package eu.org.niberthix;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Raveling extends JavaPlugin implements Listener{
	
	public static Economy econ = null;
	public static boolean econb = false;
	PluginManager pm;
	
	public static File playersFile;
	public static FileConfiguration players;
	public static File savesFile;
	public static FileConfiguration saves;
	public static File configFile;
	public static FileConfiguration config;

    public static HashMap<UUID, PlayerLevel> lvlManager;
    public static HashMap<UUID, MobData> mdata;
    public static HashMap<UUID, MobData> savemob;
    public static HashMap<UUID, Missions> mission;
	public static HashMap<Integer, ItemData> itemdata;
    
	public static boolean naturalspawn;
	
    public static void fileConf(Plugin data) {
		if(!data.getDataFolder().exists()) {
			data.getDataFolder().mkdirs();
		}
		
		playersFile = new File(data.getDataFolder(), "players.yml");
		if(!playersFile.exists()) {
			data.saveResource("players.yml", false);
		}
		players = YamlConfiguration.loadConfiguration(playersFile);
		
		savesFile = new File(data.getDataFolder(), "saves.yml");
		if(!savesFile.exists()) {
			data.saveResource("saves.yml", false);
		}
		saves = YamlConfiguration.loadConfiguration(savesFile);
		
		configFile = new File(data.getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			data.saveResource("config.yml", false);
		}
		configFile = new File(data.getDataFolder(), "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
	}
    
    public void registerEvents() {
        this.pm = getServer().getPluginManager();  
        pm.registerEvents(new Guii(this), this);  
        pm.registerEvents(this, this);
    }
	
	
	@Override
    public void onEnable() {
		getServer().getConsoleSender()
		.sendMessage(clr("&aRaveling&r has been Enabled"));
        
		lvlManager = new HashMap<>();
        mdata = new HashMap<>();
        savemob = new HashMap<>();
        mission = new HashMap<>();
        itemdata = new HashMap<>();
		
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        fileConf(this);
        
        registerEvents();
        
        this.getCommand("rvl").setExecutor(new CommandEx(this));
        this.getCommand("rvl").setTabCompleter(new CommandCm());
        setupEconomy();
        if(pm.getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
        
        naturalspawn = config.getBoolean("NaturalSpawn");
        
        loadData();
        
        new Spawner(this, null).lifeTaker();
    }

    @Override
    public void onDisable() {
    	getServer().getConsoleSender()
    	.sendMessage(clr("&aRaveling&r has been Disabled"));
    	
    	saveData();
    }
    
    public void saveData() {
    	for(Player all : this.getServer().getOnlinePlayers()) {
    		PlayerLevel plm = lvlManager.get(all.getUniqueId());
    		int level = plm.getLevel();
            int xp = plm.getXp();
            
            players.set("Players." + all.getUniqueId() + ".level", level);
            players.set("Players." + all.getUniqueId() + ".xp", xp);
    	}
    	for(World world : this.getServer().getWorlds()) {
	    	for(Entity all : world.getEntities()) {
	        	if(mdata.get(all.getUniqueId()) != null) {
	        		all.remove();
	        	}
	    	}
    	}
    	for(UUID id : savemob.keySet()) {
    		MobData smob = savemob.get(id);
    		mdata.put(id, smob);
    	}
    	for(UUID id : mdata.keySet()) {
    		MobData mobd = mdata.get(id);
    		Location loc = new Location(mobd.getMloc().getWorld(),mobd.getMloc().getX(),mobd.getMloc().getY(),mobd.getMloc().getZ());
            saves.set("Mobs."+id+".level", mobd.getMlvl());
            saves.set("Mobs."+id+".location", loc);
            saves.set("Mobs."+id+".customtype", mobd.getMcty());
            saves.set("Mobs."+id+".natural", mobd.getNatural());
    	}
    	
    	
    	try {
			players.save(playersFile);
			saves.save(savesFile);
		} catch (IOException n) {
			n.printStackTrace();
		}
    	
        lvlManager.clear();
        mdata.clear();
    }
    
    public void loadData() {
    	for(Player all : this.getServer().getOnlinePlayers()) {
    		int level = players.getInt("Players." + all.getUniqueId() + ".level");
            int xp = players.getInt("Players." + all.getUniqueId() + ".xp");
            
            lvlManager.put(all.getUniqueId(), new PlayerLevel(level, xp));
    	}
    	if(saves.get("Mobs") != null) {
	    	for(String idc : saves.getConfigurationSection("Mobs").getKeys(false)) {
	    		UUID id = UUID.fromString(idc);
	    		int mlvls = saves.getInt("Mobs."+id+".level");
	            Location mlocs = saves.getLocation("Mobs." + id + ".location");
	            int et = saves.getInt("Mobs." + id + ".customtype");
	            boolean bool = saves.getBoolean("Mobs." + id + ".natural");
	            
	            mdata.put(id, new MobData(mlvls, mlocs, et, bool));
	            new Spawner(this, id);
	    	}
	    	saves.set("Mobs", null);

            try {
    			saves.save(savesFile);
    		} catch (IOException n) {
    			n.printStackTrace();
    		}
    	}
    	for(String id : config.getConfigurationSection("Items").getKeys(false)) {
    		int idi = config.getInt("Items."+id+".id");
    		List<String> effect = new ArrayList<>();
    		for(String key : config.getConfigurationSection("Items."+id+".effect").getKeys(false)) {
    			int level = config.getInt("Items."+id+".effect."+key);
    			effect.add(key+":"+level);
    		}
            
            itemdata.put(idi, new ItemData(idi, effect));
    	}
    	saves.set("Items", null);
    	try {
			saves.save(savesFile);
		} catch (IOException n) {
			n.printStackTrace();
		}
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
        	econb = false;
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
        	econb = false;
            return false;
        }
        econ = rsp.getProvider();
        if(econ != null) {
        	econb = true;
        }
        return econ != null;
    }
    
    @EventHandler
    public void join(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        event.setJoinMessage(clr(""+player.getDisplayName()+"&r &aJoined The Game!"));
        
        if (players.get("Players." + player.getUniqueId()) == null) {
            player.sendMessage(clr("&bWelcome to "+getConfig().getString("Info.title")));
            
            lvlManager.put(id, new PlayerLevel(1, 0));
            players.set("Players." + id + ".level", 1);
            players.set("Players." + id + ".xp", 0);
            
            try {
    			players.save(playersFile);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
        } else {
            int level = players.getInt("Players." + id + ".level");
            int xp = players.getInt("Players." + id + ".xp");
            lvlManager.put(id, new PlayerLevel(level, xp));
        }
        
        if(pm.getPlugin("PlaceholderAPI") != null) {
	        PlaceholderAPI.setPlaceholders(player, "level");
	        PlaceholderAPI.setPlaceholders(player, "xp");
        }
    }

	@EventHandler
    public void quit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        event.setQuitMessage(clr(""+player.getDisplayName()+"&r &7Left The Game!"));
        
        int level = players.getInt("Players."+id+".level");
        int xp = players.getInt("Players."+id+".xp");
        
        if (level != 0) {
            players.set("Players." + id + ".level", level);
            players.set("Players." + id + ".xp", xp);
            try {
    			players.save(playersFile);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
        lvlManager.remove(id);
    }
    
	@EventHandler
    public void itemAbility(Player p) {
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
				ItemStack item = p.getInventory().getItemInMainHand();
				
				if(item != null && config.get("Items") != null && item.getItemMeta().getLore() != null) {
					
					String gmh = item.getItemMeta().getLore().get(0);
					boolean itmdb = null != itemdata.get(Integer.parseInt(gmh));
					
					for(String equal : config.getConfigurationSection("Items").getKeys(false)) {
						int id = config.getInt("Items."+equal+".id");
		                if(itmdb && itemdata.get(Integer.parseInt(gmh)).gid() == id){
		                	ItemData itemd = itemdata.get(Integer.valueOf(gmh));
							for(String eff : itemd.geffect()) {
								String[] effc = eff.split(":");
								int lvl = Integer.valueOf(effc[1])-1;
								PotionEffect effect = new PotionEffect(PotionEffectType.getByName(effc[0]), 200, lvl);
								p.addPotionEffect(effect);
							}
		                }
					}
				}
            }
        }, 100);
	}
    
	@EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
    	
		Entity damager = event.getDamager();
		Entity e = event.getEntity();
		double dmg = event.getDamage();
		
		int prcnt = new Random().nextInt(9) + 1;
		
		boolean mobdb = null != mdata.get(damager.getUniqueId());
		boolean emobdb = null != mdata.get(e.getUniqueId());
		
    	if(e instanceof Player && lvlManager.get(e.getUniqueId()) != null) {
    		int lvl = lvlManager.get(e.getUniqueId()).getLevel();
    		double mxhlth = 20 * lvl;
    		
    		if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 0) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			dmg = mobd.getMlvl() * config.getDouble("Monster.0.damage");
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(damager.getType() == EntityType.ARROW) {
    			Arrow arrow = (Arrow) damager;
    			Entity shotof = (Entity) arrow.getShooter();
    			boolean smobdb = null != mdata.get(shotof.getUniqueId());
    			if(smobdb && mdata.get(shotof.getUniqueId()).getMcty() == 1) {
    				MobData mobd = mdata.get(shotof.getUniqueId());
        			dmg = mobd.getMlvl() * config.getDouble("Monster.1.damage");
        			event.setDamage(dmg * 20 / mxhlth);
    			}else if(smobdb && mdata.get(shotof.getUniqueId()).getMcty() == 7) {
        			MobData mobd = mdata.get(shotof.getUniqueId());
        			if(prcnt <= 2) {
        				Player p = (Player) e;
        				ItemStack itemCrackData = new ItemStack(Material.REDSTONE_BLOCK);
        				p.spawnParticle(Particle.ITEM_CRACK, e.getLocation(), 10, itemCrackData);
        				PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION, 20, 8);
        				PotionEffect effect2 = new PotionEffect(PotionEffectType.CONFUSION, 200, 100);
        				p.addPotionEffect(effect);
        				p.addPotionEffect(effect2);
        				dmg = mobd.getMlvl() * (config.getDouble("Monster.7.damage")/5*7);
    	    			event.setDamage(dmg * 20 / mxhlth);
        			}else {
    	    			dmg = mobd.getMlvl() * config.getDouble("Monster.7.damage");
    	    			event.setDamage(dmg * 20 / mxhlth);
        			}
        		}
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 2) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			dmg = mobd.getMlvl() * config.getDouble("Monster.2.damage");
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 3) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			dmg = mobd.getMlvl() * config.getDouble("Monster.3.damage");
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 4) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			dmg = mobd.getMlvl() * config.getDouble("Monster.4.damage");
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 5) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			if(prcnt <= 2) {
    				Player p = (Player) e;
    				ItemStack itemCrackData = new ItemStack(Material.REDSTONE_BLOCK);
    				p.spawnParticle(Particle.ITEM_CRACK, e.getLocation(), 10, itemCrackData);
    				PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);
    				p.addPotionEffect(effect);
    				dmg = mobd.getMlvl() * (config.getDouble("Monster.5.damage")/5*7);
	    			event.setDamage(dmg * 20 / mxhlth);
    			}else {
	    			dmg = mobd.getMlvl() * config.getDouble("Monster.5.damage");
	    			event.setDamage(dmg * 20 / mxhlth);
    			}
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 6) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			dmg = mobd.getMlvl() * config.getDouble("Monster.6.damage");
    			event.setDamage(dmg * 20 / mxhlth);
    		}else if(mobdb && mdata.get(damager.getUniqueId()).getMcty() == 8) {
    			MobData mobd = mdata.get(damager.getUniqueId());
    			if(prcnt <= 2) {
    				Player p = (Player) e;
    				ItemStack itemCrackData = new ItemStack(Material.REDSTONE_BLOCK);
    				p.spawnParticle(Particle.ITEM_CRACK, e.getLocation(), 10, itemCrackData);
    				PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 60, 10);
    				PotionEffect effect2 = new PotionEffect(PotionEffectType.BLINDNESS, 60, 10);
    				p.addPotionEffect(effect);
    				p.addPotionEffect(effect2);
    				dmg = mobd.getMlvl() * (config.getDouble("Monster.8.damage")/5*8);
	    			event.setDamage(dmg * 20 / mxhlth);
    			}else {
	    			dmg = mobd.getMlvl() * config.getDouble("Monster.8.damage");
	    			event.setDamage(dmg * 20 / mxhlth);
    			}
    		}else if(event.getCause() == DamageCause.FALL){
    			event.setDamage(dmg);
    		}else {
    			event.setDamage(dmg * 20 / mxhlth);
    		}
    		
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 0) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.0.health") * mobd.getMlvl();
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 1) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.1.health") * mobd.getMlvl();
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 2) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.2.health") * mobd.getMlvl();
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 3) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.3.health") * mobd.getMlvl();
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 4) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.4.health") * mobd.getMlvl();
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 5) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.5.health") * mobd.getMlvl();
			if(prcnt <= 3) {
				event.setDamage(dmg/4 * 20 / mxhlth);
				if(damager instanceof Player) {
					Player damager2 = (Player) damager;
					damager2.playSound(e.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 10);
				}
			}else {
	    		event.setDamage(dmg * 20 / mxhlth);
			}
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 6) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.6.health") * mobd.getMlvl();
    		
            event.setDamage(dmg * 20 / mxhlth);
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 7) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.7.health") * mobd.getMlvl();
			if(prcnt <= 3) {
				event.setDamage(dmg/4 * 20 / mxhlth);
				if(damager instanceof Player) {
					Player damager2 = (Player) damager;
					damager2.playSound(e.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 10);
				}
			}else {
	    		event.setDamage(dmg * 20 / mxhlth);
			}
    	}else if(emobdb && mdata.get(e.getUniqueId()).getMcty() == 8) {
    		MobData mobd = mdata.get(e.getUniqueId());
    		double mxhlth = config.getDouble("Monster.8.health") * mobd.getMlvl();
			if(prcnt <= 4) {
				event.setDamage(dmg/4 * 20 / mxhlth);
				if(damager instanceof Player) {
					Player damager2 = (Player) damager;
					damager2.playSound(e.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 10);
					damager2.damage(dmg/2 * 20 / mxhlth, e);
				}
			}else {
	    		event.setDamage(dmg * 20 / mxhlth);
			}
    	}
    }
	
	@EventHandler
	public void onTransform(EntityTransformEvent event) {
		Entity e = event.getTransformedEntity();
		if(e != null && e instanceof LivingEntity) {
			event.setCancelled(true);
			LivingEntity e2 = (LivingEntity) event.getEntity();
			e2.setHealth(1);
			e2.damage(100);
		}
	}
    
	@EventHandler
    public void onDamager2(EntityDamageByEntityEvent event) {
    	Entity damager = event.getDamager();
    	Entity e = event.getEntity();
    	if(damager != null && e instanceof Creature && damager instanceof Player) {
    		Creature cmob = (Creature) e;
    		Player p = (Player) damager;
    		if(p.getGameMode() != GameMode.CREATIVE && cmob != null) {
    			cmob.setTarget(p);
    		}
    	}
    }
	
	@EventHandler
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if(!event.getEntity().isDead()) {
            LivingEntity let = (LivingEntity) event.getEntity();
            if(let.getHealth() - event.getDamage() <= 1) {
                let.setCustomName(null);
                let.setCustomNameVisible(false);
            }
        }
    }
    
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
    	Player player = event.getEntity().getKiller();
    	Entity e = event.getEntity();
    	
		boolean mobdb = null != mdata.get(e.getUniqueId());
		
		event.getDrops().clear();
		event.setDroppedExp(0);
    	
		if(player instanceof Player) {
			
			PlayerLevel plm = lvlManager.get(player.getUniqueId());
        	int xp = plm.getXp();
	        
        	for(String lists : config.getConfigurationSection("Monster").getKeys(false)) {
        		int list = Integer.parseInt(lists);
        		if (mobdb && mdata.get(e.getUniqueId()).getMcty() == list) {
    	        	MobData mobd = mdata.get(e.getUniqueId());
    	        	
    	        	if(econb) {
    		        	EconomyResponse r = econ.depositPlayer(player, config.getInt("Monster."+list+".money"));
    		        	if(r.transactionSuccess()) {
    		        		player.sendMessage(clr("&6+"+
    		        				(config.getInt("Monster."+list+".money") * mobd.getMlvl())+" &bMoney"));
    		        	}
    	        	}
    	        	
    	        	if(mission.get(player.getUniqueId()) != null && mission.get(player.getUniqueId()).g1()+"" != null) {
    	        		int mmob = mission.get(player.getUniqueId()).g1();
    	        		int mlvlmob = mission.get(player.getUniqueId()).g2();
    	        		int mamount = mission.get(player.getUniqueId()).g3();
    	        		int mlvlndd = mission.get(player.getUniqueId()).g4();
    	        		int rxp = mission.get(player.getUniqueId()).g5();
    	        		int rmoney = mission.get(player.getUniqueId()).g6();
    	        		String text = mission.get(player.getUniqueId()).g7();
    	        		int prog = mission.get(player.getUniqueId()).gp();
    	        		if(mmob == list && mlvlmob <= mobd.getMlvl() && mlvlndd <= plm.getLevel()) {
    	        			if(prog+1 >= mamount) {
    	        				plm.setXp(xp + rxp);
    	        				if(econb) {
    	        		        	econ.depositPlayer(player, rmoney);
    	        		        	player.sendMessage(clr("&6++"+rmoney+" &bMission Money"));
    	        	        	}
    	        				player.sendMessage(clr("&d++"+rxp+" &bMission Experience"));
    	        				player.sendMessage(clr(text));
    	        				mission.remove(player.getUniqueId());
    	        			}else {
    	        				mission.get(player.getUniqueId()).sp(prog+1);
    	        			}
    	        		}
    	        	}
    	        	
    	        	plm.setXp(xp + 
    	        			(config.getInt("Monster."+list+".xp") * mobd.getMlvl()));
    	            player.sendMessage(clr("&d+"+
    	        			(config.getInt("Monster."+list+".xp") * mobd.getMlvl())+" &bExperience"));
    	            player.giveExp(xp/4);
    	            
    	            xpcheck(player, plm);
    	            if(mobd.getNatural() == false) new Spawner(this, e.getUniqueId());
    	        }
        	}
        }else if(mobdb) {
        	MobData mobd = mdata.get(e.getUniqueId());
        	if(mobd.getNatural() == false) new Spawner(this, e.getUniqueId());
        }
    }
    
	
	
	@EventHandler
	public void chunkUnload(ChunkUnloadEvent event) {
		Entity[] alle = event.getChunk().getEntities();
		for(Entity e : alle) {
			if(mdata.get(e.getUniqueId()) != null) {
	    		MobData mobd = mdata.get(e.getUniqueId());
	    		savemob.put(e.getUniqueId(), mobd);
	    		mdata.remove(e.getUniqueId());
	    		e.remove();
			}
    	}
	}
    
	@EventHandler
	public void chunkLoad(ChunkLoadEvent event) {
		Chunk alle = event.getChunk();
		for(UUID ek : savemob.keySet()) {
			if(savemob.get(ek) != null) {
	    		MobData smob = savemob.get(ek);
	    		if(smob.getMloc().getChunk() == alle) {
		    		mdata.put(ek, smob);
		    		savemob.remove(ek);
		    		new Spawner(this, ek);
	    		}
			}
    	}
	}

    public void xpcheck(Player player, PlayerLevel plm) {
    	int lvl = plm.getLevel();
    	int xp = plm.getXp();
        int xpneeded = config.getInt("Levels.xp") * (lvl * lvl);

        if (xp >= xpneeded) {
            player.sendMessage(clr("&6Leveled UP!"));
            plm.setLevel(lvl + 1);
            plm.setXp(xp - xpneeded);
            players.set("Players." + player.getUniqueId() + ".level", lvl);
            players.set("Players." + player.getUniqueId() + ".xp", xp);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 20, 20);
            if(xp >= xpneeded) {
            	xpcheck(player, plm);
            }
            if(pm.getPlugin("PlaceholderAPI") != null) {
    	        PlaceholderAPI.setPlaceholders(player, "level");
    	        PlaceholderAPI.setPlaceholders(player, "xp");
            }
        }else {
        	players.set("Players." + player.getUniqueId() + ".level", lvl);
            players.set("Players." + player.getUniqueId() + ".xp", xp);
            if(pm.getPlugin("PlaceholderAPI") != null) {
    	        PlaceholderAPI.setPlaceholders(player, "level");
    	        PlaceholderAPI.setPlaceholders(player, "xp");
            }
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
    
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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
    
}
