package org.jufi.villagebuilder;

import java.io.IOException;

import org.jufi.lwjglutil.*;
import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.input.Mouse;

import static org.lwjgl.opengl.GL11.*;

public class VB extends Engine {
	public static final int MAP_SIZE = 256;// STATIC
	
	public static void main(String[] main) {
		new VB().start();
	}
	
	
	private int dl_map;
	
	@Override
	protected void render3dRelative() {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void render3dRelativeNoLighting() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void render3d() {
		glCallList(dl_map);
	}
	
	@Override
	protected void render3dNoLighting() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void render2d() {
		glBegin(GL_TRIANGLES);
			glColor3f(1, 0, 0);
			glVertex2f(100, 100);
			glColor3f(0, 1, 0);
			glVertex2f(200, 100);
			glColor3f(0, 0, 1);
			glVertex2f(100, 200);
		glEnd();
		
	}
	
	@Override
	protected void tick() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void preInit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void postInit() {
		try {
			int tex_grass = ResourceLoader.loadTexture("res/img/grass_top.png");
			dl_map = glGenLists(1);
			glNewList(dl_map, GL_COMPILE);
				glBindTexture(GL_TEXTURE_2D, tex_grass);
				glBegin(GL_TRIANGLES);
					for (int x = 0; x < MAP_SIZE; x++) {
						for (int y = 0; y < MAP_SIZE; y++) {
							glTexCoord2f(0, 0); glVertex3f(x, 0, y);
							glTexCoord2f(1, 1); glVertex3f(x + 1, 0, y + 1);
							glTexCoord2f(1, 0); glVertex3f(x + 1, 0, y);
							glTexCoord2f(0, 0); glVertex3f(x, 0, y);
							glTexCoord2f(0, 1); glVertex3f(x, 0, y + 1);
							glTexCoord2f(1, 1); glVertex3f(x + 1, 0, y + 1);
						}
					}
				glEnd();
			glEndList();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load grass texture");
		}
		
		Mouse.setGrabbed(true);
	}
	
	@Override
	protected void initCameraMode(CameraMode m) {
		m.setDisplayRes(1600, 900);
		m.setLightpos(-1f, 0.5f, 0.5f, 0f);
		m.setMap(null);
		m.setOptions(false, 0, false);
		m.setOrthoRes(1600, 900);
		m.setPerspective(45, 1, 500);
		m.setTitle("Villagebuilder");
		m.setTransformation(MAP_SIZE / 2, 50, MAP_SIZE / 2, -60, 0, 0);
	}
	
	@Override
	protected void onExit() {
		// TODO Auto-generated method stub
		
	}
}
