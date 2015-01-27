package org.jufi.villagebuilder;

public class BCityHall extends Building {
	public static int tex_m0, tex_m50, tex_m100, tex_m150, tex_m200;
	public static boolean ratesettings;

	Button food = new Button(VB.vb.tex_goods[5], 8, 260, 1000, 0);
	Button food_0 = new Button(tex_m0, 48, 260, 1000, 0);
	Button food_50 = new Button(tex_m50, 88, 260, 1000, 0);
	Button food_100 = new Button(tex_m100, 128, 260, 1000, 0);
	Button food_150 = new Button(tex_m150, 168, 260, 1000, 0);
	Button food_200 = new Button(tex_m200, 208, 260, 1000, 0);
	Button cloth = new Button(VB.vb.tex_goods[10], 8, 220, 1000, 0);
	Button cloth_0 = new Button(tex_m0, 48, 220, 1000, 0);
	Button cloth_50 = new Button(tex_m50, 88, 220, 1000, 0);
	Button cloth_100 = new Button(tex_m100, 128, 220, 1000, 0);
	Button cloth_150 = new Button(tex_m150, 168, 220, 1000, 0);
	Button cloth_200 = new Button(tex_m200, 208, 220, 1000, 0);
	
	public BCityHall(int x, int z, int br) {
		super(x, z, br);
		VB.vb.thlvl = 0;
	}
	public BCityHall(int x, int z, int br, String extra) {
		super(x, z, br);
		VB.vb.thlvl = 0;
	}
	
	@Override
	protected boolean tick() {
		return false;
	}
	
	@Override
	public int getID() {
		return 5;
	}
	
	@Override
	public String getExtra() {
		return null;
	}
	
	@Override
	protected void render2d(boolean click) {
		if (ratesettings) {
			food.render();
			food_0.render();
			food_50.render();
			food_100.render();
			food_150.render();
			food_200.render();
			cloth.render();
			cloth_0.render();
			cloth_50.render();
			cloth_100.render();
			cloth_150.render();
			cloth_200.render();
			if (click && food_0.mouseover()) VB.vb.foodrate = 0f;
			if (click && food_50.mouseover()) VB.vb.foodrate = 0.00005f;
			if (click && food_100.mouseover()) VB.vb.foodrate = 0.0001f;
			if (click && food_150.mouseover()) VB.vb.foodrate = 0.00015f;
			if (click && food_200.mouseover()) VB.vb.foodrate = 0.0002f;
			if (click && cloth_0.mouseover()) VB.vb.clothrate = 0f;
			if (click && cloth_50.mouseover()) VB.vb.clothrate = 0.00005f;
			if (click && cloth_100.mouseover()) VB.vb.clothrate = 0.0001f;
			if (click && cloth_150.mouseover()) VB.vb.clothrate = 0.00015f;
			if (click && cloth_200.mouseover()) VB.vb.clothrate = 0.0002f;
		}
	}
}
