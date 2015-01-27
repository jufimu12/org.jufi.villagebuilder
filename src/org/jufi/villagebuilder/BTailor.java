package org.jufi.villagebuilder;

public class BTailor extends Building {
	public BTailor(int x, int z, int br) {
		super(x, z, br);
	}
	public BTailor(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[8] >= 0.003f * VB.vb.workersq && VB.vb.goods[9] >= 0.003f * VB.vb.workersq) {
			VB.vb.goods[8] -= 0.003f * VB.vb.workersq;
			VB.vb.goods[9] -= 0.003f * VB.vb.workersq;
			VB.vb.goods[10] += 0.003f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		
	}
	
	@Override
	public int getID() {
		return 14;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
