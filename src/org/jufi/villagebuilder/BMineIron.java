package org.jufi.villagebuilder;

public class BMineIron extends Building {
	
	public BMineIron(int x, int z, int br) {
		super(x, z, br);
	}
	public BMineIron(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[12] += 0.0035f * VB.vb.workersq;
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		renderProductionUI();
	}
	
	@Override
	public int getID() {
		return 16;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
}
