package org.jufi.villagebuilder;

public class BForester extends Building {

	public BForester(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[0] += 0.0015f;
		return false;
	}

	@Override
	public int getID() {
		return 2;
	}
}
