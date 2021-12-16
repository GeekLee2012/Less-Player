package xyz.less.util;

import java.io.File;

import xyz.less.bean.ConfigConstant;

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
		return isFileSupported(file, ConfigConstant.IMAGE_SUFFIXES);
	}
	
	public static boolean isLryic(File file) {
		return isFileSupported(file, ConfigConstant.LYRIC_SUFFIXES);
	}
	
	public static boolean isAudio(File file) {
		return isFileSupported(file, ConfigConstant.AUDIO_SUFFIXES);
	}
	
	public static boolean isFileSupported(File file, String[] suffixes) {
		return isFile(file) && isSuffixSupported(file.getName(), suffixes);
	}
	
	public static boolean isSuffixSupported(String name, String[] suffixes) {
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
	
}
