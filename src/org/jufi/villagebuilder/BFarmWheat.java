package org.jufi.villagebuilder;

public class BFarmWheat extends Building {
	public BFarmWheat(int x, int z, int br) {
		super(x, z, br);
	}
	public BFarmWheat(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[6] += 0.021f * VB.vb.workersq;
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 9;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
