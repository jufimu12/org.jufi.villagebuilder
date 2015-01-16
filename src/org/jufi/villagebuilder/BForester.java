package org.jufi.villagebuilder;

public class BForester extends Building {

	public BForester(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.workersm += 4;
		VB.vb.goods[0] += 0.007f * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 2;
	}
}
