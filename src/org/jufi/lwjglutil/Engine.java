package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.util.glu.GLU.gluErrorString;
import static org.lwjgl.input.Keyboard.*;

import java.io.IOException;

import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public abstract class Engine extends Thread {
	
	protected Camera cam;
	protected int[] sh_main;// {3d, 3dnl/2d}, null to disable
	protected boolean exitmainloop = false;
	protected boolean printfps = true;
	private FPSCounter fps = new FPSCounter();
	private int timetogc = 1000, err;
	
	public Engine() {
		
	}
	
	public final void run() {
		initEverything();
        
		while (!Display.isCloseRequested() && !(Keyboard.isKeyDown(KEY_Q) && Keyboard.isKeyDown(KEY_LCONTROL))) {// Main loop
			if (exitmainloop) {
				cam.cleanup();
				break;
			}
			timetogc--;
			if (timetogc <= 0) {
				System.gc();
				timetogc = 1000;
			}
            err = glGetError();
            if (err != GL_NO_ERROR) System.err.println(gluErrorString(err));
			input();
			tick();
			fps.tick();
			try {
				if (Display.isCurrent()) {
					if (sh_main == null) {
						glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						
						glLoadIdentity();
							glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
							cam.init3d();
							render3dRelativeNoLighting();
							glEnable(GL_LIGHTING);
							render3dRelative();
							cam.tick();
							render3d();
							glDisable(GL_LIGHTING);
							render3dNoLighting();
							
						glLoadIdentity();
							glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
							cam.init2d();
							render2d();
							if (printfps) fps.dispFPS(cam.getResY(), 3);
							
						Display.update();
						Display.sync(60);
					} else {
						glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						
						glLoadIdentity();
							glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
							cam.init3d();
							render3dRelativeNoLighting();
							glUseProgram(sh_main[0]);
							render3dRelative();
							cam.tick();
							render3d();
							glUseProgram(sh_main[1]);
							render3dNoLighting();
							
						glLoadIdentity();
							glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
							cam.init2d();
							render2d();
							if (printfps) fps.dispFPS(cam.getResY(), 3);
							
						Display.update();
						Display.sync(60);
					}
				}
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
		onExit();
		exit(0);
	}
	
	private final void input() {
		if (Mouse.isGrabbed()) {
			cam.rotateY(-(float)Mouse.getDX() / 15);
			cam.rotateX(-(float)Mouse.getDY() / 15);
		}
		move();
	}
	protected void move() {
		if (isKeyDown(KEY_W)) cam.moveY(true, 0.1f);
		if (isKeyDown(KEY_S)) cam.moveY(true, -0.1f);
		if (isKeyDown(KEY_A)) cam.moveY(false, 0.1f);
		if (isKeyDown(KEY_D)) cam.moveY(false, -0.1f);
	}
	private final void initEverything() {
		try {
			ResourceLoader.loadNatives("/org/jufi/lwjglutil/natives/", getClass());
		} catch (IOException e) {
			e.printStackTrace();
		}
		preInit();
		
		CameraMode cammode = new CameraMode();
		initCameraMode(cammode);
		cam = new Camera(cammode);
		try {
			cam.initDisplay();
		} catch (LWJGLException e) {
			e.printStackTrace();
			exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			exit(2);
		}
		cam.init();
		
		cam.init2d();
		Draw.drawString("LOADING", (cam.getResX() - 56) / 2, (cam.getResY() - 10) / 2, 1, 1, 1);
		Display.update();
		
		ResourceLoader.initWhite();
		postInit();
		if (sh_main != null) glUseProgram(sh_main[1]);
		
		System.gc();
	}
	
	public void exit(int exitArg) {
		cam.cleanup();
		System.exit(exitArg);
	}
	
	protected abstract void render3dRelative();
	protected abstract void render3dRelativeNoLighting();
	protected abstract void render3d();
	protected abstract void render3dNoLighting();
	protected abstract void render2d();
	protected abstract void tick();
	protected abstract void preInit();
	protected abstract void postInit();
	protected abstract void initCameraMode(CameraMode m);
	protected abstract void onExit();
}
