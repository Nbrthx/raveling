package eu.org.niberthix;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
// import org.bukkit.entity.Player;

public class CommandCm implements TabCompleter{
	
//	private final Raveling plugin;
//	
//	public CommandCm(Raveling plugin) {
//        this.plugin = plugin;
//    }
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		List<String> nul = new ArrayList<>();
		nul.add("");
		if (cmd.getName().equalsIgnoreCase("rvl")) {
			List<String> ac0 = new ArrayList<>();
			ac0.add("reload");
			ac0.add("item");
			ac0.add("mission");
			ac0.add("kitmission");
			ac0.add("mob");
			ac0.add("killspawnmob");
			ac0.add("listspawnmob");
			if(args.length == 1) {
				return ac0;
			}else if(args[0].equalsIgnoreCase("mob")){
				List<String> ac1 = new ArrayList<>();
				ac1.add("zombie");
				ac1.add("skeleton");
				ac1.add("husk");
				ac1.add("drowned");
				ac1.add("wither_skeleton");
				ac1.add("boss_zombie");
				ac1.add("zombie_pigman");
				ac1.add("boss_skeleton");
				ac1.add("boss_husk");
				List<String> numb = new ArrayList<>();
				numb.add("1");
				if(args.length == 2) {
					return ac1;
				}else if(args.length == 3) {
					return numb;
				}else {
					return nul;
				}
			}else if(args[0].equalsIgnoreCase("killspawnmob")){
				List<String> ac1 = new ArrayList<>();
				ac1.add("all");
				if(args.length == 2) {
					return ac1;
				}
			}else if(args[0].equalsIgnoreCase("item")){
				List<String> ac1 = new ArrayList<>();
				for(String ac1s : Raveling.config.getConfigurationSection("Items").getKeys(false)) {
					ac1.add(ac1s);
				}
				if(args.length == 2) {
					return ac1;
				}else {
					return nul;
				}
			}else if(args[0].equalsIgnoreCase("mission")){
				List<String> ac1 = new ArrayList<>();
				for(String ac1s : Raveling.config.getConfigurationSection("Missions").getKeys(false)) {
					ac1.add(ac1s);
				}
				if(args.length == 2) {
					return ac1;
				}else {
					return nul;
				}
			}else if(args[0].equalsIgnoreCase("kitmission")){
				List<String> ac1 = new ArrayList<>();
				for(String ac1s : Raveling.config.getConfigurationSection("KitMissions").getKeys(false)) {
					ac1.add(ac1s);
				}
				if(args.length == 2) {
					return ac1;
				}else {
					return nul;
				}
			}else {
				return nul;
			}
		}
		return nul;
	}
}
