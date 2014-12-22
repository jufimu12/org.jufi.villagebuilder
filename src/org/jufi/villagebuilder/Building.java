package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.jufi.lwjglutil.Model;
import org.lwjgl.util.Renderable;

/*
For new building:
Create class
add to:
function build, initDLs
int[] sizeX, sizeY, sizeZ, BUILD_TIME, dls
*/

public abstract class Building implements Renderable {
	public static int[] dls = new int[5];// TODO on new building
	public static final int[] sizeX = {0, 4, 4, 4};// TODO on new building
	public static final int[] sizeY = {0, 3, 3, 3};// TODO on new building
	public static final int[] sizeZ = {0, 3, 4, 5};// TODO on new building
	private static final int[] BUILD_TIME = {1, 250, 250, 500};// TODO on new building
	protected int btimeleft = BUILD_TIME[getID()];
	protected boolean bfinished = false;
	protected int x, z;
	
	public Building(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
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
	
	public static final void initDLs() throws IOException {// TODO on new building
		dls[0] = 0;
		dls[1] = Model.getDL("res/obj/BLiving.obj");
		dls[2] = Model.getDL("res/obj/BForester.obj");
		dls[3] = Model.getDL("res/obj/BQuarry.obj");
	}
	public static final Building build(int id, int x, int z) {// TODO on new building
		int sb = id;
		if (sb <= 0) return null;
		if (sb == 1) return new BLiving(x, z);
		if (sb == 2) return new BForester(x, z);
		if (sb == 3) return new BQuarry(x, z);
		if (sb == 4) return new BStonecutter(x, z);
		return null;
	}
}
