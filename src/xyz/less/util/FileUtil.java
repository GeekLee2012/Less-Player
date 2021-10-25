package xyz.less.util;

import java.io.File;

import xyz.less.bean.ConfigConstant;

public class FileUtil {
	
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
		return isFile(file) && isSupported(file.getName(), ConfigConstant.IMAGE_SUFFIXES);
	}
	
	public static boolean isLryic(File file) {
		return isFile(file) && isSupported(file.getName(), ConfigConstant.LYRIC_SUFFIXES);
	}
	
	public static boolean isAudio(File file) {
		return isFile(file) && isSupported(file.getName(), ConfigConstant.AUDIO_SUFFIXES);
	}
	
	public static boolean isSupported(String name, String[] suffixes) {
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
