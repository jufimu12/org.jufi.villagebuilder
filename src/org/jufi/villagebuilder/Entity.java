package org.jufi.villagebuilder;

public interface Entity {
	public boolean tick();
	public void render();
	public float getX();
	public float getY();
	public float getZ();
	public float sizeX();
	public float sizeY();
	public float sizeZ();
}
