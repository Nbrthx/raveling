package eu.org.niberthix;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.entity.Player;

import com.google.common.base.Strings;

public class Placeholder extends PlaceholderExpansion{

//	private Main plugin;
//    
//    public Placeholder(Main plugin) {
//        this.plugin = plugin;
//    }
	
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Nbrthx";
    }

    @Override
    public String getIdentifier() {
        return "rvl";
    }

    @Override
    public String getPlugin() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
    
    public String prog(int current, int max) {
    	int totalBar = 30;
    	String prog = "";
    	
    	int percent = (int) current * totalBar / max;
    	
    	prog = Strings.repeat("&a&l|", percent)+Strings.repeat("&8&l|", totalBar-percent);
    	
    	return "&f[ "+prog+" &f]";
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
    	PlayerLevel plm = Raveling.lvlManager.get(p.getUniqueId());
        if (identifier.equals("level")) {
            return plm.getLevel()+"";
        }
        if (identifier.equals("xp")) {
        	return plm.getXp()+"";
        }
        if (identifier.equals("xpneeded")) {
        	int xpneeded = Raveling.config.getInt("Levels.xp")*(plm.getLevel() * plm.getLevel());
        	return xpneeded+"";
        }if (identifier.equals("health")) {
        	double health = plm.getLevel()*p.getHealth();
        	int fhealth = (int) health;
        	int fmxhealth = Integer.max(fhealth,(int) plm.getLevel()*20);
        	return fhealth+"/"+fmxhealth;
        }
        if (identifier.equals("prog")) {
        	int lvl = plm.getLevel();
        	int xp = plm.getXp();
        	int xpneeded = Raveling.config.getInt("Levels.xp") * (lvl * lvl);
        	
        	return prog(xp, xpneeded);
        }

        return null;
    }
}
