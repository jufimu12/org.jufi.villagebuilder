package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.glCallList;

public class BLiving implements Entity {
	private float x, z;
	
	public BLiving(float x, float z) {
		this.x = x;
		this.z = z;
	}
	
	@Override
	public boolean tick() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void render() {
		glCallList(VB.bdls[1]);
	}
	
	@Override
	public float getX() {
		return x;
	}
	
	@Override
	public float getY() {
		return 0;
	}
	
	@Override
	public float getZ() {
		return z;
	}
	
	@Override
	public float sizeX() {
		return 4;
	}
	
	@Override
	public float sizeY() {
		return 3;
	}
	
	@Override
	public float sizeZ() {
		return 3;
	}
}
