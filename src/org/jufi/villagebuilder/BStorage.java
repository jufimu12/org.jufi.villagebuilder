package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import org.jufi.lwjglutil.Draw;
import org.jufi.lwjglutil.ResourceLoader;

public class BStorage extends Building {
	public static int tex_mcrate;
	
	public BStorage(int x, int z, int br) {
		super(x, z, br);
	}
	public BStorage(int x, int z, int br, String extra) {
		super(x, z, br);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.goodlimittick += 100 * VB.vb.workersq;
		return false;
	}
	
	@Override
	public int getID() {
		return 7;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected void render2d() {
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, tex_mcrate);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(8, 276);
			glTexCoord2f(1, 1); glVertex2f(24, 276);
			glTexCoord2f(1, 0); glVertex2f(24, 292);
			glTexCoord2f(0, 0); glVertex2f(8, 292);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, tex_mpeople);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(8, 245);
			glTexCoord2f(1, 1); glVertex2f(24, 245);
			glTexCoord2f(1, 0); glVertex2f(24, 260);
			glTexCoord2f(0, 0); glVertex2f(8, 260);
		glEnd();
		
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
		Draw.drawString((int) (VB.vb.workersq * 100f), 32, 281, 1, 1, 1);
		Draw.drawString(cost[getID()][5] + " / " + (int) (cost[getID()][5] * VB.vb.workersq), 32, 249, 1, 1, 1);
	}
}
