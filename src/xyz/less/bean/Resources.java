package xyz.less.bean;

import java.net.URL;

import javafx.scene.image.Image;
import xyz.less.engine.ResourcesEngine;

//TODO
public final class Resources {
	public final static String FXML_SUFFIX = ".fxml";
	public final static String STYLE_SUFFIX = ".css";
	
	public final static String FXML_SRC = "/resources/fxml/";
	public final static String STYLE_SRC = "/resources/style/";
	public final static String IMG_SRC = "/resources/images/";
	public final static String AUDIO_SRC = "/resources/audio/";
	
	public static final class Fxmls {
		public final static URL MAIN_VIEW = fxml("main_view");
		public final static URL PLAYLIST_VIEW = fxml("playlist_view");
		public final static URL LYRIC_VIEW = fxml("lyric_view");
	}
	
	public static final class Styles {
		public final static String MAIN_VIEW = css("main_view");
		public final static String PLAYLIST_VIEW = css("playlist_view");
		public final static String LYRIC_VIEW = css("lyric_view");
	}
	
	public static final class Images {
		public final static Image LOGO = image("logo.png");
		public final static Image MIN = image("min_16px.png");
		public final static Image CLOSE = image("close_16px.png");
		public final static Image PLAY_PREV = image("music_play_prev_32px.png");
		public final static Image PLAY_NEXT = image("music_play_next_32px.png");
		public final static Image DEFAULT_COVER_ART = image("cover_longplay_vinyl.png");
		public final static Image DEFAULT_COVER_ART_2 = image("cover_guitar.jpg");
//		public final static Image VOLUME_TRACK = image("music_volume_track.png");
		public final static Image DND_WAITING = image("cover_waiting.png");
		public final static Image DND_NOT_FOUND = image("404_not_found.png");

		public final static Image[] PIN = { 
				image("pin_16px.png"), 
				image("pin_on_16px.png") 
			};
		
		public final static Image[] SPECTRUM = { 
				image("spectrum_32px.png"), 
				image("spectrum_on_32px.png") 
			};
		
		public final static Image[] LYRIC = { 
				image("text_32px.png"), 
				image("text_on_32px.png") 
			};
		
		public final static Image[] REPEAT = { 
				image("music_repeat_32px.png"), 
				image("music_repeat_on_32px.png"),
				image("music_repeat_self_32px.png") 
			};
		
		public final static Image[] PLAY = { 
				image("music_play_32px.png"), 
				image("music_pause_32px.png") 
			};
		
		public final static Image[] SHUFFLE = { 
				image("music_shuffle_32px.png"), 
				image("music_shuffle_on_32px.png") 
			};
		
		public final static Image[] PLAYLIST = { 
				image("music_playlist_32px.png"), 
				image("music_playlist_on_32px.png") 
			};
		
		public final static Image[] VOLUME = { 
				image("music_volume_32px.png"), 
				image("music_volume_low_32px.png"),
				image("music_volume_mute_32px.png")
			};
		
		public final static Image[] ATTACH = { 
				image("attach_16px.png"), 
				image("attach_on_16px.png") 
			};
		
		public final static Image[] TARGET = {
				image("target_16px.png"),
				image("target_on_16px.png")
			};
		
		public final static Image[] LOCK = {
				image("unlock_16px.png"),
				image("lock_16px.png")
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
}
