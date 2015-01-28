package org.jufi.villagebuilder;

public class BStreet extends Building {
	public static int dl_notconnected;
	
	public BStreet(int x, int z, int br) {
		super(x, z, br);
	}
	public BStreet(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {

	}
	
	@Override
	public int getID() {
		return 15;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected int getDL() {
		if (th) return dls[getID()];
		else return dl_notconnected;
	}
}
