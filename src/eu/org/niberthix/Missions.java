package eu.org.niberthix;

public class Missions {
	private int mob;
	private int lvlmob;
	private int amount;
	private int lvlndd;
	private int rxp;
	private int rmoney;
	private int prog;
	private String start;
	private String finish;
	
	public Missions(String id) {
		this.mob = Raveling.config.getInt("Missions."+id+".mob");
		this.lvlmob = Raveling.config.getInt("Missions."+id+".levelmob");
		this.amount = Raveling.config.getInt("Missions."+id+".amount");
		this.lvlndd = Raveling.config.getInt("Missions."+id+".levelneeded");
		this.rxp = Raveling.config.getInt("Missions."+id+".rxp");
		this.rmoney = Raveling.config.getInt("Missions."+id+".rmoney");
		this.prog = 0;
		this.start = Raveling.config.getString("Missions."+id+".start");
		this.finish = Raveling.config.getString("Missions."+id+".finish");
	}
	
	public int g1() {
		return mob;
	}
	
	public int g2() {
		return lvlmob;
	}
	
	public int g3() {
		return amount;
	}
	
	public int g4() {
		return lvlndd;
	}
	
	public int g5() {
		return rxp;
	}
	
	public int g6() {
		return rmoney;
	}
	
	public String g7() {
		return finish;
	}
	
	public String g8() {
		return start;
	}
	
	public int gp() {
		return prog;
	}
	
	public void sp(int i) {
		this.prog = i;
	}
}
