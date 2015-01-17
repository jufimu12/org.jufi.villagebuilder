package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.jufi.lwjglutil.Model;
import org.lwjgl.util.Renderable;

/*
For new building:
Create class
add to:
function get x2, initStatic
int[] sizeX, sizeY, sizeZ, BUILD_TIME, dls, cost
*/

public abstract class Building implements Renderable {
	public static int[] dls = new int[6];// TODO on new building
	public static final int[] sizeX = {0, 4, 4, 4, 4, 6};// TODO on new building
	public static final int[] sizeY = {0, 3, 3, 3, 6, 6};// TODO on new building
	public static final int[] sizeZ = {0, 3, 4, 5, 6, 6};// TODO on new building
	public static final int[][] cost = new int[6][];// TODO on new building
	private static final int[] BUILD_TIME = {1, 500, 500, 500, 500, 240};// TODO on new building
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
	
	public static void initStatic() throws IOException {// TODO on new building x2
		dls[0] = 0;
		dls[1] = Model.getDL("res/obj/BLiving.obj");
		dls[2] = Model.getDL("res/obj/BForester.obj");
		dls[3] = Model.getDL("res/obj/BQuarry.obj");
		dls[4] = Model.getDL("res/obj/BStonecutter.obj");
		dls[5] = Model.getDL("res/obj/BCityHall.obj");
		
		// {wood, stone, brick, steel, glass}
		cost[0] = new int[5];
		cost[1] = new int[] {30, 10, 0, 0, 0};
		cost[2] = new int[] {50, 10, 0, 0, 0};
		cost[3] = new int[] {80, 0, 0, 0, 0};
		cost[4] = new int[] {20, 50, 0, 0, 0};
		cost[5] = new int[] {0, 0, 0, 0, 0};
	}
	public static Building get(int id, int x, int z) {// TODO on new building
		switch (id) {
		case 1: return new BLiving(x, z);
		case 2: return new BForester(x, z);
		case 3: return new BQuarry(x, z);
		case 4: return new BStonecutter(x, z);
		case 5: return new BCityHall(x, z);
		default: return null;
		}
	}
	public static boolean canAfford(int id) {
		return VB.vb.goods[0] >= cost[id][0] && VB.vb.goods[1] >= cost[id][1] && VB.vb.goods[2] >= cost[id][2] && VB.vb.goods[3] >= cost[id][3] && VB.vb.goods[4] >= cost[id][4];
	}
}
