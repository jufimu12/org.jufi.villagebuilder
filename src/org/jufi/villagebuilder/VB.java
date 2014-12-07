package org.jufi.villagebuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.jufi.lwjglutil.*;
import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.jufi.villagebuilder.DiscMenu.DiscMenuItem;

public class VB extends Engine {
	public static final int MAP_SIZE = 256;// STATIC
	public static final int MAP_DIV = 4;
	public static final int GRASS_RES = 512;
	public static final float CAM_SPEED = 0.03f;
	
	public static void main(String[] main) {
		new VB().start();
	}
	
	
	private int dl_map;// VARS
	private float camheight = 25;
	private float rmoriginx, rmoriginy, rmmovedx, rmmovedy;
	private boolean rmdown, lmup, shiftdown;
	private boolean rendermark;
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private int sb = 1;
	private int mousex, mousez;
	private DiscMenu bmenu;// TODO complete
	
	
	@Override// FUNCTIONS
	protected void render3dRelative() {
	}
	
	@Override
	protected void render3dRelativeNoLighting() {
		
	}
	
	@Override
	protected void render3d() {
		for (Building e : buildings) {
			e.render();
		}
	}
	
	@Override
	protected void render3dNoLighting() {
		glCallList(dl_map);
		glLineWidth(2);
		glDisable(GL_DEPTH_TEST);
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
		glLineWidth(1);
		// Nothing here!
		if (rendermark && selectionavailable(Building.sizeX[sb], Building.sizeZ[sb])) {
			glColor3f(1, 1, 1);
			glDisable(GL_CULL_FACE);
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glPushMatrix();
				glTranslatef(mousex, 0, mousez);
				glCallList(Building.dls[sb]);
			glPopMatrix();
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			glEnable(GL_CULL_FACE);
		}
		// Nothing here!
		if (sb != 0 && mousex > 0 && mousex < MAP_SIZE && mousez > 0 && mousez < MAP_SIZE) {
			rendermark = true;
			if (selectionavailable(1, 1)) glColor3f(1, 1, 1);
			else glColor3f(0.5f, 0.5f, 0.5f);
			glBegin(GL_QUADS);
				glVertex3f(mousex, 0, mousez);
				glVertex3f(mousex, 0, mousez + 1);
				glVertex3f(mousex + 1, 0, mousez + 1);
				glVertex3f(mousex + 1, 0, mousez);
			glEnd();
		} else {
			rendermark = false;
		}
		glEnable(GL_DEPTH_TEST);
		// Nothing here! It could override these fragments
	}
	
	@Override
	protected void render2d() {
		if (!rendermark && sb != 0) {
			glLineWidth(3);
			glColor3f(1, 0, 0);
			glBegin(GL_LINES);
				glVertex2f(790, 440);
				glVertex2f(810, 460);
				glVertex2f(790, 460);
				glVertex2f(810, 440);
			glEnd();
			glLineWidth(1);
		}
		bmenu.render();
	}
	
	@Override
	protected void move() {
		if (rmdown) {
			cam.setRy(cam.getRy() - rmmovedx);
			cam.setRx(cam.getRx() - rmmovedy);
		}
		if (!rmdown && isKeyDown(KEY_SPACE)) {
			rmdown = true;
			rmoriginx = Mouse.getX();
			rmoriginy = Mouse.getY();
		}
		if (rmdown && !isKeyDown(KEY_SPACE)) rmdown = false;
		if (rmdown) {
			rmmovedx = (rmoriginx - Mouse.getX()) / 20f;
			rmmovedy = (Mouse.getY() - rmoriginy) / 20f;
			cam.setRy(cam.getRy() + rmmovedx);
			cam.setRx(cam.getRx() + rmmovedy);
		}
		
		if (isKeyDown(KEY_Q)) turnaroundcenter(-2);
		if (isKeyDown(KEY_E)) turnaroundcenter(2);
		if (cam.getRy() > 360) cam.setRy(cam.getRy() - 360);
		if (cam.getRy() < 0) cam.setRy(cam.getRy() + 360);
		if (isKeyDown(KEY_W)) cam.moveNoY(true, CAM_SPEED * camheight);
		if (isKeyDown(KEY_S)) cam.moveNoY(true, -CAM_SPEED * camheight);
		if (isKeyDown(KEY_A)) cam.moveNoY(false, CAM_SPEED * camheight);
		if (isKeyDown(KEY_D)) cam.moveNoY(false, -CAM_SPEED * camheight);
		float oldcamheight = camheight;
		camheight -= Mouse.getDWheel() / 24f;
		if (camheight < 10) camheight = 10;
		if (camheight > 100) camheight = 100;
		cam.moveY(true, oldcamheight - camheight);
		if (Mouse.isButtonDown(1)) sb = 0;
		if (isKeyDown(KEY_LSHIFT)) {
			if (!shiftdown) {
				bmenu.show();
				shiftdown = true;
			}
		} else {
			if (shiftdown) {
				bmenu.hide();
				shiftdown = false;
			}
		}
	}
	
