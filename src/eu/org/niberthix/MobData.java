package eu.org.niberthix;

import org.bukkit.Location;

public class MobData {
	private int mlvl;
	private Location mloc;
	private int mcty;
	private boolean natural;
	
	public MobData(int m1, Location m2, int m3, boolean m4) {
		this.mlvl = m1;
		this.mloc = m2;
		this.mcty = m3;
		this.natural = m4;
	}
	
	public int getMlvl() {
		return mlvl;
	}
	
	public Location getMloc() {
		return mloc;
	}
	
	public int getMcty() {
		return mcty;
	}
	
	public boolean getNatural() {
		return natural;
	}
}
