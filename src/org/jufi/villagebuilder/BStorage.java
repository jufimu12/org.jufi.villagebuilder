package org.jufi.villagebuilder;

public class BStorage extends Building {

	public BStorage(int x, int z, int br) {
		super(x, z, br);
	}

	@Override
	protected boolean tick() {
		VB.vb.goodlimittick += 100 * VB.vb.workersq;
		return false;
	}

	@Override
	public int getID() {
		return 7;
	}
}
