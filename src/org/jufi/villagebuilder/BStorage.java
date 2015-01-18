package org.jufi.villagebuilder;

public class BStorage extends Building {

	public BStorage(int x, int z) {
		super(x, z);
	}

	@Override
	protected boolean tick() {
		VB.vb.goodlimittick += 100;
		return false;
	}

	@Override
	public int getID() {
		return 7;
	}
}
