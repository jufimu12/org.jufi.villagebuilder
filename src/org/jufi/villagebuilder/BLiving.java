package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import org.jufi.lwjglutil.*;

public class BLiving extends Building {
	private float people;
	
	public BLiving(int x, int z, int br) {
		super(x, z, br);
	}
	public BLiving(int x, int z, int br, String extra) {
		super(x, z, br);
		people = Float.parseFloat(extra);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.workersc += 10;
		VB.vb.workersp += (int) people;
		VB.vb.goods[5] -= Math.floor(people) * 0.0001f;
		people += VB.vb.happiness / 100000f;
		if (people > 10) people = 10;
		if (people < 0) people = 0;
		return false;
	}

	@Override
	public int getID() {
		return 1;
	}
	
	@Override
	public String getExtra() {
		return String.valueOf(people);
	}
	
	@Override
	protected void render2d(boolean click) {
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, tex_mpeople);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(8, 276);
			glTexCoord2f(1, 1); glVertex2f(24, 276);
			glTexCoord2f(1, 0); glVertex2f(24, 292);
			glTexCoord2f(0, 0); glVertex2f(8, 292);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
		Draw.drawString("10 / " + (int) people, 32, 281, 1, 1, 1);
	}
}
