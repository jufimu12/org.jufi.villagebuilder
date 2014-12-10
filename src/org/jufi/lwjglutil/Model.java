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
	private ModelCommand[] commands;

	public Model(String path) throws IOException {
		ArrayList<ModelCommand> cmds = new ArrayList<ModelCommand>();
		ArrayList<float[]> vertices = new ArrayList<float[]>();
		ArrayList<float[]> vtextures = new ArrayList<float[]>();
		ArrayList<float[]> normals = new ArrayList<float[]>();
		ArrayList<Material> materials = new ArrayList<Material>();

		vertices.add(new float[] {0, 0, 0});
		vtextures.add(new float[] {0, 0});
		normals.add(new float[] {0, 0, 0});
		
		BufferedReader res = new BufferedReader(new FileReader(path));

		String line;
		while ((line = res.readLine()) != null) {
			if (!line.isEmpty() && !line.startsWith("#")) {
				String[] args = line.split(" ");
	
				if (args[0].equals("v") && args.length == 4) {
					float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2]),
							Float.valueOf(args[3])};
					vertices.add(value);
				}
				if (args[0].equals("vn") && args.length == 4) {
					float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2]),
							Float.valueOf(args[3])};
					normals.add(value);
				}
				if (args[0].equals("vt") && args.length == 3) {
					float[] value = {Float.valueOf(args[1]), Float.valueOf(args[2])};
					vtextures.add(value);
				}
				if (args[0].equals("f") && args.length == 4) {
					float[] v = new float[9];
					float[] vt = new float[6];
					float[] vn = new float[9];
					boolean[] tex = new boolean[3];
					for (int i = 0; i < 3; i++) {
						if (args[i + 1].contains("/")) {
							if (args[i + 1].contains("//")) {
								String[] splitted = args[i + 1].split("//");
								v[i * 3] = vertices.get(Integer.valueOf(splitted[0]))[0];
								v[i * 3 + 1] = vertices.get(Integer.valueOf(splitted[0]))[1];
								v[i * 3 + 2] = vertices.get(Integer.valueOf(splitted[0]))[2];
								vn[i * 3] = normals.get(Integer.valueOf(splitted[1]))[0];
								vn[i * 3 + 1] = normals.get(Integer.valueOf(splitted[1]))[1];
								vn[i * 3 + 2] = normals.get(Integer.valueOf(splitted[1]))[2];
							} else {
								tex[i] = true;
								String[] splitted = args[i + 1].split("/");
								v[i * 3] = vertices.get(Integer.valueOf(splitted[0]))[0];
								v[i * 3 + 1] = vertices.get(Integer.valueOf(splitted[0]))[1];
								v[i * 3 + 2] = vertices.get(Integer.valueOf(splitted[0]))[2];
								vt[i * 2] = vtextures.get(Integer.valueOf(splitted[1]))[0];
								vt[i * 2 + 1] = vtextures.get(Integer.valueOf(splitted[1]))[1];
								if (splitted.length == 3) {
									vn[i * 3] = normals.get(Integer.valueOf(splitted[2]))[0];
									vn[i * 3 + 1] = normals.get(Integer.valueOf(splitted[2]))[1];
									vn[i * 3 + 2] = normals.get(Integer.valueOf(splitted[2]))[2];
								}
							}
						} else {
							v[i * 3] = vertices.get(Integer.valueOf(args[i + 1]))[0];
							v[i * 3 + 1] = vertices.get(Integer.valueOf(args[i + 1]))[1];
							v[i * 3 + 2] = vertices.get(Integer.valueOf(args[i + 1]))[2];
						}
					}
					cmds.add(new Face(v, vt, vn, tex));
				}
				if (args[0].equals("mtllib") && args.length == 2) {
					String mpath = new File(path).toPath().getParent().toString() + "/" + args[1];
					BufferedReader mtlfile = new BufferedReader(new FileReader(mpath));
					String mline;
					
					while ((mline = mtlfile.readLine()) != null) {
						if (!(mline.isEmpty() || mline.startsWith("#"))) {
							String[] margs = mline.split(" ");
							
							if (margs[0].equals("newmtl") && margs.length == 2) materials.add(new Material(margs[1]));
							else if (margs[0].equals("Ka") && margs.length == 4) materials.get(materials.size() - 1).ka = PBytes.toFloatBuffer(Float.valueOf(margs[1]), Float.valueOf(margs[2]), Float.valueOf(margs[3]), 1);
							else if (margs[0].equals("Kd") && margs.length == 4) materials.get(materials.size() - 1).kd = PBytes.toFloatBuffer(Float.valueOf(margs[1]), Float.valueOf(margs[2]), Float.valueOf(margs[3]), 1);
							else if (margs[0].equals("Ks") && margs.length == 4) materials.get(materials.size() - 1).ks = PBytes.toFloatBuffer(Float.valueOf(margs[1]), Float.valueOf(margs[2]), Float.valueOf(margs[3]), 1);
							else if (margs[0].equals("Ns") && margs.length == 2) materials.get(materials.size() - 1).ns = Float.valueOf(margs[1]);
							else if ((margs[0].equals("map_Ka") || margs[0].equals("map_Kd")) && margs.length == 2) {
								int texture = ResourceLoader.loadTexture(new File(mpath).toPath().getParent().toString() + "/" + margs[1]);
								materials.get(materials.size() - 1).texture = texture;
							}
						}
					}
					
					mtlfile.close();
				}
				if (args[0].equals("usemtl") && args.length == 2) {
					for (Material mat : materials) {
						if (mat.name.equals(args[1]))
							cmds.add(mat);
					}
				}
			}
		}
		res.close();
		
		commands = new ModelCommand[cmds.size()];
		for (int i = 0; i < cmds.size(); i++) {
			commands[i] = cmds.get(i);
		}
	}

	public void render() {
		glBegin(GL_TRIANGLES);
		for (ModelCommand command : commands) {
			command.execute(this);
		}
		glEnd();
	}
	
	public static int getDL(String path) throws IOException {
		int callist = glGenLists(1);
		glNewList(callist, GL_COMPILE);
		new Model(path).render();
		glEndList();
		return callist;
	}

	private static class Face implements ModelCommand {
		private final float v0, v1, v2, v3, v4, v5, v6, v7, v8;
		private final float t0, t1, t2, t3, t4, t5;
		private final float n0, n1, n2, n3, n4, n5, n6, n7, n8;
		private final boolean texen0, texen1, texen2;

		public Face(float[] v, float[] t, float[] n, boolean[] texen) {
			v0 = v[0];
			v1 = v[1];
			v2 = v[2];
			v3 = v[3];
			v4 = v[4];
			v5 = v[5];
			v6 = v[6];
			v7 = v[7];
			v8 = v[8];
			t0 = t[0];
			t1 = t[1];
			t2 = t[2];
			t3 = t[3];
			t4 = t[4];
			t5 = t[5];
			n0 = n[0];
			n1 = n[1];
			n2 = n[2];
			n3 = n[3];
			n4 = n[4];
			n5 = n[5];
			n6 = n[6];
			n7 = n[7];
			n8 = n[8];
			texen0 = texen[0];
			texen1 = texen[1];
			texen2 = texen[2];
		}

		@Override
		public void execute(Model commander) {
			glNormal3f(n0, n1, n2);
			if (texen0) glTexCoord2f(t0, t1);
			glVertex3f(v0, v1, v2);
			
			glNormal3f(n3, n4, n5);
			if (texen1) glTexCoord2f(t2, t3);
			glVertex3f(v3, v4, v5);
			
			glNormal3f(n6, n7, n8);
			if (texen2) glTexCoord2f(t4, t5);
			glVertex3f(v6, v7, v8);
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
	}

	private static interface ModelCommand {
		public void execute(Model commander);
	}
}
