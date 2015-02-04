package org.jufi.villagebuilder;

public class BBakery extends Building {
	public BBakery(int x, int z, int br) {
		super(x, z, br);
	}
	public BBakery(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[7] >= 0.021f * VB.vb.workersq) {
			VB.vb.goods[7] -= 0.021f * VB.vb.workersq;
			VB.vb.goods[5] += 0.021f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 11;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
