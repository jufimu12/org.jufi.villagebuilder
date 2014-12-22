package org.jufi.villagebuilder;

public class BStonecutter extends Building {
	
	public BStonecutter(int x, int z) {
		super(x, z);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[1] -= 0.0015f;
		VB.vb.goods[2] += 0.0015f;
		return false;
	}

	@Override
	public int getID() {
		return 4;
	}
}
