package org.jufi.villagebuilder;

public class BAppleFarm extends Building {
	
	public BAppleFarm(int x, int z, int br) {
		super(x, z, br);
	}
	public BAppleFarm(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[5] += 0.007f * VB.vb.workersq;
		return false;
	}
	
	@Override
	public int getID() {
		return 6;
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
