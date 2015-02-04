package org.jufi.villagebuilder;

import org.jufi.lwjglutil.Draw;

public class BCityHall extends Building {
	public static int tex_m0, tex_m50, tex_m100, tex_m150, tex_m200, tex_marrow;
	public static boolean ratesettings;
	public static int[] dl_ch = new int[2];

	Button food = new Button(VB.vb.tex_goods[5], 8, 220, 1000, 0);
	Button food_0 = new Button(tex_m0, 48, 220, 1000, 0);
	Button food_50 = new Button(tex_m50, 88, 220, 1000, 0);
	Button food_100 = new Button(tex_m100, 128, 220, 1000, 0);
	Button food_150 = new Button(tex_m150, 168, 220, 1000, 0);
	Button food_200 = new Button(tex_m200, 208, 220, 1000, 0);
	Button cloth = new Button(VB.vb.tex_goods[10], 8, 180, 1000, 0);
	Button cloth_0 = new Button(tex_m0, 48, 180, 1000, 0);
	Button cloth_50 = new Button(tex_m50, 88, 180, 1000, 0);
	Button cloth_100 = new Button(tex_m100, 128, 180, 1000, 0);
	Button cloth_150 = new Button(tex_m150, 168, 180, 1000, 0);
	Button cloth_200 = new Button(tex_m200, 208, 180, 1000, 0);
	Button b_upgrade = new Button(tex_marrow, 8, 260, 1000, 0);
	
	public BCityHall(int x, int z, int br) {
		super(x, z, br);
	}
	public BCityHall(int x, int z, int br, String extra) {
		super(x, z, br);
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
	protected int getDL() {
		return dl_ch[VB.vb.thlvl];
	}
	
	@Override
	protected void render2d(boolean click) {
		if (VB.vb.thlvl < 1) {
			if (b_upgrade.mouseover()) {
				renderUpgradeCosts();
				if (click && canAffordUpgrade()) {
					VB.vb.thlvl++;
					VB.vb.thlvlup[VB.vb.thlvl].run();
					switch (VB.vb.thlvl) {
					case 1:
						VB.vb.goods[0] -= 100;
						VB.vb.goods[1] -= 200;
						VB.vb.goods[2] -= 100;
						VB.vb.goods[10] -= 1;
						break;
					default:
						System.err.println("BCityHall: invalid thlvl in switch statement");
					}
				}
			}
			b_upgrade.render();
		}
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
			if (click && food_0.mouseover()) VB.vb.foodrate = 0;
			if (click && food_50.mouseover()) VB.vb.foodrate = 1;
			if (click && food_100.mouseover()) VB.vb.foodrate = 2;
			if (click && food_150.mouseover()) VB.vb.foodrate = 3;
			if (click && food_200.mouseover()) VB.vb.foodrate = 4;
			if (click && cloth_0.mouseover()) VB.vb.clothrate = 0;
			if (click && cloth_50.mouseover()) VB.vb.clothrate = 1;
			if (click && cloth_100.mouseover()) VB.vb.clothrate = 2;
			if (click && cloth_150.mouseover()) VB.vb.clothrate = 3;
			if (click && cloth_200.mouseover()) VB.vb.clothrate = 4;
		}
	}
	
	private void renderUpgradeCosts() {
		switch (VB.vb.thlvl) {
		case 0:
			if (100 <= VB.vb.goods[0]) Draw.drawString("100", -863, 835, 0, 1, 0);
			else Draw.drawString("100", -863, 835, 1, 0, 0);
			if (200 <= VB.vb.goods[1]) Draw.drawString("200", -799, 835, 0, 1, 0);
			else Draw.drawString("200", -799, 835, 1, 0, 0);
			if (100 <= VB.vb.goods[2]) Draw.drawString("100", -735, 835, 0, 1, 0);
			else Draw.drawString("100", -735, 835, 1, 0, 0);
			if (1 <= VB.vb.goods[10]) Draw.drawString("1", -223, 835, 0, 1, 0);
			else Draw.drawString("1", -223, 835, 1, 0, 0);
			break;
		default:
			System.err.println("BCityHall: invalid thlvl in switch statement");
		}
	}
	
	private boolean canAffordUpgrade() {
		switch (VB.vb.thlvl) {
		case 0: return VB.vb.goods[0] >= 100 && VB.vb.goods[1] >= 200 && VB.vb.goods[2] >= 100 && VB.vb.goods[10] >= 1;
		default:
			System.err.println("BCityHall: invalid thlvl in switch statement");
			return false;
		}
	}
}
