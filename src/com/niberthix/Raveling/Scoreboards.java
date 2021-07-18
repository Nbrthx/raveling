package com.niberthix.Raveling;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

public class Scoreboards {
	
	public String prog(double now, double max) {
		String bar = "[ &2>&8--------- &f]";
		if(9 <= now * 10 / max) {
			bar = "[ &a=========&2> &f]";
		}else if(8 <= now * 10 / max) {
			bar = "[ &a========&2>&8- &f]";
		}else if(7 <= now * 10 / max) {
			bar = "[ &a=======&2>&8-- &f]";
		}else if(6 <= now * 10 / max) {
			bar = "[ &a======&2>&8--- &f]";
		}else if(5 <= now * 10 / max) {
			bar = "[ &a=====&2>&8---- &f]";
		}else if(4 <= now * 10 / max) {
			bar = "[ &a====&2>&8----- &f]";
		}else if(3 <= now * 10 / max) {
			bar = "[ &a===&2>&8------ &f]";
		}else if(2 <= now * 10 / max) {
			bar = "[ &a==&2>&8------- &f]";
		}else if(1 <= now * 10 / max) {
			bar = "[ &a=&2>&8-------- &f]";
		}else if(0 <= now * 10 / max) {
			bar = "[ &2>&8--------- &f]";
		}
		
		return bar;
	}
	
	public Scoreboards(Player player, int level, int xp, Plugin plugin) {
    	int xpneeded = plugin.getConfig().getInt("Levels.xp") * (level * level);
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        @SuppressWarnings("deprecation")
		Objective objective = scoreboard.registerNewObjective("test", "dummy");

        objective.setDisplayName(clr(plugin.getConfig().getString("Info.title")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Score name = objective.getScore(clr("Name: &6"+player.getName()));
        name.setScore(3);
        Score lvl = objective.getScore(clr("Level: &e"+level));
        lvl.setScore(2);
        Score exp = objective.getScore(clr("XP: &e"+xp+"/"+ (xpneeded)));
        exp.setScore(1);
        Score prog = objective.getScore(clr(prog(xp,xpneeded)));
        prog.setScore(0);

        player.setScoreboard(scoreboard);
    }
	
	public String clr(String msg) {
    	return ChatColor.translateAlternateColorCodes('&',msg);
    }
}
