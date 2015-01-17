package org.jufi.villagebuilder;

public class BAppleFarm extends Building {

	public BAppleFarm(int x, int z) {
		super(x, z);
	}

	@Override
	protected boolean tick() {
		VB.vb.workersm += 4;
		VB.vb.goods[5] += 0.007f * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 6;
	}
}
