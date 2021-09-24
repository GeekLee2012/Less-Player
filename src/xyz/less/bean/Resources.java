package xyz.less.bean;

import java.net.URL;

import javafx.scene.image.Image;
import xyz.less.engine.ResourcesEngine;

public final class Resources {
	public final static String FXML_SUFFIX = ".fxml";
	public final static String STYLE_SUFFIX = ".css";
	
	public final static String FXML_SRC = "/resources/fxml/";
	public final static String STYLE_SRC = "/resources/style/";
	public final static String IMG_SRC = "/resources/images/";
	public final static String AUDIO_SRC = "/resources/audio/";
	
	public static final class Fxmls {
		public final static URL MAIN_VIEW = ResourcesEngine.getFxml("main_view");
		public final static URL PLAYLIST_VIEW = ResourcesEngine.getFxml("playlist_view");
	}
	
	public static final class Styles {
		public final static String MAIN_VIEW = ResourcesEngine.getStyle("main_view");
		public final static String PLAYLIST_VIEW = ResourcesEngine.getStyle("playlist_view");
	}
	
	public static final class Images {
		public final static Image LOGO = ResourcesEngine.getImage("logo.png");
		public final static Image MIN = ResourcesEngine.getImage("min_16px.png");
		public final static Image CLOSE = ResourcesEngine.getImage("close_16px.png");
		public final static Image PLAY_PREV = ResourcesEngine.getImage("music_play_prev_32px.png");
		public final static Image PLAY_NEXT = ResourcesEngine.getImage("music_play_next_32px.png");
		public final static Image DEFAULT_COVER_ART = ResourcesEngine.getImage("cover_longplay_vinyl.png");
		public final static Image DEFAULT_COVER_ART_2 = ResourcesEngine.getImage("cover_guitar.jpg");
//		public final static Image VOLUME_TRACK = ResourcesEngine.getImage("music_volume_track.png");
		public final static Image DND_NOT_FOUND = ResourcesEngine.getImage("404_not_found.png");

		public final static Image[] PIN = { 
				ResourcesEngine.getImage("pin_16px.png"), 
				ResourcesEngine.getImage("pin_on_16px.png") 
			};
		
		public final static Image[] REPEAT = { 
				ResourcesEngine.getImage("music_repeat_32px.png"), 
				ResourcesEngine.getImage("music_repeat_on_32px.png"),
				ResourcesEngine.getImage("music_repeat_self_32px.png") 
			};
		
		public final static Image[] PLAY = { 
				ResourcesEngine.getImage("music_play_32px.png"), 
				ResourcesEngine.getImage("music_pause_32px.png") 
			};
		
		public final static Image[] SHUFFLE = { 
				ResourcesEngine.getImage("music_shuffle_32px.png"), 
				ResourcesEngine.getImage("music_shuffle_on_32px.png") 
			};
		
		public final static Image[] PLAYLIST = { 
				ResourcesEngine.getImage("music_playlist_32px.png"), 
				ResourcesEngine.getImage("music_playlist_on_32px.png") 
			};
		
		public final static Image[] VOLUME = { 
				ResourcesEngine.getImage("music_volume_32px.png"), 
				ResourcesEngine.getImage("music_volume_low_32px.png"),
				ResourcesEngine.getImage("music_volume_mute_32px.png")
			};
		
		public final static Image[] ATTACH = { 
				ResourcesEngine.getImage("attach_16px.png"), 
				ResourcesEngine.getImage("attach_on_16px.png") 
			};
		
		public final static Image[] TARGET = {
				ResourcesEngine.getImage("target_16px.png"),
				ResourcesEngine.getImage("target_on_16px.png")
			};
	}
}
