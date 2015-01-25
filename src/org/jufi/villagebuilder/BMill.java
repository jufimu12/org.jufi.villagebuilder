package org.jufi.villagebuilder;

public class BMill extends Building {
	public BMill(int x, int z, int br) {
		super(x, z, br);
	}
	public BMill(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[6] >= 0.006f * VB.vb.workersq) {
			VB.vb.goods[6] -= 0.006f * VB.vb.workersq;
			VB.vb.goods[7] += 0.006f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 10;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
