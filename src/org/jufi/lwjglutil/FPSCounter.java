package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;

public class FPSCounter {
    private int currentFPS;
    private int fps;
    private long start;
    
    public void tick() {
        currentFPS++;
        if (System.currentTimeMillis() - start >= 1000) {
            fps = currentFPS;
            currentFPS = 0;
            start = System.currentTimeMillis();
        }
    }
    
    public int getFPS() {
        return fps;
    }
    public void dispFPS(int resYOrtho, float size) {
    	glBindTexture(GL_TEXTURE_2D, ResourceLoader.white);
    	
    	glTranslatef(0, resYOrtho, 0);
    	glScalef(size, size, 1);
    	
		Draw.drawStringBG(fps, 0, -9, 0, 1, 0);
    }
}
