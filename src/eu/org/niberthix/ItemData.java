package eu.org.niberthix;

import java.util.List;

public class ItemData {
	private int id;
	private List<String> effect;
	
	public ItemData(int v1, List<String> v2) {
		this.id = v1;
		this.effect = v2;
	}
	
	public int gid() {
		return id;
	}
	
	public List<String> geffect() {
		return effect;
	}
}
