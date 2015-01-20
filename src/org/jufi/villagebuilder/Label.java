package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import org.jufi.lwjglutil.*;

public class Label {
	private int tex_icon, x, y;
	private float r, g, b;
	
	public Label(int tex_icon, int x, int y, float r, float g, float b) {
		this.tex_icon = tex_icon;
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void render(String text) {
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, tex_icon);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(x, y);
			glTexCoord2f(1, 1); glVertex2f(x + 16, y);
			glTexCoord2f(1, 0); glVertex2f(x + 16, y + 16);
			glTexCoord2f(0, 0); glVertex2f(x, y + 16);
		glEnd();
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
		Draw.drawString(text, x + 24, y, r, g, b);
	}
}
