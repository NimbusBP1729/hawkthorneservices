
package com.projecthawkthorne.content;

public final class UUID 
implements java.io.Serializable, Comparable<UUID> {

	private double val;

	@Override
	public int compareTo(UUID o) {
		if(this.val==o.val) return 0;
		return (this.val - o.val)>0?1:-1;
	}

	public static UUID randomUUID() {
		UUID randId = new UUID(Math.random());
		return randId;
	}

	private UUID(double random) {
		this.val = random;
	}
	
	public String toString(){
		return String.valueOf(val);
	}

}
