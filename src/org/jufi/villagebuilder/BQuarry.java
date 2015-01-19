package org.jufi.villagebuilder;

public class BQuarry extends Building {
	
	public BQuarry(int x, int z, int br) {
		super(x, z, br);
	}
	public BQuarry(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goods[1] += 0.005f * VB.vb.workersq;
		return false;
	}
	
	@Override
	public int getID() {
		return 3;
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
