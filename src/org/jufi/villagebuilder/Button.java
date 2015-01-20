package org.jufi.villagebuilder;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;

public class Button {
	private int tex_icon, x, y, tx, ty;
	
	public Button(int tex_icon, int x, int y) {
		this.tex_icon = tex_icon;
		this.x = x;
		this.y = y;
	}
	public Button(int tex_icon, int x, int y, int tx, int ty) {
		this.tex_icon = tex_icon;
		this.x = x;
		this.y = y;
		this.tx = tx;
		this.ty = ty;
	}
	
	public void render() {
		glColor3f(1, 1, 1);
		glBindTexture(GL_TEXTURE_2D, tex_icon);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(x, y);
			glTexCoord2f(1, 1); glVertex2f(x + 32, y);
			glTexCoord2f(1, 0); glVertex2f(x + 32, y + 32);
			glTexCoord2f(0, 0); glVertex2f(x, y + 32);
		glEnd();
	}
	public boolean mouseover() {
		return Mouse.getX() > x + tx && Mouse.getX() < x + tx + 32 && Mouse.getY() > y + ty && Mouse.getY() < y + ty + 32;
	}
}
