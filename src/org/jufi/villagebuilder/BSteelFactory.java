package org.jufi.villagebuilder;

public class BSteelFactory extends Building {
	
	public BSteelFactory(int x, int z, int br) {
		super(x, z, br);
	}
	public BSteelFactory(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		if (VB.vb.goods[11] >= 0.007f * VB.vb.workersq && VB.vb.goods[12] >= 0.007f * VB.vb.workersq) {
			VB.vb.goods[11] -= 0.007f * VB.vb.workersq;
			VB.vb.goods[12] -= 0.007f * VB.vb.workersq;
			VB.vb.goods[3] += 0.007f * VB.vb.workersq;
		}
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 18;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
