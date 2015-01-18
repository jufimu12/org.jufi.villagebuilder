package org.jufi.villagebuilder;

public class BLiving extends Building {
	private float people;
	
	public BLiving(int x, int z, int br) {
		super(x, z, br);
	}
	
	@Override
	public boolean tick() {
		VB.vb.workersc += 10;
		VB.vb.workersp += (int) people;
		VB.vb.goods[5] -= Math.floor(people) * 0.0001f;
		people += VB.vb.happiness / 100000f;
		if (people > 10) people = 10;
		if (people < 0) people = 0;
		return false;
	}

	@Override
	public int getID() {
		return 1;
	}
}
