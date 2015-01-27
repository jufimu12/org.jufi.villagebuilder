package org.jufi.villagebuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
	public static final float BGC = 1f / 16f;
	
	public static VB vb;
	
	public static void main(String[] main) {
		vb = new VB();
		vb.start();// debugging: initial good amount, instant build
	}
	
	
	private int dl_map, dl_hud;// VARS
	private float camheight = 25;
	private boolean lmup, shiftdown, rup;
	private boolean rendermark;
	private boolean rmtool;
	private ArrayList<Building> buildings = new ArrayList<Building>();
	private int sb, br;
	public int mousex, mousez;
	public float[] goods = new float[11];
	public int[] tex_goods = new int[11];
	private int tex_bmmat, tex_bmliv, tex_bmspc;
	private int tex_bmliv_0, tex_bmfod_0, tex_bmspc_0, tex_bmspc_1, tex_bmfod_3, tex_bmclo_0;
	private int tex_smiley;
	private int goodlimit = 1000;
	public int goodlimittick = 1000, thlvl;
	private DiscMenu bmenu, bmenu_mat, bmenu_liv, bmenu_fod, bmenu_clo, bmenu_spc;
	public float workersp, workersm, workersq, workersc;
	public float happiness = 50, dhappiness, foodrate = 0.0001f, clothrate = 0.0001f;
	public Runnable[][] tech = new Runnable[1][];
	
	
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
		// Nothing random here! It could override these fragments
		if (rendermark) {
			glDisable(GL_CULL_FACE);
			glPushMatrix();
				glTranslatef(mousex, 0, mousez);
				switch (br) {
				case 0:
					break;
				case 1:
					glTranslatef(0, 0, 1);
					glRotatef(90, 0, 1, 0);
					break;
				case 2:
					glTranslatef(1, 0, 1);
					glRotatef(180, 0, 1, 0);
					break;
				case 3:
					glTranslatef(1, 0, 0);
					glRotatef(270, 0, 1, 0);
					break;
				default:
					System.err.println("Invalid br in VB");
				}
				glColor3f(0, 0.5f, 0);
				glBegin(GL_QUADS);
					glVertex3f(0, 0, 0);
					glVertex3f(0, 0, Building.sizeZ[sb]);
					glVertex3f(Building.sizeX[sb], 0, Building.sizeZ[sb]);
					glVertex3f(Building.sizeX[sb], 0, 0);
				glEnd();
				
				if (selectionavailable()) {
					if (Building.canAfford(sb)) glColor3f(1, 1, 1);
					else glColor3f(0.5f, 0.5f, 0.5f);
				} else {
					if (Building.canAfford(sb)) glColor3f(1, 0, 0);
					else glColor3f(0.5f, 0, 0);
				}
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				if (sb > 0) glCallList(Building.dls[sb]);
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			glPopMatrix();
			glEnable(GL_CULL_FACE);
		}
		// Nothing random here! It could override these fragments
		if (mousex >= 0 && mousex < MAP_SIZE && mousez >= 0 && mousez < MAP_SIZE) {
			rendermark = true;
			if (rmtool) {
				glColor3f(1, 0, 0);
			} else {
				if (Building.occupied(mousex, mousez, buildings)) glColor3f(0.5f, 0.5f, 0.5f);
				else glColor3f(1, 1, 1);
			}
			glBegin(GL_QUADS);
				glVertex3f(mousex, 0, mousez);
				glVertex3f(mousex, 0, mousez + 1);
				glVertex3f(mousex + 1, 0, mousez + 1);
				glVertex3f(mousex + 1, 0, mousez);
			glEnd();
		} else {
			rendermark = false;
		}
		// Nothing random here! It could override these fragments
		glLineWidth(5);
		glPointSize(5);
		if (sb > 0) {
			for (Building b : buildings) {
				b.renderDynContour(sb, br);
			}
		} else if (!shiftdown) {
			for (Building b : buildings) {
				if (b.occupies(mousex, mousez)) {
					if (rmtool && b.getID() != 5) glColor3f(1, 0, 0);
					else glColor3f(1, 1, 1);
					b.renderStatContour();
				}
			}
		}
		glLineWidth(1);
		glPointSize(1);
		glEnable(GL_DEPTH_TEST);
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
		Draw.drawString("X " + (int) cam.getTx(), 1, 860, 1, 1, 1);
		Draw.drawString("Y " + (int) cam.getTy(), 1, 850, 1, 1, 1);
		Draw.drawString("Z " + (int) cam.getTz(), 1, 840, 1, 1, 1);
		Draw.drawString("R " + (int) cam.getRy(), 1, 830, 1, 1, 1);
		Draw.drawString("MX" + mousex, 1, 820, 1, 1, 1);
		Draw.drawString("MZ" + mousez, 1, 810, 1, 1, 1);
		int movex = 137;
		for (int i = 0; i < tex_goods.length; i++) {
			Draw.drawString((int) goods[i], movex, 875, 1, 1, 1);
			if (i < 5 && sb > 0) {
				if (Building.cost[sb][i] <= goods[i]) Draw.drawString(Building.cost[sb][i], movex, 835, 0, 1, 0);
				else Draw.drawString(Building.cost[sb][i], movex, 835, 1, 0, 0);
			}
			movex += 64;
		}
		Draw.drawString("&$c100100000" + (int) workersc + "&$c100100100 / &$c000100000" + (int) workersp + "&$c100100100 / &$c100000000" + (int) workersm + "&$c100100100 / " + (int) (workersq * 100f), 457, 835, 1, 1, 1);
		if (sb > 0) Draw.drawString(Building.cost[sb][5], 457, 810, 1, 1, 1);
		Draw.drawString(Math.floor(goodlimit / 100f) / 10f + "k", 649, 835, 1, 1, 1);
		float hr = 1, hg = 1;
		if (happiness >= 0) hr -= happiness / 100f;
		else hg += happiness / 100f;
		if (dhappiness > 0) Draw.drawString((int) happiness + "&$c100100100 / &$c000100000" + Math.floor(dhappiness * 6000f) / 100f, 713, 835, hr, hg, 0);
		else if (dhappiness < 0) Draw.drawString((int) happiness + "&$c100100100 / &$c100000000" + Math.floor(dhappiness * 6000f) / 100f, 713, 835, hr, hg, 0);
		else Draw.drawString((int) happiness + "&$c100100100 / " + Math.floor(dhappiness * 6000f) / 100f, 713, 835, hr, hg, 0);
		happiness += dhappiness;
		dhappiness = 0;
		
		glPushMatrix();
			glTranslatef(1000, 0, 0);
			for (Building b : buildings) {
				b.run2d();
			}
		glPopMatrix();
		
		bmenu.render();
		bmenu_mat.render();
		bmenu_liv.render();
		bmenu_fod.render();
		bmenu_clo.render();
		bmenu_spc.render();
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
		if (cam.getTx() < -50) cam.setTx(-50);
		if (cam.getTx() > MAP_SIZE + 50) cam.setTx(MAP_SIZE + 50);
		if (cam.getTz() < -50) cam.setTz(-50);
		if (cam.getTz() > MAP_SIZE + 50) cam.setTz(MAP_SIZE + 50);
		float oldcamheight = camheight;
		camheight -= Mouse.getDWheel() / 24f;
		if (camheight < 10) camheight = 10;
		if (camheight > 100) camheight = 100;
		cam.moveY(true, oldcamheight - camheight);
		if (isKeyDown(KEY_X) && !shiftdown) {
			rmtool = true;
			sb = 0;
		}
		if (Mouse.isButtonDown(1)) {
			rmtool = false;
			sb = 0;
		}
		if (isKeyDown(KEY_SPACE)) {
			if (!shiftdown && !rmtool) {
				bmenu.active = true;
				shiftdown = true;
			}
		} else {
			if (shiftdown) {
				bmenu.active = false;
				bmenu_mat.active = false;
				bmenu_liv.active = false;
				bmenu_fod.active = false;
				bmenu_clo.active = false;
				bmenu_spc.active = false;
				shiftdown = false;
			}
		}
		if (isKeyDown(KEY_R)) {
			if (rup && sb > 0) {
				br++;
				if (br > 3) br = 0;
			}
			rup = false;
		} else rup = true;
		if (isKeyDown(KEY_F9)) save();
		if (isKeyDown(KEY_F10)) load();
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
		workersc = 0;
		
		goodlimit = goodlimittick;
		goodlimittick = 1000;
		if (goodlimit > 9999) goodlimit = 9999;
		
		if (happiness < -100) happiness = -100;
		if (happiness > 100) happiness = 100;
		
		if (!shiftdown && Mouse.isButtonDown(0)) {
			if (lmup) {
				lmup = false;
				if (rmtool) {
					Iterator<Building> ib = buildings.iterator();
					Building b;
					while (ib.hasNext()) {
						b = ib.next();
						if (b.occupies(mousex, mousez) && b.getID() != 5) ib.remove();
					}
				}
				if (sb > 0 && selectionavailable() && Building.canAfford(sb)) {
					Building b = Building.get(sb, mousex, mousez, br);
					if (b != null) {
						buildings.add(b);
						goods[0] -= Building.cost[sb][0];
						goods[1] -= Building.cost[sb][1];
						goods[2] -= Building.cost[sb][2];
						goods[3] -= Building.cost[sb][3];
						goods[4] -= Building.cost[sb][4];
					}
				}
				if (!rmtool && !shiftdown && sb == 0) {
					for (Building b : buildings) {
						if (b.occupies(mousex, mousez)) b.onMouseClick();
					}
				}
			}
		} else lmup = true;
		
		Iterator<Building> ib = buildings.iterator();
		while (ib.hasNext()) {
			if (ib.next().run()) ib.remove();
		}
		
		for (int i = 0; i < goods.length; i++) {
			if (goods[i] > goodlimit) goods[i] = goodlimit;
			if (goods[i] < 0) goods[i] = 0;
		}
		if (goods[5] == 0) dhappiness -= 0.008f;
		else dhappiness += foodrate * 100f - 0.00833333f;
		if (thlvl == 0) {
			if (goods[10] > 0) dhappiness += clothrate * 100f - 0.00833333f;
		} else {
			if (goods[10] == 0) dhappiness -= 0.008f;
			else dhappiness += clothrate * 100f - 0.00833333f;
		}
	}
	
	@Override
	protected void preInit() {
		System.out.println("Launching Villagebuilder");
	}
	@Override
	protected void postInit() {
		System.out.println("Loading resources");
		try {// shader
			sh_main = new int[3];
			sh_main[0] = ResourceLoader.loadShader("res/shader/3d.vsh", "res/shader/3d.fsh")[0];
			sh_main[1] = ResourceLoader.loadShader("res/shader/3dnl.vsh", "res/shader/3dnl.fsh")[0];
			sh_main[2] = ResourceLoader.loadShader("res/shader/2d.vsh", "res/shader/2d.fsh")[0];
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to init shaders");
		}
		
		initGoods();
		initTex();
		initDisplayLists();
		initDiscMenus();
		initTech();
		
		glClearColor(0.53f, 0.81f, 0.92f, 1f);
		Mouse.getDWheel();
		buildings.add(Building.get(5, MAP_SIZE / 2, MAP_SIZE / 2, 0));
		System.out.println("Done loading");
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
	private boolean selectionavailable() {
		if (mousex < 0 || mousez < 0 || mousex + Building.sizeX[sb] > MAP_SIZE || mousez + Building.sizeZ[sb] > MAP_SIZE) return false;
		switch (br) {
		case 0:
			for (int x = mousex; x < mousex + Building.sizeX[sb]; x++) {
				for (int z = mousez; z < mousez + Building.sizeZ[sb]; z++) {
					if (Building.occupied(x, z, buildings)) return false;
				}
			}
			break;
		case 1:
			for (int x = mousex; x < mousex + Building.sizeZ[sb]; x++) {
				for (int z = mousez - Building.sizeX[sb] + 1; z <= mousez; z++) {
					if (Building.occupied(x, z, buildings)) return false;
				}
			}
			break;
		case 2:
			for (int x = mousex - Building.sizeX[sb] + 1; x <= mousex; x++) {
				for (int z = mousez - Building.sizeZ[sb] + 1; z <= mousez; z++) {
					if (Building.occupied(x, z, buildings)) return false;
				}
			}
			break;
		case 3:
			for (int x = mousex - Building.sizeZ[sb] + 1; x <= mousex; x++) {
				for (int z = mousez; z < mousez + Building.sizeX[sb]; z++) {
					if (Building.occupied(x, z, buildings)) return false;
				}
			}
			break;
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
		goods[0] = 200;
		goods[1] = 200;
		goods[2] = 100;
		goods[3] = 0;
		goods[4] = 0;
		goods[5] = 200;
		goods[6] = 0;
		goods[7] = 0;
		goods[8] = 0;
		goods[9] = 0;
		goods[10] = 0;
	}
	private void initTex() {
		try {
			tex_goods[0] = ResourceLoader.loadTexture("res/img/bmat0.png");
			tex_goods[1] = ResourceLoader.loadTexture("res/img/bmat1.png");
			tex_goods[2] = ResourceLoader.loadTexture("res/img/bmat2.png");
			tex_goods[3] = ResourceLoader.loadTexture("res/img/gsteel.png");
			tex_goods[4] = ResourceLoader.loadTexture("res/img/gglass.png");
			tex_goods[5] = ResourceLoader.loadTexture("res/img/bfod.png");
			tex_goods[6] = ResourceLoader.loadTexture("res/img/bfod1.png");
			tex_goods[7] = ResourceLoader.loadTexture("res/img/bfod2.png");
			tex_goods[8] = ResourceLoader.loadTexture("res/img/bclo1.png");
			tex_goods[9] = ResourceLoader.loadTexture("res/img/bclo2.png");
			tex_goods[10] = ResourceLoader.loadTexture("res/img/bclo.png");
			
			tex_bmmat = ResourceLoader.loadTexture("res/img/bmat.png");
			tex_bmliv = ResourceLoader.loadTexture("res/img/bliv.png");
			tex_bmspc = ResourceLoader.loadTexture("res/img/bspc.png");
			tex_bmliv_0 = ResourceLoader.loadTexture("res/img/bliv0.png");
			tex_bmfod_0 = ResourceLoader.loadTexture("res/img/bfod0.png");
			tex_bmfod_3 = ResourceLoader.loadTexture("res/img/bfod3.png");
			tex_bmspc_0 = ResourceLoader.loadTexture("res/img/bspc0.png");
			tex_bmspc_1 = ResourceLoader.loadTexture("res/img/bspc1.png");
			tex_bmclo_0 = ResourceLoader.loadTexture("res/img/bclo0.png");
			
			tex_smiley = ResourceLoader.loadTexture("res/img/ssmiley.png");
			Building.tex_mconstruction = ResourceLoader.loadTexture("res/img/mconstruction.png");
			Building.tex_mgear = ResourceLoader.loadTexture("res/img/mgear.png");
			Building.tex_mpeople = tex_bmliv;
			BCityHall.tex_m0 = ResourceLoader.loadTexture("res/img/m0.png");
			BCityHall.tex_m50 = ResourceLoader.loadTexture("res/img/m50.png");
			BCityHall.tex_m100 = ResourceLoader.loadTexture("res/img/m100.png");
			BCityHall.tex_m150 = ResourceLoader.loadTexture("res/img/m150.png");
			BCityHall.tex_m200 = ResourceLoader.loadTexture("res/img/m200.png");
			BStorage.tex_mcrate = tex_bmspc_0;
			BSchool.tex_locked[0] = tex_goods[2];
			BSchool.tex_locked[1] = ResourceLoader.loadTexture("res/img/tfoodrate.png");
			BSchool.tex_locked[2] = tex_bmfod_3;
			BSchool.tex_locked[3] = tex_goods[10];
		} catch (IOException e) {
			System.err.println("While loading textures:");
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
				for (int x = -MAP_SIZE * 2; x < 3 * MAP_SIZE; x += MAP_SIZE / MAP_DIV) {
					for (int y = -MAP_SIZE * 2; y < 3 * MAP_SIZE; y += MAP_SIZE / MAP_DIV) {
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
			glColor3f(1, 0, 0);
			glBegin(GL_LINE_LOOP);
				glVertex3f(0, 0, 0);
				glVertex3f(0, 0, MAP_SIZE);
				glVertex3f(MAP_SIZE, 0, MAP_SIZE);
				glVertex3f(MAP_SIZE, 0, 0);
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
			glBindTexture(GL_TEXTURE_2D, tex_bmliv_0);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1); glVertex2f(433, 831);
				glTexCoord2f(1, 1); glVertex2f(449, 831);
				glTexCoord2f(1, 0); glVertex2f(449, 847);
				glTexCoord2f(0, 0); glVertex2f(433, 847);
			glEnd();
			glBindTexture(GL_TEXTURE_2D, tex_bmspc_0);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1); glVertex2f(625, 831);
				glTexCoord2f(1, 1); glVertex2f(641, 831);
				glTexCoord2f(1, 0); glVertex2f(641, 847);
				glTexCoord2f(0, 0); glVertex2f(625, 847);
			glEnd();
			glBindTexture(GL_TEXTURE_2D, tex_smiley);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 1); glVertex2f(689, 831);
				glTexCoord2f(1, 1); glVertex2f(705, 831);
				glTexCoord2f(1, 0); glVertex2f(705, 847);
				glTexCoord2f(0, 0); glVertex2f(689, 847);
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
		
		bmenu_liv = new DiscMenu();
		bmenu_liv.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmliv_0, 1);
			}
			@Override
			public void run() {
				sb = 1;
			}
		});
		
		bmenu_fod = new DiscMenu();
		bmenu_fod.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmfod_0, 6);
			}
			@Override
			public void run() {
				sb = 6;
			}
		});
		
		bmenu_clo = new DiscMenu();
		bmenu_clo.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_goods[8], 1);
			}
			@Override
			public void run() {
				sb = 12;
			}
		});
		bmenu_clo.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_goods[9], 1);
			}
			@Override
			public void run() {
				sb = 13;
			}
		});
		bmenu_clo.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmclo_0, 1);
			}
			@Override
			public void run() {
				sb = 14;
			}
		});
		
		bmenu_spc = new DiscMenu();
		bmenu_spc.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmspc_0, 7);
			}
			@Override
			public void run() {
				sb = 7;
			}
		});
		bmenu_spc.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDynDiscMenuIconWithTexture(tex_bmspc_1, 8);
			}
			@Override
			public void run() {
				sb = 8;
			}
		});
		
		bmenu = new DiscMenu();
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_bmmat);
			}
			@Override
			public void run() {
				bmenu_mat.active = true;
			}
		});
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_bmliv);
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
		bmenu.addItem(new DiscMenuItem() {
			@Override
			public void render() {
				renderDiscMenuIconWithTexture(tex_bmspc);
			}
			@Override
			public void run() {
				bmenu_spc.active = true;
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
	private void initTech() {
		tech[0] = new Runnable[4];
		tech[0][0] = new Runnable() {
			@Override
			public void run() {
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
			}
		};
		tech[0][1] = new Runnable() {
			@Override
			public void run() {
				BCityHall.ratesettings = true;
			}
		};
		tech[0][2] = new Runnable() {
			@Override
			public void run() {
				bmenu_fod.addItem(new DiscMenuItem() {
					@Override
					public void render() {
						renderDynDiscMenuIconWithTexture(tex_goods[6], 9);
					}
					@Override
					public void run() {
						sb = 9;
					}
				});
				bmenu_fod.addItem(new DiscMenuItem() {
					@Override
					public void render() {
						renderDynDiscMenuIconWithTexture(tex_goods[7], 10);
					}
					@Override
					public void run() {
						sb = 10;
					}
				});
				bmenu_fod.addItem(new DiscMenuItem() {
					@Override
					public void render() {
						renderDynDiscMenuIconWithTexture(tex_bmfod_3, 11);
					}
					@Override
					public void run() {
						sb = 11;
					}
				});
			}
		};
		tech[0][3] = new Runnable() {
			@Override
			public void run() {
				bmenu.addItem(new DiscMenuItem() {
					@Override
					public void render() {
						renderDiscMenuIconWithTexture(tex_goods[10]);
					}
					@Override
					public void run() {
						bmenu_clo.active = true;
					}
				});
			}
		};
	}
	private void save() {
		BufferedWriter target = null;
		try {
			JFileChooser fc = new JFileChooser();
			if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;
			target = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
			
			target.write("s h " + happiness);
			target.newLine();
			for (int i = 0; i < goods.length; i++) {
				target.write("g " + i + " " + goods[i]);
				target.newLine();
			}
			for (Building b : buildings) {
				target.write("b " + b.getID() + " " + b.getX() + " " + b.getZ() + " " + b.getBR() + " " + b.getExtra());
				target.newLine();
			}
			JOptionPane.showMessageDialog(null, "Succesfully saved the game", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			System.err.println("Exception while trying to save game:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Something went wrong. See console log for details", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (target != null) {
				try {
					target.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void load() {
		Building.takestimetobuild = false;
		float[] newgoods = new float[goods.length];
		ArrayList<Building> newbuildings = new ArrayList<Building>();
		BufferedReader source = null;
		try {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
			source = new BufferedReader(new FileReader(fc.getSelectedFile()));
			
			for (String in = source.readLine(); in != null; in = source.readLine()) {
				String[] args = in.split(" ");
				switch (args[0]) {
				case "g":
					newgoods[Integer.parseInt(args[1])] = Float.parseFloat(args[2]);
					break;
				case "b":
					newbuildings.add(Building.get(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]));
					break;
				case "s":
					switch (args[1]) {
					case "h":
						happiness = Float.parseFloat(args[2]);
						break;
					default:
							System.out.println("Invalid line in input file:");
							System.out.println(in);
					}
					break;
				default:
					System.out.println("Invalid line in input file:");
					System.out.println(in);
				}
			}
			goods = newgoods;
			buildings = newbuildings;
		} catch (IOException e) {
			System.err.println("Failed to load game");
			e.printStackTrace();
		} finally {
			if (source != null) {
				try {
					source.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Building.takestimetobuild = true;
	}
}
