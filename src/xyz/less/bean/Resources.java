package xyz.less.bean;

import java.io.File;
import java.net.URL;

import javafx.scene.image.Image;
import xyz.less.engine.ResourcesEngine;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

//TODO
public final class Resources {
	public final static String FXML = ".fxml";
	public final static String CSS = ".css";
	public static final String JAR = ".jar";
	
	public final static String PNG = ".png";
	public final static String JPG = ".jpg";
	
	public final static String FLAC = ".flac";
	public final static String OGG = ".ogg";
	public final static String AAC = ".aac";
	
	public final static String[] IMAGE_SUFFIXES = {PNG, JPG, ".jpeg", ".bmp", ".gif"};
	public final static String[] LYRIC_SUFFIXES = {".lrc"};

	public final static String FILE_PREFIX = "file:/";
	public final static String FILE_PREFIX_2 = "file:///";
	public final static String HTTP_PREFIX = "http://";
	public final static String HTTPS_PREFIX = "https://";
	
	public final static String FXML_SRC = "/resources/fxml/";
	public final static String STYLE_SRC = "/resources/style/";
	public final static String IMG_SRC = "/resources/images/";
	 
	public static final class Images {
		public final static Image LOGO = png("logo");
		public final static Image MIN = png("min_16px");
		public final static Image MIN_SKIN = png("mini_skin_16px");
		public final static Image CLOSE = png("close_16px");
		public final static Image PLAY_PREV = png("music_play_prev_32px");
		public final static Image PLAY_NEXT = png("music_play_next_32px");
		public final static Image DEFAULT_COVER_ART = png("longplay_vinyl");
		public final static Image DND_NOT_FOUND = png("404_not_found");

		public final static Image[] PIN = { 
				png("pin_16px"), 
				png("pin_on_16px") 
			};
		
		public final static Image[] SPECTRUM = { 
				png("spectrum_32px"), 
				png("spectrum_on_32px") 
			};
		
		public final static Image[] LYRIC = { 
				png("text_32px"), 
				png("text_on_32px") 
			};
		
		public final static Image[] REPEAT = { 
				png("music_repeat_32px"), 
				png("music_repeat_on_32px"),
				png("music_repeat_self_32px") 
			};
		
		public final static Image[] PLAY = { 
				png("music_play_32px"), 
				png("music_pause_32px") 
			};
		
		public final static Image[] SHUFFLE = { 
				png("music_shuffle_32px"), 
				png("music_shuffle_on_32px") 
			};
		
		public final static Image[] PLAYLIST = { 
				png("music_playlist_32px"), 
				png("music_playlist_on_32px") 
			};
		
		public final static Image[] VOLUME = { 
				png("music_volume_32px"), 
				png("music_volume_low_32px"),
				png("music_volume_mute_32px")
			};
		
		public final static Image[] ATTACH = { 
				png("attach_16px"), 
				png("attach_on_16px") 
			};
		
		public final static Image[] TARGET = {
				png("target_16px"),
				png("target_on_16px")
			};
		
		public final static Image[] LOCK = {
				png("unlock_16px"),
				png("lock_16px")
			};
		
		public final static Image[] PLAY_MODE = { 
				png("music_repeat_32px"), 
				png("music_repeat_on_32px"),
				png("music_repeat_self_32px"),
				png("music_shuffle_on_32px") 
			};
	}
	
	public static URL fxml(String name) {
		return ResourcesEngine.getFxml(name);
	}
	
	public static String css(String name) {
		return ResourcesEngine.getStyle(name);
	}
	
	public static Image image(String name) {
		return ResourcesEngine.getImage(name);
	}
	
	private static Image png(String name) {
		return image(name + PNG);
	}
	
	public static boolean isFlac(Audio audio) {
		return isMatchSuffix(audio, FLAC);
	}
	
	public static boolean isOgg(Audio audio) {
		return isMatchSuffix(audio, OGG);
	}
	
	public static boolean isAac(Audio audio) {
		return isMatchSuffix(audio, AAC);
	}
	
	public static boolean isImage(File file) {
		return FileUtil.isFileSupported(file, IMAGE_SUFFIXES);
	}
	
	public static boolean isLryic(File file) {
		return FileUtil.isFileSupported(file, LYRIC_SUFFIXES);
	}
	
	public static boolean isJar(File file) {
		return FileUtil.isFileSupported(file, JAR);
	}
	
	private static boolean isMatchSuffix(Audio audio, String suffix) {
		return audio != null && isMatchSuffix(audio.getSource(), suffix);
	}
	
	private static boolean isMatchSuffix(String name, String suffix) {
		if(StringUtil.isBlank(name)) {
			return false;
		}
		return StringUtil.trim(name).toLowerCase().endsWith(suffix);
	}
}
