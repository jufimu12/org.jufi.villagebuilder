package org.jufi.lwjglutil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PhysMap {
	private float[][] physmap;
	public PhysMap() {
		
	}
	public PhysMap(String path, float objectHeight, float objectWidth) throws IOException {
		physmap = loadPhysModel(path, objectHeight, objectWidth);
	}
	public void load(String path, float objectHeight, float objectWidth) throws IOException {
		physmap = loadPhysModel(path, objectHeight, objectWidth);
	}
	
	private float[][] loadPhysModel(String path, float objectHeight, float objectWidth) throws IOException {
		BufferedReader res = new BufferedReader(new FileReader(path));
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> objects = new ArrayList<float[]>();
		
		String line;
		while ((line = res.readLine()) != null) {
			if (!line.isEmpty() && !line.startsWith("#")) {
				
				String[] args = line.split(" ");
				
				if (args[0].equals("v") && args.length == 4) {
					float[] entry = {Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3])};
					vertices.add(entry);
				}
				if (args[0].equals("o") && args.length == 3) {
					float x1 = vertices.get(Integer.valueOf(args[1]) - 1)[0];
					float x2 = vertices.get(Integer.valueOf(args[2]) - 1)[0];
					float y1 = vertices.get(Integer.valueOf(args[1]) - 1)[1];
					float y2 = vertices.get(Integer.valueOf(args[2]) - 1)[1];
					float z1 = vertices.get(Integer.valueOf(args[1]) - 1)[2];
					float z2 = vertices.get(Integer.valueOf(args[2]) - 1)[2];
					
					float xmin, xmax, ymin, ymax, zmin, zmax;
					
					if (x1 > x2) {
						xmin = x2;
						xmax = x1;
					} else {
						xmin = x1;
						xmax = x2;
					}
					if (y1 > y2) {
						ymin = y2;
						ymax = y1;
					} else {
						ymin = y1;
						ymax = y2;
					}
					if (z1 > z2) {
						zmin = z2;
						zmax = z1;
					} else {
						zmin = z1;
						zmax = z2;
					}
					
					ymin -= objectHeight;
					xmin -= objectWidth;
					xmax += objectWidth;
					zmin -= objectWidth;
					zmax += objectWidth;
					
					float[] entry = {xmin, xmax, ymin, ymax, zmin, zmax};
					objects.add(entry);
				}
			}
		}
		res.close();
		
		float[][] output = new float[objects.size()][6];
		for (int i = 0; i < objects.size(); i++) {
			output[i] = objects.get(i);
		}
		
		return output;
	}
	
	public boolean collides(float x, float y, float z) {
		for (float[] points : physmap) {
			if (x > points[0] && x < points[1] && y > points[2] && y < points[3] && z > points[4] && z < points[5]) return true;
		}
		return false;
	}
}
