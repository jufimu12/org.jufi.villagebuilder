package org.jufi.villagebuilder;

public class BForester extends Building {
	
	public BForester(int x, int z, int br) {
		super(x, z, br);
	}
	public BForester(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[0] += 0.007f * VB.vb.workersq;
		return false;
	}
	
	@Override
	public int getID() {
		return 2;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
}
