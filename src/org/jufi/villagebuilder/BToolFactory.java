package org.jufi.villagebuilder;

public class BToolFactory extends Building {
	
	public BToolFactory(int x, int z, int br) {
		super(x, z, br);
	}
	public BToolFactory(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[0] >= 0.007f * VB.vb.workersq && VB.vb.goods[12] >= 0.007f * VB.vb.workersq) {
			VB.vb.goods[0] -= 0.007f * VB.vb.workersq;
			VB.vb.goods[12] -= 0.007f * VB.vb.workersq;
			VB.vb.goods[13] += 0.014f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 20;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
