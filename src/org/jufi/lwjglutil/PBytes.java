package org.jufi.lwjglutil;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

public class PBytes {

	public static byte[] byChar(char data) {
	    return new byte[] {
	        (byte) ((data >> 8) & 0xFF),
	        (byte) (data & 0xFF)
	    };
	}
	public static byte[] byShort(short data) {
	    return new byte[] {
	        (byte) ((data >> 8) & 0xFF),
	        (byte) (data & 0xFF)
	    };
	}
	public static byte[] byInt(int data) {
	    return new byte[] {
	        (byte) ((data >> 24) & 0xFF),
	        (byte) ((data >> 16) & 0xFF),
	        (byte) ((data >> 8) & 0xFF),
	        (byte) (data & 0xFF)
	    };
	}
	public static byte[] byLong(long data) {
	    return new byte[] {
	        (byte) ((data >> 56) & 0xFF),
	        (byte) ((data >> 48) & 0xFF),
	        (byte) ((data >> 40) & 0xFF),
	        (byte) ((data >> 32) & 0xFF),
	        (byte) ((data >> 24) & 0xFF),
	        (byte) ((data >> 16) & 0xFF),
	        (byte) ((data >> 8) & 0xFF),
	        (byte) (data & 0xFF)
	    };
	}
	public static byte[] byFloat(float data) {
	    return byInt(Float.floatToRawIntBits(data));
	}
	public static byte[] byDouble(double data) {
	    return byLong(Double.doubleToRawLongBits(data));
	}
	public static byte[] byFile(String path) throws IOException {
		FileInputStream fistream = new FileInputStream(path);
		ObjectInputStream oistream = new ObjectInputStream(fistream);
		ArrayList<Byte> input = new ArrayList<Byte>();
		while (true) {
			try {
				input.add(Byte.valueOf(oistream.readByte()));
			} catch (EOFException e) {
				break;
			}
		}
		oistream.close();
		byte[] inputarray = new byte[input.size()];
		for (int i = 0; i < input.size(); i++) inputarray[i] = input.get(i).byteValue();
		return inputarray;
	}
	
	public static char toChar(byte[] data) {
	    if (data == null || data.length != 2) return 0x0;
	    return (char) (
	            (0xFF & data[0]) << 8   |
	            (0xFF & data[1]) << 0
	            );
	}
	public static char toChar(byte arg0, byte arg1) {
	    return (char) (
	            (0xFF & arg0) << 8   |
	            (0xFF & arg1) << 0
	            );
	}
	public static short toShort(byte[] data) {
	    if (data == null || data.length != 2) return 0x0;
	    return (short) (
	            (0xFF & data[0]) << 8   |
	            (0xFF & data[1]) << 0
	            );
	}
	public static short toShort(byte arg0, byte arg1) {
		return (short) (
	            (0xFF & arg0) << 8   |
	            (0xFF & arg1) << 0
	            );
	}
	public static int toInt(byte[] data) {
	    if (data == null || data.length != 4) return 0x0;
	    return (int) (
	            (0xFF & data[0]) << 24  |
	            (0xFF & data[1]) << 16  |
	            (0xFF & data[2]) << 8   |
	            (0xFF & data[3]) << 0
	            );
	}
	public static int toInt(byte arg0, byte arg1, byte arg2, byte arg3) {
		return (int) (
	            (0xFF & arg0) << 24  |
	            (0xFF & arg1) << 16  |
	            (0xFF & arg2) << 8   |
	            (0xFF & arg3) << 0
	            );
	}
	public static long toLong(byte[] data) {
	    if (data == null || data.length != 8) return 0x0;
	    return (long) (
	            (long)(0xFF & data[0]) << 56  |
	            (long)(0xFF & data[1]) << 48  |
	            (long)(0xFF & data[2]) << 40  |
	            (long)(0xFF & data[3]) << 32  |
	            (long)(0xFF & data[4]) << 24  |
	            (long)(0xFF & data[5]) << 16  |
	            (long)(0xFF & data[6]) << 8   |
	            (long)(0xFF & data[7]) << 0
	            );
	}
	public static long toLong(byte arg0, byte arg1, byte arg2, byte arg3, byte arg4, byte arg5, byte arg6, byte arg7) {
		return (long) (
	            (long)(0xFF & arg0) << 56  |
	            (long)(0xFF & arg1) << 48  |
	            (long)(0xFF & arg2) << 40  |
	            (long)(0xFF & arg3) << 32  |
	            (long)(0xFF & arg4) << 24  |
	            (long)(0xFF & arg5) << 16  |
	            (long)(0xFF & arg6) << 8   |
	            (long)(0xFF & arg7) << 0
	            );
	}
	public static float toFloat(byte[] data) {
	    if (data == null || data.length != 4) return 0x0;
	    return Float.intBitsToFloat(toInt(data));
	}
	public static float toFloat(byte arg0, byte arg1, byte arg2, byte arg3) {
		return Float.intBitsToFloat(toInt(arg0, arg1, arg2, arg3));
	}
	public static double toDouble(byte[] data) {
	    if (data == null || data.length != 8) return 0x0;
	    return Double.longBitsToDouble(toLong(data));
	}
	public static double toDouble(byte arg0, byte arg1, byte arg2, byte arg3, byte arg4, byte arg5, byte arg6, byte arg7) {
		return Double.longBitsToDouble(toLong(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
	}
	public static void toFile(byte[] data, String path) throws IOException {
		FileOutputStream fostream = new FileOutputStream(path);
		ObjectOutputStream oostream = new ObjectOutputStream(fostream);
		oostream.write(data);
		oostream.close();
	}
	public static FloatBuffer toFloatBuffer(float ... data) {
		FloatBuffer b = BufferUtils.createFloatBuffer(data.length);
		for (float f : data) {
			b.put(f);
		}
		b.flip();
		return b;
	}
}
