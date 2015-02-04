package org.jufi.villagebuilder;

public class BLiving extends Building {
	private float people;
	private Label l_people = new Label(tex_mpeople, 8, 276, 1, 1, 1);
	
	public BLiving(int x, int z, int br) {
		super(x, z, br);
	}
	public BLiving(int x, int z, int br, String extra) {
		super(x, z, br);
		people = Float.parseFloat(extra);
	}
	
	@Override
	protected boolean tick() {
		VB.vb.workersc += 10;
		VB.vb.workersp += (int) people;
		VB.vb.goods[5] -= Math.floor(people) * VB.vb.foodrate * 0.00035f;
		VB.vb.goods[10] -= Math.floor(people) * VB.vb.clothrate * 0.00035f;
		people += VB.vb.happiness / 100000f;
		if (people > 10) people = 10;
		if (people < 0) people = 0;
		return false;
	}

	@Override
	public int getID() {
		return 1;
	}
	
	@Override
	public String getExtra() {
		return String.valueOf(people);
	}
	
	@Override
	protected void render2d(boolean click) {
		l_people.render("10 / " + (int) people);
	}
}
