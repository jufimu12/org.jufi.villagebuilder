package org.jufi.villagebuilder;

public class BQuarry extends Building {
	
	public BQuarry(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[1] += 0.0015f;
		return false;
	}

	@Override
	public int getID() {
		return 3;
	}
}
