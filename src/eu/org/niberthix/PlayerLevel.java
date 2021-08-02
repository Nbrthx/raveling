package eu.org.niberthix;

public class PlayerLevel {
	private int level;
	private int xp;
	
	public PlayerLevel(int level, int xp) {
		this.level = level;
		this.xp = xp;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getXp() {
		return xp;
	}
	
	public void setXp(int xp) {
		this.xp = xp;
	}
}
