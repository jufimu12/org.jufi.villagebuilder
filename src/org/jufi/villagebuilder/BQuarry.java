package org.jufi.villagebuilder;

public class BQuarry extends Building {
	
	public BQuarry(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.workersm += 6;
		VB.vb.goods[1] += 0.005f * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 3;
	}
}
