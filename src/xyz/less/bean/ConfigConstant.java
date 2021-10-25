package xyz.less.bean;

public final class ConfigConstant {
	public final static String UTF_8 = "UTF-8";
	public final static String ISO_8859_1 = "ISO-8859-1";
	
	//TODO
	public final static double APP_WIDTH = 666;
	public final static double APP_HEIGHT = 333;
	
	public final static double PLAYLIST_WIDTH = 366;
	public final static double PLAYLIST_HEIGHT = 520;
	public final static double PLAYLIST_PADDING_X = 6;
	
	public final static double LYRIC_WIDTH = 666;
	public final static double LYRIC_HEIGHT = 150;
	public final static double LYRIC_PADDING_Y = 6;
	
	public final static String APP_TITLE_DEFAULT_MODE = "Less-Player, Less is More !";
	public final static String DEV_MODE_PREFIX = "[开发者模式]"; 
	public final static String APP_TITLE_DEV_MODE = DEV_MODE_PREFIX + " Less-Player, More is Less ! ";
	public final static String PLAYING_PREFIX = "正在播放: ";
	public final static double PLAYER_ICON_FIT_SIZE = 25;
	public final static double INITIAL_VOLUME = 0.5;
	
	
	public final static String[] IMAGE_SUFFIXES = {".png", ".jpg", ".jpeg", ".bmp", ".gif"};
	public final static String[] LYRIC_SUFFIXES = {".lrc"};
	public final static String[] AUDIO_SUFFIXES = {".flac", ".mp3", ".mp4", ".m4a", ".wav"};

	public final static String FILE_PREFIX = "file:/";
	public final static String HTTP_PREFIX = "http://";
	public final static String HTTPS_PREFIX = "https://";
	
	public final static double COVER_ART_FIT_SIZE = 202;
	public final static double COVER_ART_BORDERS_WIDTH = 6;

	public final static String UNKOWN_AUDIO_ONLINE = "未知在线歌曲";
	public final static String UNKOWN_AUDIO = "未知歌曲";
	public final static String UNKOWN_ARTIST = "未知艺术家";
	public final static String UNKOWN_ALBUM = "<未知专辑>";
	
	public final static String DEFAULT_CURRENT_DURATION_TEXT= "00:00/00:00";
	public final static String CURRENT_DURATION_FORMAT = "%1$s / %2$s";
	
	public final static String INFINITED_TIME_KEY = "99:99.999";

}
