package org.jufi.villagebuilder;

public class BCityHall extends Building {
	
	public BCityHall(int x, int z, int br) {
		super(x, z, br);
	}
	public BCityHall(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		return false;
	}
	
	@Override
	public int getID() {
		return 5;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected void render2d() {
		
	}
}
