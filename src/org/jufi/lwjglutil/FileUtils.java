package org.jufi.lwjglutil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static void extractZIP(File archive, File destDir) throws IOException {
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		
		ZipFile zipFile = new ZipFile(archive);
		Enumeration<?> entries = zipFile.entries();
		
		byte[] buffer = new byte[16384];
		int len;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			
			String entryFileName = entry.getName();
			
			File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			if (!entry.isDirectory()) {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(new File(destDir, entryFileName)));
				
				BufferedInputStream bis = new BufferedInputStream(zipFile
						.getInputStream(entry));
				
				while ((len = bis.read(buffer)) > 0) {
					bos.write(buffer, 0, len);
				}
				
				bos.flush();
				bos.close();
				bis.close();
			}
		}
				zipFile.close();
	}
	private static File buildDirectoryHierarchyFor(String entryName, File destDir) {
		int lastIndex = entryName.lastIndexOf('/');
		String internalPathToEntry = entryName.substring(0, lastIndex + 1);
		return new File(destDir, internalPathToEntry);
	}
	
	public static void zipDirectory(File directory, String destination) throws FileNotFoundException, IOException {
		ArrayList<File> fileList = new ArrayList<File>();
		getAllFiles(directory, fileList);
		writeZipFile(directory, fileList, destination);
	}
	private static void getAllFiles(File dir, ArrayList<File> fileList) throws IOException {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) getAllFiles(file, fileList);
		}
	}
	
	private static void writeZipFile(File directoryToZip, ArrayList<File> fileList, String zippath) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(zippath);
		ZipOutputStream zos = new ZipOutputStream(fos);
		
		for (File file : fileList) {
			if (!file.isDirectory()) { // we only zip files, not directories
				addToZip(directoryToZip, file, zos);
			}
		}
		
		zos.close();
		fos.close();
	}
	
	private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		
		FileInputStream fis = new FileInputStream(file);
		
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);
		
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		
		zos.closeEntry();
		fis.close();
	}
	
	public static void copyJARRes(String source, String target, Class<?> executingClass) throws IOException {
		InputStream stream = executingClass.getResourceAsStream(source);
		if (stream == null) {
			throw new IOException("Could not create InputStream");
		}
		OutputStream resStreamOut;
		int readBytes;
		byte[] buffer = new byte[4096];
		resStreamOut = new FileOutputStream(new File(target));
		while ((readBytes = stream.read(buffer)) > 0) {
			resStreamOut.write(buffer, 0, readBytes);
		}
		stream.close();
		resStreamOut.close();
	}
	public static void copyJARRess(String srcdir, String dstdir, Class<?> executingClass, String ... names) throws IOException {
		for (String s : names) {
			copyJARRes(srcdir + s, dstdir + s, executingClass);
		}
	}
}
