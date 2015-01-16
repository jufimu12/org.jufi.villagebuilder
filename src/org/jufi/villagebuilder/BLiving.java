package org.jufi.villagebuilder;

public class BLiving extends Building {
	
	public BLiving(int x, int z) {
		super(x, z);
	}
	
	@Override
	public boolean tick() {
		VB.vb.workersp += 10;
		return false;
	}

	@Override
	public int getID() {
		return 1;
	}
}
