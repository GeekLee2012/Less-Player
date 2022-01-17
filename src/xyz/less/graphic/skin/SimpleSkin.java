package xyz.less.graphic.skin;

import java.net.URL;

import xyz.less.bean.Resources;
import xyz.less.graphic.view.MainView;

/**
 * 普通风格
 */
//TODO
public final class SimpleSkin extends PlayerSkin {
	public static final String NAME = "simple";
	//Fxml
	public final static URL MAIN_VIEW_FXML = Resources.fxml("main_view");
	public final static URL PLAYLIST_VIEW_FXML = Resources.fxml("playlist_view");
	public final static URL LYRIC_VIEW_FXML = Resources.fxml("lyric_view");
	//Style
	public final static String MAIN_VIEW_STYLE = Resources.css("main_view");
	public final static String PLAYLIST_VIEW_STYLE = Resources.css("playlist_view");
	public final static String LYRIC_VIEW_STYLE = Resources.css("lyric_view");
	//Size
	public final static double PLAYLIST_WIDTH = 366;
	public final static double PLAYLIST_HEIGHT = 520;
	public final static double PLAYLIST_PADDING_X = 6;
	public final static double PLAYLIST_ROW_WIDTH = 335;
	
	public final static double LYRIC_WIDTH = 666;
	public final static double LYRIC_HEIGHT = 150;
	public final static double LYRIC_PADDING_Y = 6;
	
	public final static double PLAYER_ICON_FIT_SIZE = 28; //25
	
	public final static double COVER_ART_FIT_SIZE = 202;
	public final static double COVER_ART_BORDERS_WIDTH = 6;
	
	public SimpleSkin() {
		super(NAME, new MainView(666, 333));
	}

}
