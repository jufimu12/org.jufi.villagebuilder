package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Camera {
	private final PhysMap ppmap;
	private final String title;
	private final float znear, zfar;
	private final float playerHeight;
	private final int resxdisplay, resydisplay, resxortho, resyortho;
	private final boolean fullscreen;
	private FloatBuffer lightpos;
	private float tx, ty, tz, rx, ry, rz;
	private float fov;
	private boolean physics;
	
	public Camera(PhysMap ppmap, FloatBuffer lightpos, String title,
			float znear, float zfar, int resxdisplay, int resydisplay, int resxortho, int resyortho,
			boolean fullscreen, float playerHeight, boolean physics,
			float tx, float ty, float tz, float rx, float ry, float rz, float fov) {
		this.ppmap = ppmap;
		this.lightpos = lightpos;
		this.title = title;
		this.znear = znear;
		this.zfar = zfar;
		this.resxdisplay = resxdisplay;
		this.resydisplay = resydisplay;
		this.resxortho = resxortho;
		this.resyortho = resyortho;
		this.fullscreen = fullscreen;
		this.playerHeight = playerHeight;
		this.physics = physics;
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.fov = fov;
	}
	
	public Camera(CameraMode m) {
		ppmap = m.ppmap;
		lightpos = m.lightpos;
		title = m.title;
		znear = m.znear;
		zfar = m.zfar;
		resxdisplay = m.resxdisplay;
		resydisplay = m.resydisplay;
		resxortho = m.resxortho;
		resyortho = m.resyortho;
		fullscreen = m.fullscreen;
		playerHeight = m.playerHeight;
		tx = m.tx;
		ty = m.ty;
		tz = m.tz;
		rx = m.rx;
		ry = m.ry;
		rz = m.rz;
		fov = m.fov;
		physics = m.physics;
	}
	
	public void tick() {
		glRotatef(-rx, 1, 0, 0);
		glRotatef(-ry, 0, 1, 0);
		glRotatef(-rz, 0, 0, 1);
		glTranslatef(-tx, -ty - playerHeight, -tz);
		glLight(GL_LIGHT0, GL_POSITION, lightpos);
	}
	
	public void moveY(boolean dir, float amount) {
		float otz = tz;
		float oty = ty;
		float otx = tx;
		if (dir) {
			tx -= amount * MathLookup.cos(90 - ry) * MathLookup.cos(rx);
			ty += amount * MathLookup.sin(rx);
			tz -= amount * MathLookup.sin(90 - ry) * MathLookup.cos(rx);
		} else {
			tx -= amount * MathLookup.cos(-ry);
			tz -= amount * MathLookup.sin(-ry);
		}
		if (physics && ppmap.collides(tx, ty, tz) && !ppmap.collides(otx, oty, otz)) {
			tx = otx;
			tz = otz;
		}
	}
	public void moveNoY(float dir, float amount) {
		float otz = tz;
		float otx = tx;
		tz -= amount * MathLookup.sin(-ry + 90 * dir);
		tx -= amount * MathLookup.cos(-ry + 90 * dir);
		if (physics && ppmap.collides(tx, ty, tz) && !ppmap.collides(otx, ty, otz)) {
			tx = otx;
			tz = otz;
		}
	}
	
	public void rotateY(float amount) {
		ry += amount;
		if (ry >= 360) {
			ry -= 360;
		}
		if (ry < 0) {
			ry += 360;
		}
	}
	
	public void rotateX(float amount) {
		rx -= amount;
		if (rx > 90) {
			rx = 90;
		}
		if (rx < -90) {
			rx = -90;
		}
	}
	
	public void init() {
		glMaterialf(GL_FRONT, GL_SHININESS, 64);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LIGHT0);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		System.out.println("OpenGL version: " + glGetString(GL_VERSION));
	}
	
	public void init3d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, (float)Display.getWidth() / Display.getHeight(), znear, zfar);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
	}
	public void init2d() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, resxortho, 0, resyortho, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_DEPTH_TEST);
	}
	
	public void initDisplay() throws LWJGLException, IOException {
		if (fullscreen) Display.setDisplayMode(Display.getDesktopDisplayMode());
		else Display.setDisplayMode(new DisplayMode(resxdisplay, resydisplay));
		Display.setFullscreen(fullscreen);
		Display.setTitle(title);
		Display.setVSyncEnabled(true);
		
		ByteBuffer[] icon = new ByteBuffer[2];
		icon[0] = ResourceLoader.loadTextureIntoByteBuffer(System.getProperty("user.dir") + "/res/img/icon16.png");
		icon[1] = ResourceLoader.loadTextureIntoByteBuffer(System.getProperty("user.dir") + "/res/img/icon32.png");
		Display.setIcon(icon);
		
		Display.create(new PixelFormat(8, 8, 0, 8));
	}
	
	public void cleanup() {
		if (Display.isCreated()) Display.destroy();
		if (AL.isCreated()) AL.destroy();
	}
	
	public float getTx() {
		return tx;
	}
	public void setTx(float tx) {
		this.tx = tx;
	}
	public float getTy() {
		return ty;
	}
	public boolean setTy(float ty, boolean collision) {
		if (collision && ppmap.collides(this.tx, ty, this.tz)) return true;
		this.ty = ty;
		return false;
	}
	public float getTz() {
		return tz;
	}
	public void setTz(float tz) {
		this.tz = tz;
	}
	public float getRx() {
		return rx;
	}
	public void setRx(float rx) {
		this.rx = rx;
	}
	public float getRy() {
		return ry;
	}
	public void setRy(float ry) {
		this.ry = ry;
	}
	public float getRz() {
		return rz;
	}
	public void setRz(float rz) {
		this.rz = rz;
	}
	public float getFov() {
		return fov;
	}
	public void setFov(float fov) {
		this.fov = fov;
	}
	public int getResX() {
		return resxortho;
	}
	public int getResY() {
		return resyortho;
	}
	
	public static class CameraMode {
		private PhysMap ppmap;
		private FloatBuffer lightpos;
		private String title;
		private float tx, ty, tz, rx, ry, rz;
		private float fov, znear, zfar, playerHeight;
		private int resxdisplay, resydisplay;
		private int resxortho, resyortho;
		private boolean fullscreen, physics;
		
		public void setMap(PhysMap ppmap) {
			this.ppmap = ppmap;
		}
		public void setLightpos(float x, float y, float z, float w) {
			lightpos = PBytes.toFloatBuffer(x, y, z, w);
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setPerspective(float fov, float znear, float zfar) {
			this.fov = fov;
			this.znear = znear;
			this.zfar = zfar;
		}
		public void setDisplayRes(int x, int y) {
			this.resxdisplay = x;
			this.resydisplay = y;
		}
		public void setOrthoRes(int x, int y) {
			this.resxortho = x;
			this.resyortho = y;
		}
		public void setOptions(boolean fullscreen, float playerHeight, boolean physics) {
			this.fullscreen = fullscreen;
			this.playerHeight = playerHeight;
			this.physics = physics;
		}
		public void setTransformation(float tx, float ty, float tz, float rx, float ry, float rz) {
			this.tx = tx;
			this.ty = ty;
			this.tz = tz;
			this.rx = rx;
			this.ry = ry;
			this.rz = rz;
		}
	}
}
