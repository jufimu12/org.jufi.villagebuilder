package org.jufi.villagebuilder;

public class BAppleFarm extends Building {

	public BAppleFarm(int x, int z, int br) {
		super(x, z, br);
	}

	@Override
	protected boolean tick() {
		VB.vb.goods[5] += 0.001f * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 6;
	}
}
