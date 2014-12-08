package org.jufi.lwjglutil;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.Renderable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Model implements Renderable {
	private ArrayList<float[]> vertices = new ArrayList<float[]>();
	private ArrayList<float[]> vtextures = new ArrayList<float[]>();
	private ArrayList<float[]> normals = new ArrayList<float[]>();
	private ArrayList<ModelCommand> commands = new ArrayList<ModelCommand>();
	private ArrayList<Material> materials = new ArrayList<Material>();

	public Model(String path) throws IOException {
		BufferedReader res = new BufferedReader(new FileReader(path));
		String line;

		float[] firstValue = { 0, 0, 0 };
		float[] vfirstValue = { 0, 0 };
		vertices.add(firstValue);
		vtextures.add(vfirstValue);
		normals.add(firstValue);
		while ((line = res.readLine()) != null) {
			readln(line, path);
		}
		res.close();
	}

	public void render() {
		glBegin(GL_TRIANGLES);
		for (ModelCommand command : commands) {
			command.execute(this);
		}
		glEnd();
	}

	public void renderFace(Face face) {
		if (face.normals[0] != 0)
			glNormal3f(normals.get(face.normals[0])[0],
					normals.get(face.normals[0])[1],
					normals.get(face.normals[0])[2]);
		if (face.textures[0] != 0)
			glTexCoord2f(vtextures.get(face.textures[0])[0],
					vtextures.get(face.textures[0])[1]);
		glVertex3f(vertices.get(face.vertices[0])[0],
				vertices.get(face.vertices[0])[1],
				vertices.get(face.vertices[0])[2]);

		if (face.normals[1] != 0)
			glNormal3f(normals.get(face.normals[1])[0],
					normals.get(face.normals[1])[1],
					normals.get(face.normals[1])[2]);
		if (face.textures[1] != 0)
			glTexCoord2f(vtextures.get(face.textures[1])[0],
					vtextures.get(face.textures[1])[1]);
		glVertex3f(vertices.get(face.vertices[1])[0],
				vertices.get(face.vertices[1])[1],
				vertices.get(face.vertices[1])[2]);

		if (face.normals[2] != 0)
			glNormal3f(normals.get(face.normals[2])[0],
					normals.get(face.normals[2])[1],
					normals.get(face.normals[2])[2]);
		if (face.textures[2] != 0)
			glTexCoord2f(vtextures.get(face.textures[2])[0],
					vtextures.get(face.textures[2])[1]);
		glVertex3f(vertices.get(face.vertices[2])[0],
				vertices.get(face.vertices[2])[1],
				vertices.get(face.vertices[2])[2]);
	}
	
	private void readln(String line, String objpath) throws IOException {
		if (line.isEmpty() || line.startsWith("#")) {
			return;
		}

		String[] args = line.split(" ");

		if (args[0].equals("v") && args.length == 4) {
			float[] value = { Float.valueOf(args[1]), Float.valueOf(args[2]),
					Float.valueOf(args[3]) };
			vertices.add(value);
		}
		if (args[0].equals("vn") && args.length == 4) {
			float[] value = { Float.valueOf(args[1]), Float.valueOf(args[2]),
					Float.valueOf(args[3]) };
			normals.add(value);
		}
		if (args[0].equals("vt") && args.length == 3) {
			float[] value = { Float.valueOf(args[1]), Float.valueOf(args[2]) };
			vtextures.add(value);
		}
		if (args[0].equals("f") && args.length == 4) {
			int[] v = new int[args.length - 1];
			int[] vt = new int[args.length - 1];
			int[] vn = new int[args.length - 1];
			for (int i = 0; i < vt.length; i++)
				vt[i] = 0;
			for (int i = 1; i <= 3; i++) {
				if (args[i].contains("/")) {
					if (args[i].contains("//")) {
						String[] splitted = args[i].split("//");
						v[i - 1] = Integer.valueOf(splitted[0]);
						vn[i - 1] = Integer.valueOf(splitted[1]);
					} else {
						String[] splitted = args[i].split("/");
						v[i - 1] = Integer.valueOf(splitted[0]);
						if (splitted.length >= 2) {
							vt[i - 1] = Integer.valueOf(splitted[1]);
						}
						if (splitted.length == 3) {
							vn[i - 1] = Integer.valueOf(splitted[2]);
						}
					}
				} else {
					v[i - 1] = Integer.valueOf(args[i]);
				}
			}
			commands.add(new Face(v, vt, vn));
		}
		if (args[0].equals("mtllib") && args.length == 2) {
			Material.readFile(materials, new File(objpath).toPath().getParent()
					.toString()
					+ "/" + args[1]);

		}
		if (args[0].equals("usemtl") && args.length == 2) {
			for (Material mat : materials) {
				if (mat.name.equals(args[1]))
					commands.add(mat);
			}
		}
	}
	
	public static int getDL(String path) throws IOException {
		int callist = glGenLists(1);
		glNewList(callist, GL_COMPILE);
		new Model(path).render();
		glEndList();
		return callist;
	}

	private static class Face implements ModelCommand {
		public final int[] vertices;
		public final int[] textures;
		public final int[] normals;

		public Face(int[] vertices, int[] textures, int[] normals) {
			this.vertices = vertices;
			this.textures = textures;
			this.normals = normals;
		}

		@Override
		public void execute(Model commander) {
			commander.renderFace(this);
		}
	}

	private static class Material implements ModelCommand {
		public final String name;
		private FloatBuffer ka = BufferUtils.createFloatBuffer(4);
		private FloatBuffer kd = BufferUtils.createFloatBuffer(4);
		private FloatBuffer ks = BufferUtils.createFloatBuffer(4);
		private float ns = 64;
		private int texture = ResourceLoader.white;

		public Material(String name) {
			this.name = name;
			ka.put(1).put(1).put(1).put(1).flip();
			kd.put(1).put(1).put(1).put(1).flip();
			ks.put(1).put(1).put(1).put(1).flip();
		}

		@Override
		public void execute(Model commander) {
			glEnd();
			glMaterial(GL_FRONT_AND_BACK, GL_AMBIENT, ka);
			glMaterial(GL_FRONT_AND_BACK, GL_DIFFUSE, kd);
			glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, ks);
			glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, ns);
			glBindTexture(GL_TEXTURE_2D, texture);
			glBegin(GL_TRIANGLES);
		}
		
		public static void readFile(ArrayList<Material> materials, String path) throws IOException {
			BufferedReader mtlfile = new BufferedReader(new FileReader(path));
			String line;
			
			while ((line = mtlfile.readLine()) != null) {
				if (!(line.isEmpty() || line.startsWith("#"))) {
					String[] args = line.split(" ");
					
					if (args[0].equals("newmtl") && args.length == 2) materials.add(new Material(args[1]));
					else if (args[0].equals("Ka") && args.length == 4) materials.get(materials.size() - 1).ka = PBytes.toFloatBuffer(Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3]), 1);
					else if (args[0].equals("Kd") && args.length == 4) materials.get(materials.size() - 1).kd = PBytes.toFloatBuffer(Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3]), 1);
					else if (args[0].equals("Ks") && args.length == 4) materials.get(materials.size() - 1).ks = PBytes.toFloatBuffer(Float.valueOf(args[1]), Float.valueOf(args[2]), Float.valueOf(args[3]), 1);
					else if (args[0].equals("Ns") && args.length == 2) materials.get(materials.size() - 1).ns = Float.valueOf(args[1]);
					else if ((args[0].equals("map_Ka") || args[0].equals("map_Kd")) && args.length == 2) {
						int texture = ResourceLoader.loadTexture(new File(path).toPath().getParent().toString() + "/" + args[1]);
						materials.get(materials.size() - 1).texture = texture;
					}
				}
			}
			
			mtlfile.close();
		}
	}

	private static interface ModelCommand {
		public void execute(Model commander);
	}
}
