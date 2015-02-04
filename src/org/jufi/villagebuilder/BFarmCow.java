package org.jufi.villagebuilder;

public class BFarmCow extends Building {
	public BFarmCow(int x, int z, int br) {
		super(x, z, br);
	}
	public BFarmCow(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[9] += 0.007f * VB.vb.workersq;
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 13;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
