package xyz.less.graphic.skin;

import xyz.less.graphic.view.MainView;

/**
 * 普通风格
 */
//TODO
public final class SimpleSkin extends PlayerSkin {
	public static final String NAME = "simple";
	
	public final static double PLAYLIST_WIDTH = 366;
	public final static double PLAYLIST_HEIGHT = 520;
	public final static double PLAYLIST_PADDING_X = 6;
	
	public final static double LYRIC_WIDTH = 666;
	public final static double LYRIC_HEIGHT = 150;
	public final static double LYRIC_PADDING_Y = 6;
	
	public final static double PLAYER_ICON_FIT_SIZE = 28; //25
	
	public final static double COVER_ART_FIT_SIZE = 202;
	public final static double COVER_ART_BORDERS_WIDTH = 6;
	
	public SimpleSkin() {
		super(new MainView(666, 333));
	}
	
}