	@Override
	protected void tick() {
		double k = cam.getTy() / (float) MathLookup.sin(cam.getRx());
		mousex = (int) Math.floor(k * MathLookup.sin(cam.getRy()) * MathLookup.cos(cam.getRx()) + cam.getTx());
		mousez = (int) Math.floor(k * MathLookup.cos(cam.getRy()) * MathLookup.cos(cam.getRx()) + cam.getTz());
		
		if (!shiftdown && Mouse.isButtonDown(0)) {
			if (lmup) {
				lmup = false;
				if (selectionavailable(Building.sizeX[sb], Building.sizeZ[sb])) {
					Building b = Building.build(sb, mousex, mousez);
					if (b != null) buildings.add(b);
				}
			}
		} else lmup = true;
		
		Iterator<Building> ib = buildings.iterator();
		Building b;
		while (ib.hasNext()) {
			b = ib.next();
			if (b.run()) ib.remove();
		}
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
		
		initdisplaylists();
		DiscMenu.initDL(250, 350);
		bmenu = new DiscMenu();
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("0");
			}
			@Override
			public void render() {
				glColor3f(1, 1, 1);
				glBegin(GL_QUADS);
					glVertex2f(-32, -32);
					glVertex2f(32, -32);
					glVertex2f(32, 32);
					glVertex2f(-32, 32);
				glEnd();
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("1");
			}
			@Override
			public void render() {
				glColor3f(1, 0, 0);
				glBegin(GL_QUADS);
					glVertex2f(-10, -10);
					glVertex2f(10, -10);
					glVertex2f(10, 10);
					glVertex2f(-10, 10);
				glEnd();
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("2");
			}
			@Override
			public void render() {
				glColor3f(0, 1, 0);
				glBegin(GL_QUADS);
					glVertex2f(-10, -10);
					glVertex2f(10, -10);
					glVertex2f(10, 10);
					glVertex2f(-10, 10);
				glEnd();
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("3");
			}
			@Override
			public void render() {
				glColor3f(0, 0, 1);
				glBegin(GL_QUADS);
					glVertex2f(-10, -10);
					glVertex2f(10, -10);
					glVertex2f(10, 10);
					glVertex2f(-10, 10);
				glEnd();
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("4");
			}
			@Override
			public void render() {
				glColor3f(1, 0, 1);
				glBegin(GL_QUADS);
					glVertex2f(-10, -10);
					glVertex2f(10, -10);
					glVertex2f(10, 10);
					glVertex2f(-10, 10);
				glEnd();
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				System.out.println("5");
			}
			@Override
			public void render() {
				glColor3f(1, 1, 0);
				glBegin(GL_QUADS);
					glVertex2f(-10, -10);
					glVertex2f(10, -10);
					glVertex2f(10, 10);
					glVertex2f(-10, 10);
				glEnd();
			}
		});
		
		glClearColor(0.53f, 0.81f, 0.92f, 1f);
		
		Mouse.getDWheel();
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
		m.setTransformation(MAP_SIZE / 2, 25, MAP_SIZE / 2, -45, 45, 0);
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
	
	private void turnaroundcenter(float dry) {
		float k = cam.getTy() / (float) MathLookup.sin(cam.getRx());
		float dx = (float) (k * MathLookup.sin(cam.getRy()) * MathLookup.cos(cam.getRx()));
		float dz = (float) (k * MathLookup.cos(cam.getRy()) * MathLookup.cos(cam.getRx()));
		float length = (float) Math.sqrt(dx * dx + cam.getTy() * cam.getTy() + dz * dz);
		cam.moveY(true, length);
		cam.setRy(cam.getRy() + dry);
		cam.moveY(true, -length);
	}
	private void initdisplaylists() {
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
		
		try {
			Building.initDLs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean selectionavailable(int sizeX, int sizeZ) {
		if (mousex < 0 || mousez < 0 || mousex + sizeX > MAP_SIZE || mousez + sizeZ > MAP_SIZE) return false;
		for (Building b : buildings) {
			for (int x = mousex; x <= mousex + sizeX; x++) {
				for (int z = mousez; z <= mousez + sizeZ; z++) {
					if (x > b.getX() && x < b.getX() + Building.sizeX[b.getID()] && z > b.getZ() && z < b.getZ() + Building.sizeZ[b.getID()]) return false;
				}
			}
		}
		return true;
	}
}
