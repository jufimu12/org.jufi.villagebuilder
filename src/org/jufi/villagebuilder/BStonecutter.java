package org.jufi.villagebuilder;

public class BStonecutter extends Building {
	
	public BStonecutter(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.workersm += 6;
		VB.vb.goods[1] -= 0.005f * VB.vb.workersq;
		VB.vb.goods[2] += 0.005f * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 4;
	}
}
