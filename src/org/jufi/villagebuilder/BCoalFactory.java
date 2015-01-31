package org.jufi.villagebuilder;

public class BCoalFactory extends Building {
	
	public BCoalFactory(int x, int z, int br) {
		super(x, z, br);
	}
	public BCoalFactory(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[0] >= 0.007f * VB.vb.workersq) {
			VB.vb.goods[0] -= 0.007f * VB.vb.workersq;
			VB.vb.goods[11] += 0.0035f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 17;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
