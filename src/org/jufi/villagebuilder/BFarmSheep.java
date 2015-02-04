package org.jufi.villagebuilder;

public class BFarmSheep extends Building {
	public BFarmSheep(int x, int z, int br) {
		super(x, z, br);
	}
	public BFarmSheep(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[8] += 0.007f * VB.vb.workersq;
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 12;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
