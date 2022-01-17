package xyz.less.util;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import xyz.less.bean.Resources;

public final class FileUtil {
	
	public static boolean exists(File file) {
		return file != null && file.exists();
	}
	
	public static boolean isFile(File file) {
		return exists(file) && file.isFile();
	}
	
	public static boolean isDirectory(File file) {
		return exists(file) && file.isDirectory();
	}
	
	public static boolean isImage(File file) {
		return isFileSupported(file, Resources.IMAGE_SUFFIXES);
	}
	
	public static boolean isLryic(File file) {
		return isFileSupported(file, Resources.LYRIC_SUFFIXES);
	}
	
	public static boolean isJar(File file) {
		return isFileSupported(file, Resources.JAR);
	}
	
	public static String toExternalForm(File file) {
		try {
			return file.toURI().toURL().toExternalForm();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isFileSupported(File file, String... suffixes) {
		return isFile(file) && isSuffixSupported(file.getName(), suffixes);
	}
	
	public static boolean isSuffixSupported(String name, String... suffixes) {
		if(suffixes == null) {
			return true;
		}
		for(String suffix : suffixes) {
			if(name.toLowerCase().endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	public static File toFile(String uri) {
		return Paths.get(URI.create(uri)).toFile();
	}
	
}
