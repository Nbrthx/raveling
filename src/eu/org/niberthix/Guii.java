package eu.org.niberthix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Guii implements Listener{
	
	private Inventory mission;
	private String gets;
	private HashMap<ItemStack, String> itemid;

	public Guii(Raveling plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		itemid = new HashMap<>();
        mission = plugin.getServer().createInventory(null, 9, "Mission");
    }
	
    public void initializeItems() {
    	for(String equal : Raveling.config.getConfigurationSection("KitMissions."+gets).getKeys(false)) {
    		String mat = Raveling.config.getString("KitMissions."+gets+"."+equal+".material");
    		String name = Raveling.config.getString("KitMissions."+gets+"."+equal+".name");
    		List<String> lore = Raveling.config.getStringList("KitMissions."+gets+"."+equal+".lore");
    		
    		ItemStack itemstck = createGuiItem(Material.getMaterial(mat), name, lore);
    		
    		mission.addItem(itemstck);
    		this.itemid.put(itemstck, equal);
    	}
    }

    protected ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(clr(name));
        meta.setLore(clr2(lore));
        item.setItemMeta(meta);

        return item;
    }

    public void opMission(Player p, String key) {
        p.openInventory(mission);
        this.gets = key;
        initializeItems();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory() == mission) {
            ItemStack clickedItem = e.getCurrentItem();

            boolean empt = clickedItem != null && !clickedItem.getType().isAir();
            Player p = (Player) e.getWhoClicked();
            for(String a : Raveling.config.getConfigurationSection("KitMissions."+gets).getKeys(false)) {
            	if(empt && itemid.get(clickedItem).equals(a)) {
		            if(Raveling.mission.get(p.getUniqueId()) != null) {
		    			p.sendMessage(clr("&2[&aRVL&2]&r Rejected Mission: "+Raveling.mission.get(p.getUniqueId()).g8()));
		    			Raveling.mission.remove(p.getUniqueId());
		    		}else {
		        		Raveling.mission.put(p.getUniqueId(), new Missions(a));
		        		p.sendMessage(clr("&2[&aRVL&2]&r Accepted Mission: "+Raveling.mission.get(p.getUniqueId()).g8()));
		    		}
		            e.setCancelled(true);
		            p.closeInventory();
            	}
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        if(e.getInventory().equals(mission)) {
        	e.setCancelled(true);
        }
    }
    
    public String clr(String msg) {
    	return ChatColor.translateAlternateColorCodes('&',msg);
    }
    
    public List<String> clr2(List<String> msg) {
    	List<String> trmsg = new ArrayList<>();
    	for(String smsg : msg) {
    		trmsg.add(ChatColor.translateAlternateColorCodes('&',smsg));
    	}
    	return trmsg;
    }
}
