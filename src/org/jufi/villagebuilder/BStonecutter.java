package org.jufi.villagebuilder;

public class BStonecutter extends Building {
	
	public BStonecutter(int x, int z, int br) {
		super(x, z, br);
	}
	public BStonecutter(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[1] -= 0.005f * VB.vb.workersq;
		VB.vb.goods[2] += 0.005f * VB.vb.workersq;
		return false;
	}
	
	@Override
	public int getID() {
		return 4;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected void render2d() {
		renderProductionUI();
	}
}
