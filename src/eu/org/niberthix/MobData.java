package eu.org.niberthix;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MobData {
	private int mlvl;
	private Location mloc;
	private String mcty;
	private List<Player> damager;
	private boolean natural;
	
	public MobData(int m1, Location m2, String m3, List<Player> p, boolean m4) {
		this.mlvl = m1;
		this.mloc = m2;
		this.mcty = m3;
		this.damager = p;
		this.natural = m4;
	}
	
	public int getMlvl() {
		return mlvl;
	}
	
	public Location getMloc() {
		return mloc;
	}
	
	public List<Player> getDmgr() {
		return damager;
	}
	
	
	public String getMcty() {
		return mcty;
	}
	
	public boolean getNatural() {
		return natural;
	}
}
