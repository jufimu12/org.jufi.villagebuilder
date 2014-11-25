package org.jufi.villagebuilder;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jufi.lwjglutil.*;
import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class VB extends Engine {
	public static final int MAP_SIZE = 256;// STATIC
	public static final int MAP_DIV = 4;
	public static final int GRASS_RES = 1024;
	public static final float CAM_SPEED = 0.5f;
	public static double PERSPECTIVE_ZNEAR_Y_MAX;
	public static double PERSPECTIVE_ZNEAR_X_MAX;
	
	public static void main(String[] main) {
		new VB().start();
	}
	
	
	private int dl_map;// VARS
	private float camheight = 25;
	private Model house;// TODO DEBUG
	
	@Override// FUNCTIONS
	protected void render3dRelative() {
	}
	
	@Override
	protected void render3dRelativeNoLighting() {
		
	}
	
	@Override
	protected void render3d() {
		glPushMatrix();// TODO DEBUG
			glTranslatef(100, 0, 100);
			house.render();
		glPopMatrix();
	}
	
	@Override
	protected void render3dNoLighting() {
		glCallList(dl_map);
		glBegin(GL_LINES);
			glColor3f(1, 0, 0);
			glVertex3f(0, 0, 0);
			glVertex3f(1, 0, 0);
			glColor3f(0, 1, 0);
			glVertex3f(0, 0, 0);
			glVertex3f(0, 1, 0);
			glColor3f(0, 0, 1);
			glVertex3f(0, 0, 0);
			glVertex3f(0, 0, 1);
		glEnd();
		
		int[] mpos = get3dmousecoords();
		System.out.println("X=" + mpos[0]);
		System.out.println("Y=" + mpos[1]);
		glDisable(GL_DEPTH_TEST);
		glColor3f(1, 1, 1);
		glBegin(GL_QUADS);
			glVertex3f(mpos[0], 0, mpos[1]);
			glVertex3f(mpos[0], 0, mpos[1] + 1);
			glVertex3f(mpos[0] + 1, 0, mpos[1] + 1);
			glVertex3f(mpos[0] + 1, 0, mpos[1]);
		glEnd();
		glEnable(GL_DEPTH_TEST);
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
	protected void move() {
		if (isKeyDown(KEY_Q)) cam.setRy(cam.getRy() + 1);
		if (isKeyDown(KEY_E)) cam.setRy(cam.getRy() - 1);
		if (cam.getRy() > 360) cam.setRy(cam.getRy() - 360);
		if (cam.getRy() < 0) cam.setRy(cam.getRy() + 360);
		if (isKeyDown(KEY_W)) cam.moveNoY(true, CAM_SPEED);
		if (isKeyDown(KEY_S)) cam.moveNoY(true, -CAM_SPEED);
		if (isKeyDown(KEY_A)) cam.moveNoY(false, CAM_SPEED);
		if (isKeyDown(KEY_D)) cam.moveNoY(false, -CAM_SPEED);
		float oldcamheight = camheight;
		camheight -= Mouse.getDWheel() / 24f;
		if (camheight < 10) camheight = 10;
		if (camheight > 100) camheight = 100;
		cam.moveY(true, oldcamheight - camheight);
	}
	
	@Override
	protected void tick() {
		
	}
	
	@Override
	protected void preInit() {
		
	}
	
	@Override
	protected void postInit() {
		try {
			sh_main = new int[3];
			sh_main[0] = ResourceLoader.loadShader("res/shader/3d.vsh", "res/shader/3d.fsh")[0];
			sh_main[1] = ResourceLoader.loadShader("res/shader/2d.vsh", "res/shader/2d.fsh")[0];
			sh_main[2] = sh_main[1];
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to init shaders");
		}
		
		try {// TODO DEBUG
			house = new Model("res/obj/house.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int tex_grass = generateRandomGrassTexture();// DisplayLists
		dl_map = glGenLists(1);
		glNewList(dl_map, GL_COMPILE);
			glBindTexture(GL_TEXTURE_2D, tex_grass);
			glColor3f(1, 1, 1);
			glBegin(GL_TRIANGLES);
				for (int x = 0; x < MAP_SIZE; x += MAP_SIZE / MAP_DIV) {
					for (int y = 0; y < MAP_SIZE; y += MAP_SIZE / MAP_DIV) {
						glTexCoord2f(0, 0); glVertex3f(0 + x, 0, 0 + y);
						glTexCoord2f(1, 1); glVertex3f(MAP_SIZE / MAP_DIV + x, 0, MAP_SIZE / MAP_DIV + y);
						glTexCoord2f(1, 0); glVertex3f(MAP_SIZE / MAP_DIV + x, 0, 0 + y);
						glTexCoord2f(0, 0); glVertex3f(0 + x, 0, 0 + y);
						glTexCoord2f(0, 1); glVertex3f(0 + x, 0, MAP_SIZE / MAP_DIV + y);
						glTexCoord2f(1, 1); glVertex3f(MAP_SIZE / MAP_DIV + x, 0, MAP_SIZE / MAP_DIV + y);
					}
				}
			glEnd();
			glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
			glColor3f(0, 0, 0);
			glBegin(GL_LINES);
			for (int i = 1; i < MAP_SIZE; i++) {
				glVertex3f(i, 0, 0);
				glVertex3f(i, 0, MAP_SIZE);
				glVertex3f(0, 0, i);
				glVertex3f(MAP_SIZE, 0, i);
			}
			glEnd();
		glEndList();
		
		PERSPECTIVE_ZNEAR_Y_MAX = Math.tan(Math.toRadians(45.0 / 2.0));
		PERSPECTIVE_ZNEAR_X_MAX = PERSPECTIVE_ZNEAR_Y_MAX * Display.getWidth() / Display.getHeight();
		
		Mouse.getDWheel();
		
//		glPolygonMode(GL_FRONT, GL_LINE);
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
		m.setTransformation(MAP_SIZE / 2, 25, MAP_SIZE / 2, -60, 0, 0);
	}
	
	@Override
	protected void onExit() {
		
	}
	
	private int generateRandomGrassTexture() {
		int cr = 154, cg = 205, cb = 50;
		int id = glGenTextures();
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(GRASS_RES * GRASS_RES * 4);
		for (int i = 0; i < GRASS_RES * GRASS_RES; i++) {
			byte red = (byte) ((int) (cr * (Math.random() * 0.2 + 0.9)) & 0xFF);
			byte green = (byte) ((int) (cg * (Math.random() * 0.2 + 0.9)) & 0xFF);
			byte blue = (byte) ((int) (cb * (Math.random() * 0.2 + 0.9)) & 0xFF);
			buffer.put(red).put(green).put(blue).put((byte) -1);
		}
		buffer.flip();
		
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, GRASS_RES, GRASS_RES, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return id;
	}
	
	private int[] get3dmousecoords() {// TODO NOT WORKIG PROPERLY -> CIRCLE -> NOTHING
		int[] result = new int[2];
		
		float mrx = (float) Math.toDegrees(Math.atan((Mouse.getX() * 2.0 / Display.getWidth() - 1) * PERSPECTIVE_ZNEAR_X_MAX));
		float mry = (float) Math.toDegrees(Math.atan((Mouse.getY() * 2.0 / Display.getHeight() - 1) * PERSPECTIVE_ZNEAR_Y_MAX));
		
		Matrix4f tf = new Matrix4f();
		tf.m00 = 1;
		tf.m11 = 1;
		tf.m22 = 1;
		tf.m33 = 1;
		
		Vector4f v0 = new Vector4f(0, 0, -1, 0);
		
		tf.translate(new Vector3f(cam.getTx(), cam.getTy(), cam.getTz()));
		tf.rotate(cam.getRx(), new Vector3f(1, 0, 0));
		tf.rotate(cam.getRy(), new Vector3f(0, 1, 0));
		tf.rotate(mrx, new Vector3f(1, 0, 0));
		tf.rotate(mry, new Vector3f(0, 1, 0));
		
		float k = cam.getTy() / (cam.getTy() - v0.getY());
		
		result[0] = (int) Math.floor(k * (cam.getTx() - v0.getX()));
		result[1] = (int) Math.floor(k * (cam.getTz() - v0.getZ()));
		
		v0 = Matrix4f.transform(tf, v0, null);
		
		return result;
	}
}
