package org.jufi.villagebuilder;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Renderable;

import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

import org.jufi.lwjglutil.MathLookup;
import org.jufi.lwjglutil.ResourceLoader;

public class DiscMenu {
	private static final float CIRCLE_RES = 0.5f;
	public static final float COLOR = 1f / 16f;
	private static final float T = (float) (2.0 * Math.PI);
	private static final float Tq = (float) (Math.PI / 2.0);
	private static float rm;
	private static int dl_disc;
	public boolean active;
	private boolean lmdown;
	private ArrayList<DiscMenuItem> items = new ArrayList<DiscMenuItem>();
	
	public void render() {
		if (active) {
			glCallList(dl_disc);
			for (int i = 0; i < items.size(); i++) {
				glPushMatrix();
					float angle = 360 * i / items.size() + 180 / items.size();
					glTranslatef(Display.getWidth() / 2 + (float) MathLookup.sin(angle) * rm, Display.getHeight() / 2 + (float) MathLookup.cos(angle) * rm, 0);
					items.get(i).render();
				glPopMatrix();
			}
			if (Mouse.isButtonDown(0)) {
				lmdown = true;
			} else if (lmdown == true) {
				float angle = (float) Math.atan2(Mouse.getY() - Display.getHeight() / 2f , Mouse.getX() - Display.getWidth() / 2f) - Tq;
				if (angle < 0) angle += T;
				angle = T - angle;
				int sel = (int) Math.floor(angle / T * items.size());
				active = false;
				lmdown = false;
				try {
					items.get(sel).run();
				} catch (ArrayIndexOutOfBoundsException e) {
					
				}
			}
		}
	}
	
	public void addItem(DiscMenuItem item) {
		items.add(item);
	}
	
	public static void initDL(float ri, float ro) {
		rm = (ri + ro) / 2f;
		float dx = Display.getWidth() / 2f;
		float dy = Display.getHeight() / 2f;
		dl_disc = glGenLists(1);
		glNewList(dl_disc, GL_COMPILE);
			glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
			glColor3f(COLOR, COLOR, COLOR);
			glBegin(GL_TRIANGLES);
			glEnd();
			glBegin(GL_TRIANGLES);
				for (float a = 0; a < 360; a += 1 / CIRCLE_RES) {
					glVertex2d(dx + sin(Math.toRadians(a)) * ro, dy + cos(Math.toRadians(a)) * ro);
					glVertex2d(dx + sin(Math.toRadians(a + 0.5f / CIRCLE_RES)) * ri, dy + cos(Math.toRadians(a + 0.5f / CIRCLE_RES)) * ri);
					glVertex2d(dx + sin(Math.toRadians(a + 1 / CIRCLE_RES)) * ro, dy + cos(Math.toRadians(a + 1 / CIRCLE_RES)) * ro);
					glVertex2d(dx + sin(Math.toRadians(a + 1 / CIRCLE_RES)) * ro, dy + cos(Math.toRadians(a + 1 / CIRCLE_RES)) * ro);
					glVertex2d(dx + sin(Math.toRadians(a + 0.5f / CIRCLE_RES)) * ri, dy + cos(Math.toRadians(a + 0.5f / CIRCLE_RES)) * ri);
					glVertex2d(dx + sin(Math.toRadians(a + 1.5f / CIRCLE_RES)) * ri, dy + cos(Math.toRadians(a + 1.5f / CIRCLE_RES)) * ri);
				}
			glEnd();
		glEndList();
	}
	
	public static abstract class DiscMenuItem implements Runnable, Renderable {
		
	}
}
