package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.jufi.lwjglutil.Model;
import org.lwjgl.util.Renderable;

public abstract class Building implements Renderable {
	public static int[] dls = new int[2];
	public static final int[] sizeX = {0, 4};
	public static final int[] sizeY = {0, 3}; 
	public static final int[] sizeZ = {0, 3};
	private static final int[] BUILD_TIME = {1, 250};
	protected int btimeleft = BUILD_TIME[getID()];
	protected boolean bfinished = false;
	protected int x, z;
	
	public final boolean run() {
		if (bfinished) return tick();
		btimeleft--;
		if (btimeleft <= 0) bfinished = true;
		return false;
	}
	
	@Override
	public final void render() {
		glPushMatrix();
			if (bfinished) glTranslatef(x, 0, z);
			else glTranslatef(x, (float) btimeleft / (float) BUILD_TIME[getID()] * -(float) sizeY[getID()], z);
			glCallList(dls[getID()]);
		glPopMatrix();
	}
	
	public final float getX() {
		return x;
	}
	public final float getZ() {
		return z;
	}
	
	protected abstract boolean tick();
	public abstract int getID();
	
	public static final void initDLs() throws IOException {
		dls[0] = 0;
		dls[1] = glGenLists(1);
		glNewList(dls[1], GL_COMPILE);
			new Model("res/obj/BLiving.obj").render();
		glEndList();
	}
	public static final Building build(int id, int x, int z) {
		int sb = id;
		if (sb <= 0) return null;
		if (sb == 1) return new BLiving(x, z);
		return null;
	}
}
