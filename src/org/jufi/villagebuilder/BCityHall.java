package org.jufi.villagebuilder;

public class BCityHall extends Building {

	public BCityHall(int x, int z) {
		super(x, z);
	}

	@Override
	protected boolean tick() {
		return false;
	}

	@Override
	public int getID() {
		return 5;
	}
}
