package xyz.less.bean;

import java.net.URL;

import javafx.scene.image.Image;
import xyz.less.engine.ResourcesEngine;
import xyz.less.util.StringUtil;

//TODO
public final class Resources {
	public final static String FXML_SUFFIX = ".fxml";
	public final static String STYLE_SUFFIX = ".css";
	public final static String PNG = ".png";
	public final static String JPG = ".jpg";
	public final static String FLAC = ".flac";
	public final static String OGG = ".ogg";
	public final static String AAC = ".aac";
	
	public final static String[] IMAGE_SUFFIXES = {PNG, JPG, ".jpeg", ".bmp", ".gif"};
	public final static String[] LYRIC_SUFFIXES = {".lrc"};
//	public final static String[] AUDIO_SUFFIXES = {".mp3", ".wav", FLAC, AAC, ".m4a", ".mp4"};

	public final static String FILE_PREFIX = "file:/";
	public final static String FILE_PREFIX_2 = "file:///";
	public final static String HTTP_PREFIX = "http://";
	public final static String HTTPS_PREFIX = "https://";
	
	public final static String FXML_SRC = "/resources/fxml/";
	public final static String STYLE_SRC = "/resources/style/";
	public final static String IMG_SRC = "/resources/images/";
	
	public static final class Fxmls {
		public final static URL MAIN_VIEW = fxml("main_view");
		public final static URL PLAYLIST_VIEW = fxml("playlist_view");
		public final static URL LYRIC_VIEW = fxml("lyric_view");
		
		public final static URL MINI_MAIN_VIEW = fxml("mini/main_view");
	}
	
	public static final class Styles {
		public final static String MAIN_VIEW = css("main_view");
		public final static String PLAYLIST_VIEW = css("playlist_view");
		public final static String LYRIC_VIEW = css("lyric_view");
		
		public final static String MINI_MAIN_VIEW = css("mini/main_view");
	}
	
	public static final class Images {
		public final static Image LOGO = png("logo");
		public final static Image MIN = png("min_16px");
		public final static Image CLOSE = png("close_16px");
		public final static Image PLAY_PREV = png("music_play_prev_32px");
		public final static Image PLAY_NEXT = png("music_play_next_32px");
		public final static Image DEFAULT_COVER_ART = png("cover_longplay_vinyl");
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
