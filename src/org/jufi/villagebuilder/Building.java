package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.ArrayList;

import org.jufi.lwjglutil.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/*
For new building:
Create class
menus
add to:
function get x2, initStatic x2
int[] sizeX, sizeY, sizeZ, BUILD_TIME, dls, cost
*/

public abstract class Building {
	public static int[] dls = new int[9];// TODO on new building
	public static final int[] sizeX =   {0, 4, 4, 4, 4, 6, 9,    4,     5};// TODO on new building
	public static final float[] sizeY = {0, 3, 3, 3, 6, 6, 4, 4.5f, 4.52f};// TODO on new building
	public static final int[] sizeZ =   {0, 3, 4, 5, 6, 6, 8,    5,     5};// TODO on new building
	public static final int[][] cost = new int[9][];// TODO on new building
	private static final int[] BUILD_TIME = {1, 1000, 1000, 2000, 4000, 240, 4000, 1000, 4000};// TODO on new building
	public static boolean takestimetobuild = false;// false to instantly build
	public static int tex_mgear, tex_mpeople, tex_mconstruction;
	private static Label l_productivity, l_workers;
	protected int btimeleft;
	private boolean inactive2d = true, lmup;
	protected boolean bfinished;
	protected int x, z, br;
	
	public Building(int x, int z, int br) {
		this.br = br;
		this.x = x;
		this.z = z;
		if (takestimetobuild) btimeleft = BUILD_TIME[getID()];
	}
	
	public final boolean run() {
		if (bfinished) {
			VB.vb.workersm += cost[getID()][5];
			return tick();
		}
		btimeleft--;
		if (btimeleft <= 0) bfinished = true;
		return false;
	}
	
	public final void onMouseClick() {
		inactive2d = false;
		lmup = false;
	}
	
