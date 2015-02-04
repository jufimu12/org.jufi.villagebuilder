package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import org.jufi.lwjglutil.Draw;
import org.jufi.lwjglutil.ResourceLoader;
import org.lwjgl.input.Mouse;

public class BSchool extends Building {
	private static boolean[] unlocked = new boolean[3];
	public static int[] tex_locked = new int[3];
	private static int[][] cost = new int[3][];
	
	static {
		cost[0] = new int[] {25, 100, 0, 0, 0};
		cost[1] = new int[] {100, 50, 0, 0, 0};
		cost[2] = new int[] {200, 50, 0, 0, 0};
	}
	
	public BSchool(int x, int z, int br) {
		super(x, z, br);
	}
	public BSchool(int x, int z, int br, String extra) {
		super(x, z, br);
		char[] in = extra.toCharArray();
		for (int i = 0; i < in.length; i++) {
			if (in[i] == '1') {
				unlocked[i] = true;VB.vb.tech[0][i].run();
			}
		}
	}
	
	@Override
	protected boolean tick() {
		return false;
	}
	
	@Override
	protected void render2d(boolean click) {
		glColor3f(1, 1, 1);
		for (int i = 0; i < unlocked.length; i++) {
			if (!unlocked[i]) {
				glBindTexture(GL_TEXTURE_2D, tex_locked[i]);
				glBegin(GL_QUADS);
					glTexCoord2f(0, 1); glVertex2f(i * 40 + 8, 260);
					glTexCoord2f(1, 1); glVertex2f(i * 40 + 40, 260);
					glTexCoord2f(1, 0); glVertex2f(i * 40 + 40, 292);
					glTexCoord2f(0, 0); glVertex2f(i * 40 + 8, 292);
				glEnd();
				glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
				
				if (Mouse.getX() > i * 40 + 1008 && Mouse.getX() < i * 40 + 1040 && Mouse.getY() > 260 && Mouse.getY() < 292) {
					for (int j = 0; j < 5; j++) {
						if (cost[i][j] <= VB.vb.goods[j]) Draw.drawString(cost[i][j], j * 64 - 863, 835, 0, 1, 0);
						else Draw.drawString(cost[i][j], j * 64 - 863, 835, 1, 0, 0);
					}
					glColor3f(1, 1, 1);
					
					if (click && cost[i][0] <= VB.vb.goods[0] && cost[i][1] <= VB.vb.goods[1] && cost[i][2] <= VB.vb.goods[2] && cost[i][3] <= VB.vb.goods[3] && cost[i][4] <= VB.vb.goods[4]) {
						for (int j = 0; j < 5; j++) {
							VB.vb.goods[j] -= cost[i][j];
						}
						unlocked[i] = true;
						VB.vb.tech[0][i].run();
					}
				}
			}
		}
	}
	
	@Override
	public int getID() {
		return 8;
	}
	
	@Override
	public String getExtra() {
		StringBuilder sb = new StringBuilder();
		for (boolean b : unlocked) {
			if (b) sb.append('1');
			else sb.append('0');
		}
		return sb.toString();
	}
}
