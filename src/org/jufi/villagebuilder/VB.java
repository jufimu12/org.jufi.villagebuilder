package org.jufi.villagebuilder;// TODO all used squares marked if sb > 0, bstorage

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.jufi.lwjglutil.*;
import org.jufi.lwjglutil.Camera.CameraMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.jufi.villagebuilder.DiscMenu.DiscMenuItem;

public class VB extends Engine {
	public static final int MAP_SIZE = 256;// STATIC
	public static final int MAP_DIV = 4;
	public static final int GRASS_RES = 512;
	public static final float CAM_SPEED = 0.03f;
	
	public static VB vb;
	
	public static void main(String[] main) {
		vb = new VB();
		vb.start();
	}
	
	
	private int dl_map, dl_hud;// VARS
	private float camheight = 25;
	private boolean lmup, shiftdown;
	private boolean rendermark;
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private int sb;
	private int mousex, mousez;
	public float[] goods = new float[6];
	private int[] tex_goods = new int[6];
	private int tex_bmenu_mat, tex_bmenu_liv;
	private int tex_bmenu_liv_0, tex_bmenu_fod_0;
	private int tex_person;
	private int goodlimit = 1000;
	private DiscMenu bmenu, bmenu_mat, bmenu_liv, bmenu_fod;
	public float workersp, workersm, workersq;
	
	
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
		pickGround();// needs matrix, actually should belong to tick
		
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
		if (rendermark) {
			if (selectionavailable(Building.sizeX[sb], Building.sizeZ[sb])) {
				if (Building.canAfford(sb)) glColor3f(1, 1, 1);
				else glColor3f(0.5f, 0.5f, 0.5f);
			} else {
				if (Building.canAfford(sb)) glColor3f(1, 0, 0);
				else glColor3f(0.5f, 0, 0);
			}
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
		if (mousex >= 0 && mousex < MAP_SIZE && mousez >= 0 && mousez < MAP_SIZE) {
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
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
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
		glCallList(dl_hud);
		glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
		int movex = 137;
		for (int i = 0; i < tex_goods.length; i++) {
			Draw.drawString((int) goods[i], movex, 875, 1, 1, 1);
			if (i < 5 && sb > 0) {
				if (Building.cost[sb][i] <= goods[i]) Draw.drawString(Building.cost[sb][i], movex, 835, 0, 1, 0);
				else Draw.drawString(Building.cost[sb][i], movex, 835, 1, 0, 0);
				
			}
			movex += 64;
		}
		Draw.drawString((int) workersp + " / " + (int) workersm + " / " + Math.floor(workersq * 100f) / 100f, 457, 835, 1, 1, 1);
		
		bmenu.render();
		bmenu_mat.render();
		bmenu_liv.render();
		bmenu_fod.render();
	}
	
	@Override
	protected void move() {
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
				bmenu.active = true;
				shiftdown = true;
			}
		} else {
			if (shiftdown) {
				bmenu.active = false;
				bmenu_mat.active = false;
				bmenu_liv.active = false;
				bmenu_fod.active = false;
				shiftdown = false;
			}
		}
	}
	
	@Override
	protected void tick() {
		if (workersm == 0) {
			workersq = 1;
		} else {
			workersq = workersp / workersm;
			if (workersq > 1) workersq = 1;
		}
		workersp = 0;
		workersm = 0;
		
		if (!shiftdown && Mouse.isButtonDown(0)) {
			if (lmup) {
				lmup = false;
				if (selectionavailable(Building.sizeX[sb], Building.sizeZ[sb]) && Building.canAfford(sb)) {
					Building b = Building.get(sb, mousex, mousez);
					if (b != null) {
						buildings.add(b);
						goods[0] -= Building.cost[sb][0];
						goods[1] -= Building.cost[sb][1];
						goods[2] -= Building.cost[sb][2];
						goods[3] -= Building.cost[sb][3];
						goods[4] -= Building.cost[sb][4];
					}
				}
			}
		} else lmup = true;
		
		Iterator<Building> ib = buildings.iterator();
		Building b;
		while (ib.hasNext()) {
			b = ib.next();
			if (b.run()) ib.remove();
		}
		for (int i = 0; i < goods.length; i++) {
			if (goods[i] > goodlimit) goods[i] = goodlimit;
		}
	}
	
	@Override
	protected void preInit() {
		
	}
	@Override
	protected void postInit() {
		try {// shader
			sh_main = new int[3];
			sh_main[0] = ResourceLoader.loadShader("res/shader/3d.vsh", "res/shader/3d.fsh")[0];
			sh_main[1] = ResourceLoader.loadShader("res/shader/2d.vsh", "res/shader/2d.fsh")[0];
			sh_main[2] = sh_main[1];
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to init shaders");
		}
		
		initGoods();
		initTex();
		initDisplayLists();
		initDiscMenus();
		
		glClearColor(0.53f, 0.81f, 0.92f, 1f);
		
		Mouse.getDWheel();
		
		buildings.add(Building.get(5, MAP_SIZE / 2, MAP_SIZE / 2));
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
		m.setTransformation(MAP_SIZE / 2 + 25, 25, MAP_SIZE / 2 + 25, -45, 45, 0);
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
	private void pickGround() {
		Vector4f ray = new Vector4f((2f * Mouse.getX()) / Display.getWidth() - 1f, (2f * Mouse.getY()) / Display.getHeight() - 1f, -1, 1);// clip space
		
		FloatBuffer bp = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_PROJECTION_MATRIX, bp);
		Matrix4f p = new Matrix4f();
		p.load(bp);
		p.invert();
		Matrix4f.transform(p, ray, ray);// eye space
		ray.z = -1f;
		ray.w = 0f;// ray
		
		FloatBuffer bmv = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_MODELVIEW_MATRIX, bmv);
		Matrix4f mv = new Matrix4f();
		mv.load(bmv);
		mv.invert();
		Matrix4f.transform(mv, ray, ray);// world space
		
		mousex = (int) Math.floor(cam.getTy() / -ray.y * ray.x + cam.getTx());
		mousez = (int) Math.floor(cam.getTy() / -ray.y * ray.z + cam.getTz());// y = 0
	}
	private void initGoods() {
		goods[0] = 250;
		goods[1] = 150;
		goods[2] = 0;
		goods[3] = 0;
		goods[4] = 0;
		goods[5] = 250;
	}
	private void initTex() {
		try {
			tex_goods[0] = ResourceLoader.loadTexture("res/img/glogs.png");
			tex_goods[1] = ResourceLoader.loadTexture("res/img/gstone.png");
			tex_goods[2] = ResourceLoader.loadTexture("res/img/gbrick.png");
			tex_goods[3] = ResourceLoader.loadTexture("res/img/gsteel.png");
			tex_goods[4] = ResourceLoader.loadTexture("res/img/gglass.png");
			tex_goods[5] = ResourceLoader.loadTexture("res/img/gfood.png");
			
			tex_bmenu_mat = ResourceLoader.loadTexture("res/img/bmmat.png");
			tex_bmenu_liv = ResourceLoader.loadTexture("res/img/bmliv.png");
			tex_bmenu_liv_0 = ResourceLoader.loadTexture("res/img/sperson.png");
			tex_bmenu_fod_0 = ResourceLoader.loadTexture("res/img/bmfod_0.png");
			
			tex_person = tex_bmenu_liv_0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initDisplayLists() {
		int tex_grass = generateRandomGrassTexture();// grass
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
		
		dl_hud = glGenLists(1);// hud
		glNewList(dl_hud, GL_COMPILE);
			glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
			glColor3f(DiscMenu.COLOR, DiscMenu.COLOR, DiscMenu.COLOR);
			glBegin(GL_QUADS);
				glVertex2f(105, 863);
				glVertex2f(1595, 863);
				glVertex2f(1595, 895);
				glVertex2f(105, 895);
				glVertex2f(105, 823);
				glVertex2f(1595, 823);
				glVertex2f(1595, 855);
				glVertex2f(105, 855);
			glEnd();
			glColor3f(1, 1, 1);
			float movex = 105;
			for (int i = 0; i < tex_goods.length; i++) {
				glBindTexture(GL_TEXTURE_2D, tex_goods[i]);
				glBegin(GL_QUADS);
					glTexCoord2f(0, 1); glVertex2f(movex + 8, 871);
					glTexCoord2f(1, 1); glVertex2f(movex + 24, 871);
					glTexCoord2f(1, 0); glVertex2f(movex + 24, 887);
					glTexCoord2f(0, 0); glVertex2f(movex + 8, 887);
				glEnd();
				movex += 64;
			}
			glBindTexture(GL_TEXTURE_2D, tex_person);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1); glVertex2f(433, 831);
				glTexCoord2f(1, 1); glVertex2f(449, 831);
				glTexCoord2f(1, 0); glVertex2f(449, 847);
				glTexCoord2f(0, 0); glVertex2f(433, 847);
			glEnd();
		glEndList();

		try {// misc
			Building.initStatic();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DiscMenu.initDL(250, 350);
	}
	private void initDiscMenus() {
		bmenu_mat = new DiscMenu();
		bmenu_mat.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_goods[0], 2);
			}
			@Override
			public void run() {
				sb = 2;
			}
		});
		bmenu_mat.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_goods[1], 3);
			}
			@Override
			public void run() {
				sb = 3;
			}
		});
		bmenu_mat.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_goods[2], 4);
			}
			@Override
			public void run() {
				sb = 4;
			}
		});
		
		bmenu_liv = new DiscMenu();
		bmenu_liv.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmenu_liv_0, 1);
			}
			@Override
			public void run() {
				sb = 1;
			}
		});
		
		bmenu_fod = new DiscMenu();
		bmenu_fod.addItem(new DiscMenuItem() {
			@Override
			public void run() {
				sb = 6;
			}
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmenu_fod_0, 6);
			}
		});
		
		bmenu = new DiscMenu();
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_bmenu_mat);
			}
			@Override
			public void run() {
				bmenu_mat.active = true;
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_bmenu_liv);
			}
			@Override
			public void run() {
				bmenu_liv.active = true;
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_goods[5]);
			}
			@Override
			public void run() {
				bmenu_fod.active = true;
			}
		});
	}
	private void renderDynDiscMenuIconWithTexture(int tex, int id) {
		renderDiscMenuIconWithTexture(tex);
		if (!Building.canAfford(id)) {
			glLineWidth(5);
			glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
			glColor3f(1, 0, 0);
			glBegin(GL_LINES);
				glVertex2f(-32, -32);
				glVertex2f(32, 32);
				glVertex2f(-32, 32);
				glVertex2f(32, -32);
			glEnd();
			glLineWidth(1);
		}
	}
	private void renderDiscMenuIconWithTexture(int tex) {
		glBindTexture(GL_TEXTURE_2D, tex);
		glColor3f(1, 1, 1);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex2f(-32, -32);
			glTexCoord2f(1, 1); glVertex2f(32, -32);
			glTexCoord2f(1, 0); glVertex2f(32, 32);
			glTexCoord2f(0, 0); glVertex2f(-32, 32);
		glEnd();
	}
}