	public final void run2d() {
		if (inactive2d) return;
		if (Mouse.isButtonDown(1) || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			inactive2d = true;
			return;
		}
		if (Mouse.isButtonDown(0)) {
			if (lmup && (Mouse.getX() < 1000 || Mouse.getY() > 300)) {
				inactive2d = true;
			}
		}
		
		glColor3f(VB.BGC, VB.BGC, VB.BGC);
		glBegin(GL_QUADS);
			glVertex2f(0, 0);
			glVertex2f(600, 0);
			glVertex2f(600, 300);
			glVertex2f(0, 300);
		glEnd();
		if (bfinished) render2d(lmup && Mouse.isButtonDown(0));
		else {
			glColor3f(1, 1, 1);
			glBindTexture(GL_TEXTURE_2D, tex_mconstruction);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1); glVertex2f(236, 86);
				glTexCoord2f(1, 1); glVertex2f(364, 86);
				glTexCoord2f(1, 0); glVertex2f(364, 214);
				glTexCoord2f(0, 0); glVertex2f(236, 214);
			glEnd();
		}
		lmup = !Mouse.isButtonDown(0);
	}
	
	
	
	public final void render() {
		glPushMatrix();
			if (bfinished) glTranslatef(x, 0, z);
			else glTranslatef(x, (float) btimeleft / (float) BUILD_TIME[getID()] * -(float) sizeY[getID()], z);
			useBR();
			glCallList(dls[getID()]);
		glPopMatrix();
	}
	public final void renderDynContour(int sb, int br) {
		glColor3f(0, 0.5f, 0);
		switch (br) {
		case 0:
			for (int x = VB.vb.mousex; x < VB.vb.mousex + sizeX[sb]; x++) {
				for (int z = VB.vb.mousez; z < VB.vb.mousez + sizeZ[sb]; z++) {
					if (occupies(x, z)) glColor3f(0.5f, 0, 0);
				}
			}
			break;
		case 1:
			for (int x = VB.vb.mousex; x < VB.vb.mousex + sizeZ[sb]; x++) {
				for (int z = VB.vb.mousez - sizeX[sb] + 1; z <= VB.vb.mousez; z++) {
					if (occupies(x, z)) glColor3f(0.5f, 0, 0);
				}
			}
			break;
		case 2:
			for (int x = VB.vb.mousex - sizeX[sb] + 1; x <= VB.vb.mousex; x++) {
				for (int z = VB.vb.mousez - sizeZ[sb] + 1; z <= VB.vb.mousez; z++) {
					if (occupies(x, z)) glColor3f(0.5f, 0, 0);
				}
			}
			break;
		case 3:
			for (int x = VB.vb.mousex - sizeZ[sb] + 1; x <= VB.vb.mousex; x++) {
				for (int z = VB.vb.mousez; z < VB.vb.mousez + sizeX[sb]; z++) {
					if (occupies(x, z)) glColor3f(0.5f, 0, 0);
				}
			}
			break;
		}
		
		glPushMatrix();
			glTranslatef(x, 0, z);
			useBR();
			renderContour();
		glPopMatrix();
	}
	public final void renderStatContour() {
		glPushMatrix();
			glTranslatef(x, 0, z);
			useBR();
			renderContour();
		glPopMatrix();
	}
	
	private void useBR() {
		switch (this.br) {
		case 0:
			break;
		case 1:
			glTranslatef(0, 0, 1);
			glRotatef(90, 0, 1, 0);
			break;
		case 2:
			glTranslatef(1, 0, 1);
			glRotatef(180, 0, 1, 0);
			break;
		case 3:
			glTranslatef(1, 0, 0);
			glRotatef(270, 0, 1, 0);
			break;
		default:
			System.err.println("Invalid br for building " + this);
		}
	}
	private void renderContour() {
		glBegin(GL_LINE_LOOP);
			glVertex3f(0, 0, 0);
			glVertex3f(0, 0, sizeZ[getID()]);
			glVertex3f(sizeX[getID()], 0, sizeZ[getID()]);
			glVertex3f(sizeX[getID()], 0, 0);
		glEnd();
		glBegin(GL_POINTS);
			glVertex3f(0, 0, 0);
			glVertex3f(0, 0, sizeZ[getID()]);
			glVertex3f(sizeX[getID()], 0, sizeZ[getID()]);
			glVertex3f(sizeX[getID()], 0, 0);
		glEnd();
	}
	
	protected void renderProductionUI() {
		l_productivity.render(String.valueOf((int) (VB.vb.workersq * 100f)));
		l_workers.render(cost[getID()][5] + " / " + (int) (cost[getID()][5] * VB.vb.workersq));
	}
	
	public final int getX() {
		return x;
	}
	public final int getZ() {
		return z;
	}
	public final int getBR() {
		return br;
	}
	
	protected abstract boolean tick();
	protected abstract void render2d(boolean click);
	public abstract int getID();
	public abstract String getExtra();
	
	public static void initStatic() throws IOException {// TODO on new building x2
		dls[0] = 0;
		dls[1] = Model.getDL("res/obj/BLiving.obj");
		dls[2] = Model.getDL("res/obj/BForester.obj");
		dls[3] = Model.getDL("res/obj/BQuarry.obj");
		dls[4] = Model.getDL("res/obj/BStonecutter.obj");
		dls[5] = Model.getDL("res/obj/BCityHall.obj");
		dls[6] = Model.getDL("res/obj/BAppleFarm.obj");
		dls[7] = Model.getDL("res/obj/BStorage.obj");
		dls[8] = Model.getDL("res/obj/BSchool.obj");
		
		// {wood, stone, brick, steel, glass}
		cost[0] = new int[5];
		cost[1] = new int[] {30, 10, 0, 0, 0, 0};
		cost[2] = new int[] {50, 10, 0, 0, 0, 4};
		cost[3] = new int[] {80, 0, 0, 0, 0, 6};
		cost[4] = new int[] {20, 50, 0, 0, 0, 6};
		cost[5] = new int[] {0, 0, 0, 0, 0, 0};
		cost[6] = new int[] {50, 10, 0, 0, 0, 4};
		cost[7] = new int[] {20, 10, 0, 0, 0, 1};
		cost[8] = new int[] {50, 100, 0, 0, 0, 0};
		
		l_productivity = new Label(tex_mgear, 8, 276, 1, 1, 1);
		l_workers = new Label(tex_mpeople, 8, 245, 1, 1, 1);
	}
	public static Building get(int id, int x, int z, int br) {// TODO on new building
		switch (id) {
		case 1: return new BLiving(x, z, br);
		case 2: return new BForester(x, z, br);
		case 3: return new BQuarry(x, z, br);
		case 4: return new BStonecutter(x, z, br);
		case 5: return new BCityHall(x, z, br);
		case 6: return new BAppleFarm(x, z, br);
		case 7: return new BStorage(x, z, br);
		case 8: return new BSchool(x, z, br);
		default: return null;
		}
	}
	public static Building get(int id, int x, int z, int br, String extra) {// TODO on new building
		switch (id) {
		case 1: return new BLiving(x, z, br, extra);
		case 2: return new BForester(x, z, br, extra);
		case 3: return new BQuarry(x, z, br, extra);
		case 4: return new BStonecutter(x, z, br, extra);
		case 5: return new BCityHall(x, z, br, extra);
		case 6: return new BAppleFarm(x, z, br, extra);
		case 7: return new BStorage(x, z, br, extra);
		case 8: return new BSchool(x, z, br, extra);
		default: return null;
		}
	}
	public static boolean canAfford(int id) {
		return VB.vb.goods[0] >= cost[id][0] && VB.vb.goods[1] >= cost[id][1] && VB.vb.goods[2] >= cost[id][2] && VB.vb.goods[3] >= cost[id][3] && VB.vb.goods[4] >= cost[id][4];
	}
	public boolean occupies(int x, int z) {
		switch (br) {
		case 0:
			return x >= this.x && x < this.x + sizeX[getID()] && z >= this.z && z < this.z + sizeZ[getID()];
		case 1:
			return x >= this.x && x < this.x + sizeZ[getID()] && z > this.z - sizeX[getID()] && z <= this.z;
		case 2:
			return x > this.x - sizeX[getID()] && x <= this.x && z > this.z - sizeZ[getID()] && z <= this.z;
		case 3:
			return x > this.x - sizeZ[getID()] && x <= this.x &&  z >= this.z && z < this.z + sizeX[getID()];
		}
		return false;
	}
	public static boolean occupied(int x, int z, ArrayList<Building> buildings) {
		for (Building b : buildings) {
			if (b.occupies(x, z)) return true;
		}
		return false;
	}
}
